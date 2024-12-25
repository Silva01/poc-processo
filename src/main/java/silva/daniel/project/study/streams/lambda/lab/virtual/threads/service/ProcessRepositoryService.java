package silva.daniel.project.study.streams.lambda.lab.virtual.threads.service;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.dto.GroupedProcess;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository.ProcessRepository;

import java.util.List;

@Service
public class ProcessRepositoryService {

    private final ProcessRepository processRepository;

    public ProcessRepositoryService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Cacheable(value = "processCache", key = "#historyObjectIds")
    public List<GroupedProcess> getData(List<ObjectId> historyObjectIds) {
        return processRepository.findGroupedByHistoryIds(historyObjectIds);
    }

}
