package silva.daniel.project.study.streams.lambda.lab.virtual.threads.dto;

import silva.daniel.project.study.streams.lambda.lab.virtual.threads.entity.Process;

import java.util.List;

public class GroupedProcess {
    private List<Process> processes;

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }
}
