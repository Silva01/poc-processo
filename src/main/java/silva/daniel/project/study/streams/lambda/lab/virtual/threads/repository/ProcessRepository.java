package silva.daniel.project.study.streams.lambda.lab.virtual.threads.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.dto.GroupedProcess;
import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.Process;

import java.util.Collection;
import java.util.List;

public interface ProcessRepository extends MongoRepository<Process, String> {

    List<Process> findAllByHistory_Id(String historyId);

    @Query("{ 'history.$id' : { $in: ?0 } }")
    List<Process> findAllByHistoryIdIn(Collection<ObjectId> historyIds);

    @Aggregation(pipeline = {
            "{ $match: { 'history.$id': { $in: ?0 } } }",
            "{ $group: { _id: '$history.id', processes: { $push: '$$ROOT' } } }"
    })
    List<GroupedProcess> findGroupedByHistoryIds(List<ObjectId> historyIds);
}
