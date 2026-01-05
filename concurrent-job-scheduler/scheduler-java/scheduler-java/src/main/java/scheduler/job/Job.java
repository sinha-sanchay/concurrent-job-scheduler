package scheduler.job;

import java.util.UUID;

public class Job {

    public enum Status {
        SUBMITTED,
        QUEUED,
        RUNNING,
        COMPLETED,
        FAILED
    }

    private final String jobId;
    private final String jobName;
    private final int executionTimeMs;

    private volatile Status status;

    public Job(String jobName, int executionTimeMs) {
        this.jobId = UUID.randomUUID().toString();
        this.jobName = jobName;
        this.executionTimeMs = executionTimeMs;
        this.status = Status.SUBMITTED;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public int getExecutionTimeMs() {
        return executionTimeMs;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
