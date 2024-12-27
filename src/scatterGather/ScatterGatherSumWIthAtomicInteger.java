package src.scatterGather;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ScatterGatherSumWIthAtomicInteger {
    public static void main(String[] args) {
        int[] numbers = new int[100];
        for (int i = 0; i < 100; i++) {
            numbers[i] = i+1;
        }

        int numberOfTasks = 3;

        AtomicInteger totalSum = new AtomicInteger(0);

        try (ExecutorService executorService = Executors.newFixedThreadPool(numberOfTasks)) {

            AtomicInteger completedTasks = new AtomicInteger(0);

            int chunkSize = (int) Math.ceil((double) numbers.length / numberOfTasks);

            for (int i = 0; i < numberOfTasks; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, numbers.length);

                executorService.submit(() -> {
                    int sum = 0;
                    for (int j = start; j < end; j++) {
                        sum += numbers[j];
                    }
                    totalSum.addAndGet(sum);

                    if (completedTasks.incrementAndGet() == numberOfTasks) {
                        System.out.println("All tasks completed.");
                        System.out.println("Total sum: " + totalSum.get());
                        executorService.shutdown();
                    }
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Main thread is free to do other tasks...");
    }
}
