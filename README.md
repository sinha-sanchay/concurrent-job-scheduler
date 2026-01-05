# Concurrent Job Scheduler

A system-level project that implements controlled concurrent execution of jobs using Java concurrency primitives and a C++ execution engine.

This project focuses on operating system concepts such as multithreading, synchronization, resource control, and graceful shutdown rather than UI or application frameworks.

The system accepts multiple jobs concurrently, queues excess jobs safely, and executes only a fixed number of jobs in parallel using a counting semaphore. Job execution is delegated to an external C++ worker process, while the Java scheduler manages job lifecycle and concurrency.

Architecture:
- Java scheduler manages job submission, queuing, concurrency control, and lifecycle.
- A thread-safe blocking queue buffers jobs.
- A counting semaphore limits parallel execution.
- Jobs are executed by a C++ worker process launched via Java.
- The scheduler ensures all jobs complete before shutdown.

Technologies and Concepts:
- Java (ExecutorService, Semaphore, BlockingQueue)
- C++ (process execution)
- Multithreading and synchronization
- Producer–Consumer pattern
- Process management
- Graceful shutdown
- Java–C++ interoperability using WSL

Project Structure:
- scheduler-java : Java scheduler and concurrency control
- worker-cpp     : C++ execution engine
- docs           : Design notes

How to Run:
1. Compile the C++ worker in `worker-cpp` using `g++ worker.cpp -o worker`
2. Run the `Main` class from `scheduler-java`

Notes:
- Maximum concurrent jobs are configurable.
- Job execution order is non-deterministic due to concurrency.
- The system guarantees controlled parallelism and completion of all submitted jobs.

Author: Sanchay Sinha
