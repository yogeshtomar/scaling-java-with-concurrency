package src.runningProcessesInBackground;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FireAndForgetWithThreadPoolExecutor {
    public static void main(String[] args) {
        // Create a ThreadPoolExecutor
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1, // Core pool size
                1, // Maximum pool size
                60, // Keep-alive time
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        // Example: Reading from System.in as a stream
        InputStream inputStream = System.in;

        // Fire-and-forget task to read and print data from the stream
        threadPoolExecutor.execute(() -> {
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
                // Shut down the executor after the task is complete
                threadPoolExecutor.shutdown();
                try {
                    if (!threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("Forcing shutdown...");
                        threadPoolExecutor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    threadPoolExecutor.shutdownNow();
                }
            }
        });

        // Main thread continues execution
        System.out.println("Main thread is free to do other tasks...");
    }
}
