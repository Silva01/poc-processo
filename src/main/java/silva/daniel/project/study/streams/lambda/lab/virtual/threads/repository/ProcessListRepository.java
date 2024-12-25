package silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.ProcessList;

public interface ProcessListRepository extends MongoRepository<ProcessList, String> {
}
