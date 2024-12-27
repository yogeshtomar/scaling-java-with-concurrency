package src.scatterGather;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ScatterGatherWithConcurrentHashMap {
    public static void main(String[] args) {
        int[] numbers = new int[100];
        for (int i = 0; i < 100; i++) {
            numbers[i] = i+1;
        }

        int numberOfTasks = 4;

        ConcurrentHashMap<Integer, Integer> partialResults = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfTasks);

        AtomicInteger completedTasks = new AtomicInteger(0);

        int chunkSize = (int) Math.ceil((double) numbers.length / numberOfTasks);

        for (int i = 0; i < numberOfTasks; i++) {
            int taskId = i;
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, numbers.length);

            executorService.submit(() -> {
                int sum = 0;
                for (int j = start; j < end; j++) {
                    sum += numbers[j];
                }

                partialResults.put(taskId, sum);
                if (completedTasks.incrementAndGet() == numberOfTasks) {
                    System.out.println("All tasks completed. Gathering results...");
                    int totalSum = partialResults.values().stream().mapToInt(Integer::intValue).sum();
                    System.out.println("Total sum: " + totalSum);

                    executorService.shutdown();
                }
            });
        }

        System.out.println("Main thread is free to do other tasks...");
    }
}
