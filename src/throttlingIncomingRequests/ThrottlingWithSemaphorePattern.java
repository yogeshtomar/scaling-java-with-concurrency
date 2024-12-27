package src.throttlingIncomingRequests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThrottlingWithSemaphorePattern {
    private static final int MAX_CONCURRENT_REQUESTS = 3;

    private static final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_REQUESTS);

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    /*
        Processing request 0
        Processing request 1
        Processing request 3
        Request 3 completed. Total tasks processed: 2
        Request 1 completed. Total tasks processed: 3
        Processing request 2
        Request 0 completed. Total tasks processed: 2
        Processing request 4
        Processing request 5
        Request 9 is throttled due to too many concurrent requests.
        Request 7 is throttled due to too many concurrent requests.
        Request 8 is throttled due to too many concurrent requests.
        Request 6 is throttled due to too many concurrent requests.
        Request 5 completed. Total tasks processed: 6
        Request 2 completed. Total tasks processed: 6
        Request 4 completed. Total tasks processed: 6

        Process finished with exit code 0
     */

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            simulateIncomingRequest(i);
        }

        executor.shutdown();
    }

    private static void simulateIncomingRequest(int requestId) {
        executor.submit(() -> {
            try {
                /*
                    throttling with Rejecting Tasks:
                    Instead of blocking the request when the semaphore is exhausted, you can reject the task or apply
                    some custom logic in the rejection handler. This could be done by using tryAcquire() with a timeout,
                    as demonstrated.
                    If you want the request to be blocked until a permit becomes available, you can replace tryAcquire()
                    with acquire(). This will cause the thread to block indefinitely until a permit is available.

                 */
                if (semaphore.tryAcquire(1500, TimeUnit.MILLISECONDS)) {
                    processRequest(requestId);
                } else {
                    System.out.println("Request " + requestId + " is throttled due to too many concurrent requests.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private static void processRequest(int requestId) throws InterruptedException {
        // simulate processing time
        System.out.println("Processing request " + requestId);
        Thread.sleep(1000);
        taskCounter.incrementAndGet();
        System.out.println("Request " + requestId + " completed. Total tasks processed: " + taskCounter.get());

        semaphore.release();
    }
}
