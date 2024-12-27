package src.throttlingIncomingRequests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 	Batching: This approach collects a set number of tasks (requests) into a batch and processes them together.
 * 	            This is useful when you want to limit the rate of processing or group tasks together for efficiency.
 * 	CyclicBarrier: The barrier is a synchronization mechanism that forces a group of threads to wait for each other
 * 	            before proceeding. It’s used here to wait for a specific number of requests to accumulate before
 * 	            processing them.
 * 	ExecutorService: The ExecutorService handles the concurrent processing of incoming requests.
 * 	Variations:
 * 	Time-based Batching: Instead of processing when the batch size is reached, you could also implement time-based
 * 	            batching (e.g., process requests every 1 second). This can be achieved using a ScheduledExecutorService
 * 	            to trigger the batch processing at regular intervals.
 */

public class ThrottlingWithBatchingPatternUsingCountdownLatch {
    private static final int BATCH_SIZE = 5;

    private static final List<Integer> requestBatch = Collections.synchronizedList(new ArrayList<>());

    private static final CyclicBarrier barrier = new CyclicBarrier(BATCH_SIZE, new BatchProcessor());

    private static AtomicInteger taskCounter = new AtomicInteger(0);

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        for (int i = 0; i < 15; i++) {
            simulateIncomingRequests(i);
        }

        executorService.shutdown();
    }

    private static void simulateIncomingRequests(int requestId) {
        executorService.submit(() -> {
            try {
                synchronized (requestBatch) {
                    requestBatch.add(requestId);
                }
                barrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(requestId + " was interrupted");
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static class BatchProcessor implements Runnable {

        @Override
        public void run() {
            processBatch();
        }

        private void processBatch() {
            List<Integer> batchToProcess = new ArrayList<>();
            synchronized (requestBatch) {
                batchToProcess.addAll(requestBatch);
                requestBatch.clear();
            }

            System.out.println("Processing Batch: " + batchToProcess);
            taskCounter.addAndGet(batchToProcess.size());
            System.out.println("Batch processed. Total tasks processed: " + taskCounter.get());
        }
    }
}
