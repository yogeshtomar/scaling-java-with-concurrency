package src.dependencyChoreography;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyChoreographyWithCountDownLatch {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(3);
        AtomicInteger sharedCounter = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Runnable taskA = () -> {
            try {
                System.out.println("Task A is waiting for dependencies to complete...");
                latch.await();
                System.out.println("Task A has started after dependencies are satisfied...");
                System.out.println("Shared Counter : " + sharedCounter.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Task A was interrupted");
            }
        };

        Runnable taskB = () -> {
            try {
                System.out.println("Task B is running...");
                Thread.sleep(1000);
                sharedCounter.incrementAndGet();
                System.out.println("Task B completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Task B was interrupted.");
            } finally {
                latch.countDown();
            }
        };

        Runnable taskC = () -> {
            try {
                System.out.println("Task C is running...");
                Thread.sleep(3000);
                sharedCounter.incrementAndGet();
                System.out.println("Task C completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Task C was interrupted");
            } finally {
                latch.countDown();
            }
        };

        Runnable taskD = () -> {
            try {
                System.out.println("Task D is running...");
                Thread.sleep(1500);
                sharedCounter.incrementAndGet();
                System.out.println("Task D completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Task D was interrupted");
            } finally {
                latch.countDown();
            }
        };

        executorService.submit(taskA);
        executorService.submit(taskB);
        executorService.submit(taskC);
        executorService.submit(taskD);

        executorService.shutdown();
    }
}
