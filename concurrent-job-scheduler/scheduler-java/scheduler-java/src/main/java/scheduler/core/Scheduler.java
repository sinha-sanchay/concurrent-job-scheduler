package scheduler.core;

import scheduler.job.Job;
import scheduler.job.Job.Status;
import scheduler.worker.JobWorker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private final BlockingQueue<Job> jobQueue;
    private final Semaphore executionSlots;
    private final ExecutorService workerPool;

    private final int maxConcurrentJobs;
    private volatile boolean acceptingJobs = true;

    public Scheduler(int maxConcurrentJobs) {
        this.maxConcurrentJobs = maxConcurrentJobs;
        this.jobQueue = new LinkedBlockingQueue<>();
        this.executionSlots = new Semaphore(maxConcurrentJobs);
        this.workerPool = Executors.newFixedThreadPool(maxConcurrentJobs);
    }

    // Entry point for submitting jobs
    public void submitJob(Job job) {
        if (!acceptingJobs) {
            throw new IllegalStateException("Scheduler is shutting down, cannot accept new jobs");
        }

        job.setStatus(Status.QUEUED);
        try {
            jobQueue.put(job);
            dispatchJobs();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Dispatch jobs when resources are available
    private void dispatchJobs() {
        while (!jobQueue.isEmpty() && executionSlots.tryAcquire()) {
            Job job = jobQueue.poll();
            if (job != null) {
                workerPool.submit(() -> {
                    try {
                        new JobWorker(job).run();
                    } finally {
                        executionSlots.release();
                        dispatchJobs(); // trigger next job
                    }
                });
            } else {
                executionSlots.release();
            }
        }
    }

    // Graceful shutdown: drain queue, finish running jobs, then stop
    public void shutdown() {
        acceptingJobs = false;

        // Wait until queue is empty AND no jobs are running
        while (!jobQueue.isEmpty() ||
                executionSlots.availablePermits() != maxConcurrentJobs) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        workerPool.shutdown();
        try {
            workerPool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
