package src.dependencyChoreography;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyChoreographyWithContinuationPassing {
    public static void main(String[] args) {
        AtomicInteger sharedCounter = new AtomicInteger(0);     // For handling Thread Contention

        System.out.println("Starting Dependency Choreography...");

        CompletableFuture<Void> taskB = CompletableFuture.runAsync(() -> {
            System.out.println("Task B is running...");
            simulateWork(1000);
            sharedCounter.incrementAndGet();
            System.out.println("Task B completed.");
        });

        CompletableFuture<Void> taskC = CompletableFuture.runAsync(() -> {
            System.out.println("Task C is running...");
            simulateWork(3000);
            sharedCounter.incrementAndGet();
            System.out.println("Task C completed.");
        });

        CompletableFuture<Void> taskD = CompletableFuture.runAsync(() -> {
            System.out.println("Task D is running...");
            simulateWork(1500);
            sharedCounter.incrementAndGet();
            System.out.println("Task D completed.");
        });

        CompletableFuture<Void> taskA = CompletableFuture
                .allOf(taskB, taskC, taskD)
                .thenRun(() -> {
                    System.out.println("Task A is running after dependencies...");
                    System.out.println("Shared Counter: " + sharedCounter.get());
                    simulateWork(500);
                    System.out.println("Task A is completed");
                });
        taskA.join();
        System.out.println("All tasks completed.");
    }

    private static void simulateWork(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Work Simulation interrupted.");
        }
    }
}
