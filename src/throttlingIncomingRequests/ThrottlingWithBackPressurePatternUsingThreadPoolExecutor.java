package src.throttlingIncomingRequests;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThrottlingWithBackPressurePatternUsingThreadPoolExecutor {
    private static final int MAX_THREADS = 4;

    private static final int QUEUE_CAPACITY = 10;

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    private static ThreadPoolExecutor executor;

    public static void main(String[] args) {
        executor = new ThreadPoolExecutor(
                MAX_THREADS,
                MAX_THREADS,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new CustomRejectedExecutionHandler()
        );

        /*
            public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)

            1. AbortPolicy (default): Throws a RejectedExecutionException when a task cannot be executed.
                new ThreadPoolExecutor.AbortPolicy();
            2. CallerRunsPolicy: Executes the task in the calling thread, providing back-pressure.
                new ThreadPoolExecutor.CallerRunsPolicy();
            3. DiscardPolicy: Silently discards the task without doing anything.
                new ThreadPoolExecutor.DiscardPolicy();
            4. DiscardOldestPolicy: Discards the oldest unprocessed task in the queue.
                new ThreadPoolExecutor.DiscardOldestPolicy();

         */

        for (int i = 0; i < 20; i++) {
            simulateIncomingRequest(i);
        }

        executor.shutdown();
    }

    private static void simulateIncomingRequest(int requestId) {
        executor.submit(() -> {
           try {
               processRequest(requestId);
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               System.err.println(requestId + " was interrupted");
           }
        });
    }

    private static void processRequest(int requestId) throws InterruptedException {
        System.out.println("Processing request: " + requestId);
        Thread.sleep(1000);
        taskCounter.incrementAndGet();
        System.out.println("Request : " + requestId + " completed. Total tasks processed: " + taskCounter.get());
    }

    static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("Task rejected: " + r);
            // You can perform additional logic here, like adding the task to a retry queue, logging, or handling failures.
            // Example: Add to a separate queue or retry later
            retryTask(r);
        }

        private void retryTask(Runnable task) {
            System.out.println("Retrying rejected task...");
            // In a real system, you might want to add it back to a different queue or retry after a delay
            // For simplicity, we're just submitting it again
            executor.submit(task);
        }
    }
}
