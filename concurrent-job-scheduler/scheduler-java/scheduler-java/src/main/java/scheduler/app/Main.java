package scheduler.app;

import scheduler.core.Scheduler;
import scheduler.job.Job;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Scheduler scheduler = new Scheduler(3);

        for (int i = 1; i <= 8; i++) {
            scheduler.submitJob(new Job("Job-" + i, 2000));
        }

        scheduler.shutdown();
        System.out.println("Scheduler shut down");
    }
}
