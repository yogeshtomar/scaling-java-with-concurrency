package src.runningProcessesInBackground;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FireAndForgetStreamWithForkJoinPool {
    public static void main(String[] args) {
        // Create a ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // Example: Reading from System.in as a stream
        InputStream inputStream = System.in;

        // Atomic flag to track task completion
        AtomicBoolean isRunning = new AtomicBoolean(true);

        // Fire-and-forget task to read and process data from the stream
        forkJoinPool.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                System.out.println("Start typing (type 'exit' to quit):");
                while ((line = reader.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        System.out.println("Exiting...");
                        break;
                    }
                    System.out.println("Read: " + line);
                }
            } catch (Exception e) {
                System.err.println("Error reading from stream: " + e.getMessage());
            } finally {
                // Mark task as completed
                isRunning.set(false);
            }
        });

        // Main thread waits until the task completes
        System.out.println("Main thread is free to perform other tasks...");
        while (isRunning.get()) {
            try {
                Thread.sleep(100); // Wait briefly to avoid busy-waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted");
            }
        }

        // Shutdown the ForkJoinPool gracefully
        forkJoinPool.shutdown();
        try {
            if (!forkJoinPool.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Forcing ForkJoinPool shutdown...");
                forkJoinPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            forkJoinPool.shutdownNow();
        }

        System.out.println("Application terminated.");
    }
}
