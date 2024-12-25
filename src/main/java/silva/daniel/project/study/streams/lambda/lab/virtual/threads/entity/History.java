package silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "history")
public class History {

    @Id
    private String id;

    private String name;

    @DBRef
    private ProcessList processList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessList getProcessList() {
        return processList;
    }

    public void setProcessList(ProcessList processList) {
        this.processList = processList;
    }
}
