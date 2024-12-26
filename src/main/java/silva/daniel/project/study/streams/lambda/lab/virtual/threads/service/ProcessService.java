package silva.daniel.project.study.streams.lambda.lab.virtual.threads.service;

import com.github.javafaker.Faker;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.dto.GroupedProcess;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.Data;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.FileResult;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.History;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.Process;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.ProcessList;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository.FileDataRepository;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository.HistoryRepository;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository.ProcessListRepository;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository.ProcessRepository;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class ProcessService {

    private final ProcessListRepository processListRepository;
    private final ProcessRepository processRepository;
    private final HistoryRepository historyRepository;
    private final FileDataRepository fileDataRepository;
    private final ProcessRepositoryService processRepositoryService;

    private Logger log = LoggerFactory.getLogger(ProcessService.class);

    public ProcessService(ProcessListRepository processListRepository, ProcessRepository processRepository, HistoryRepository historyRepository, FileDataRepository fileDataRepository, ProcessRepositoryService processRepositoryService) {
        this.processListRepository = processListRepository;
        this.processRepository = processRepository;
        this.historyRepository = historyRepository;
        this.fileDataRepository = fileDataRepository;
        this.processRepositoryService = processRepositoryService;
    }

    public void generateProcess(int qtd) {
        log.info("Generating process...");
        final var faker = new Faker();
        final var processes = new ArrayList<Process>();
        final var molde = new ArrayList<Data>();

        final var processList = new ProcessList();
        processList.setListName(faker.name().fullName());

        processListRepository.save(processList);

        final var history = new History();
        history.setName(faker.name().fullName());
        history.setProcessList(processListRepository.save(processList));

        final var historySave = historyRepository.save(history);

        for (int i = 0; i < qtd; i++) {
            log.info("Generating process {}...", i);
            final var process = new Process();
            process.setValid(faker.bool().bool());
            process.setHistory(historySave);

            final var newProcess = processRepository.save(process);

            processes.add(newProcess);

            final var data = new Data();
            data.setProcessId(newProcess.getId());
            data.setIsvalid(newProcess.isValid());
            data.setHistoryId(newProcess.getHistory().getId());

            molde.add(data);
        }

        final var fileData = new FileResult();
        fileData.setListId(processList.getId());
        fileData.setData(molde);

        fileDataRepository.save(fileData);
    }

    public void generateCsvData() throws IOException {
        StringWriter writer = new StringWriter();
        writer.write("id,is_valid,history_id\n");
        log.info("Generating CSV data...");
        processListRepository.findAll().forEach(processList -> {

            Optional<History> history = historyRepository.findByProcessList_Id(processList.getId());

            if (history.isEmpty()) {
                return;
            }

            List<Process> processes = processRepository.findAllByHistory_Id(history.get().getId());
            processes.forEach(p -> writer.write(p.getId() + "," + p.isValid() + "," + p.getHistory().getId() + "\n"));
        });

        log.info("Gerando ToString dos dados");
        String csvData = writer.toString();
        writer.close();

        // Mostrar no console
        log.info("CSV Gerado:");

        // Escrever o CSV em um arquivo
        Path outputPath = Path.of("tmp/%s.csv".formatted(UUID.randomUUID().toString()));
        Files.writeString(outputPath, csvData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        log.info("Arquivo CSV salvo em: {}", outputPath.toAbsolutePath());
    }

    public void generateCsvDataWithTemplate() throws IOException {
        StringWriter writer = new StringWriter();
        writer.write("id,is_valid,history_id\n");
        log.info("Generating CSV data...");
        processListRepository.findAll().forEach(processList -> fileDataRepository.findAllByListId(processList.getId()).parallelStream().forEach(fileResult -> fileResult.getData().forEach(data -> {
            writer.write(data.getProcessId() + "," + data.isIsvalid() + "," + data.getHistoryId() + "\n");
        })));

        log.info("Gerando ToString dos dados");
        String csvData = writer.toString();
        writer.close();

        // Mostrar no console
        log.info("CSV Gerado:");

        // Escrever o CSV em um arquivo
        Path outputPath = Path.of("tmp/%s.csv".formatted(UUID.randomUUID().toString()));
        Files.writeString(outputPath, csvData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        log.info("Arquivo CSV salvo em: {}", outputPath.toAbsolutePath());
    }


    public void generateCsvVirtualData() {
        try {
            // Usar StringBuilder para a saída do CSV
            StringBuilder writer = new StringBuilder();
            writer.append("id,is_valid,history_id\n");
            log.info("Generating CSV virtual data...");

            log.info("Obtendo todas as ProcessList IDs");
            // Obter todas as ProcessList IDs
            List<ObjectId> processListIds = processListRepository.findAll()
                    .stream()
                    .map(p -> new ObjectId(p.getId()))
                    .collect(Collectors.toList());

            log.info("Obtendo todas as entidades de History e Process em lote");
            // Buscar todas as entidades de History e Process em lote
            Map<String, History> historyMap = historyRepository.findAllByProcessListIdIn(processListIds).stream()
                    .collect(Collectors.toMap(h -> h.getProcessList().getId(), history -> history));

            log.info("Obtendo todas as entidades de Process em lote");
            List<ObjectId> historyObjectIds = historyMap.values().stream()
                    .map(h -> new ObjectId(h.getId()))
                    .toList();

            log.info("Gerando CSV de Forma paralela");
            final var virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
            StringBuilder finalBuffer = new StringBuilder();

            try {
                log.info("Obtendo dados agrupados de Process");
                final var groupedProcesses = processRepositoryService.getData(historyObjectIds);
                log.info("Dados agrupados obtidos: {}", groupedProcesses.size());
                // Cria tarefas para cada GroupedProcess
                List<Callable<String>> tasks = groupedProcesses.stream()
                        .map(groupedProcess -> (Callable<String>) () -> {
                            StringBuilder localBuffer = new StringBuilder();
                            for (Process p : groupedProcess.getProcesses()) {
                                localBuffer.append(p.getId())
                                        .append(",")
                                        .append(p.isValid())
                                        .append(",")
                                        .append(p.getHistory().getId())
                                        .append("\n");
                            }
                            return localBuffer.toString();
                        })
                        .toList();

                // Executa todas as tarefas em virtual threads
                List<Future<String>> results = virtualExecutor.invokeAll(tasks);

                // Coleta os resultados e escreve no buffer final
                for (Future<String> result : results) {
                    finalBuffer.append(result.get());
                }

                // Escreve os dados no Writer
                writer.append(finalBuffer);

            } catch (InterruptedException | ExecutionException e) {
                log.error("Erro ao processar os dados: {}", e.getMessage());
            } finally {
                virtualExecutor.shutdown();
            }

            // Gerar o CSV final
            log.info("Gerando ToString dos dados");
            String csvData = writer.toString();

            // Escrever o CSV em um arquivo
            Path outputPath = Path.of("tmp/%s.csv".formatted(UUID.randomUUID().toString()));
            Files.writeString(outputPath, csvData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Arquivo CSV salvo em: {}", outputPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Error generating virtual data: {}", e.getMessage());
        }
    }

    public static <T> List<List<T>> partition(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return chunks;
    }
}
