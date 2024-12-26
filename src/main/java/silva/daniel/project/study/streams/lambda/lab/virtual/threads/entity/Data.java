package silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class Data {

    @Field("process_id")
    private String processId;

    @Field("is_valid")
    private boolean isvalid;

    @Field("history_id")
    private String historyId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public boolean isIsvalid() {
        return isvalid;
    }

    public void setIsvalid(boolean isvalid) {
        this.isvalid = isvalid;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }
}
