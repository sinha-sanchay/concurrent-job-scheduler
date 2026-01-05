package scheduler.worker;

import scheduler.job.Job;
import scheduler.job.Job.Status;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JobWorker implements Runnable {

    private static final String WORKER_PATH =
            "/mnt/d/projects/concurrent-job-scheduler/worker-cpp/worker";

    private final Job job;

    public JobWorker(Job job) {
        this.job = job;
    }

    @Override
    public void run() {
        Process process = null;
        try {
            job.setStatus(Status.RUNNING);
            System.out.println("[RUNNING] " + job.getJobName());

            ProcessBuilder builder = new ProcessBuilder(
                    "wsl",
                    "/mnt/d/projects/concurrent-job-scheduler/worker-cpp/worker",
                    job.getJobName(),
                    String.valueOf(job.getExecutionTimeMs())
            );

            builder.redirectErrorStream(true);
            process = builder.start();

            // Read C++ worker output
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                job.setStatus(Status.COMPLETED);
                System.out.println("[COMPLETED] " + job.getJobName());
            } else {
                job.setStatus(Status.FAILED);
                System.out.println("[FAILED] " + job.getJobName());
            }

        } catch (Exception e) {
            job.setStatus(Status.FAILED);
            System.out.println("[FAILED] " + job.getJobName());
        }
    }
}
