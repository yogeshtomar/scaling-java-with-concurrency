package src.throttlingIncomingRequests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

public class ThrottlingWithPhaser {
    private static final int MAX_CONCURRENT_REQUESTS = 3;

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    private static final Phaser phaser = new Phaser(MAX_CONCURRENT_REQUESTS);

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        for (int i = 0; i < 15; i++) {
            simulateIncomingRequest(i);
        }
    }

    private static void simulateIncomingRequest(int requestId) {
        executorService.submit(() -> {
           try {
               phaser.arriveAndAwaitAdvance();

               processRequest(requestId);

               phaser.arriveAndDeregister();
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               System.err.println(requestId + " was interrupter");
           }
        });
    }

    private static void processRequest(int requestId) throws InterruptedException {
        System.out.println("Processing request: " + requestId);
        Thread.sleep(1000);
        taskCounter.incrementAndGet();
        System.out.println("Request " + requestId + " completed. Total Tasks processed: " + taskCounter.get());
    }
}
