package silva.daniel.project.study.streams.lambda.lab.virtual.threads.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.controller.request.CreateRequest;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.service.ProcessService;

import java.io.IOException;

@RestController
@RequestMapping("/api/")
public class Controller {

    private final ProcessService service;

    public Controller(ProcessService service) {
        this.service = service;
    }

    @PostMapping("create/data")
    public void createData(@RequestBody CreateRequest request) {
        service.generateProcess(request.qtd());
    }

    @GetMapping("generate/csv/data")
    public void generateCsvData() throws IOException {
        service.generateCsvData();
    }

    @GetMapping("generate/csv/data/template")
    public void generateCsvDataTemplate() throws IOException {
        service.generateCsvDataWithTemplate();
    }

    @GetMapping("generate/csv/virtual/data")
    public void generateCsvVirtualData() throws IOException {
        service.generateCsvVirtualData();
    }
}
