package silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.dto.GroupedProcess;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.History;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends MongoRepository<History, String> {
    Optional<History> findByProcessList_Id(String processListId);

    @Query("{ 'processList.$id' : { $in: ?0 } }")
    List<History> findAllByProcessListIdIn(List<ObjectId> processListIds);
}
