package src.scatterGather;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ScatterGatherWithReentrantLocks {
    public static void main(String[] args) {
        int[] numbers = new int[100];
        for (int i = 0; i < 100; i++) {
            numbers[i] = i+1;
        }

        int numberOfTasks = 4;

        final int[] totalSum= {0};

        ReentrantLock lock = new ReentrantLock();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfTasks);

        int chunkSize = (int) Math.ceil((double) numbers.length / numberOfTasks);

        for (int i = 0; i < numberOfTasks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, numbers.length);

            executorService.submit(() -> {
                int partialSum = 0;
                for (int j = start; j < end; j++) {
                    partialSum += numbers[j];
                }

                lock.lock();
                try {
                    totalSum[0] += partialSum;
                } finally {
                    lock.unlock();
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Total sum: " + totalSum[0]);
    }
}
