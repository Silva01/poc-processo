package silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.FileResult;

import java.util.List;

public interface FileDataRepository extends MongoRepository<FileResult, String> {

    List<FileResult> findAllByListId(String listId);
}
