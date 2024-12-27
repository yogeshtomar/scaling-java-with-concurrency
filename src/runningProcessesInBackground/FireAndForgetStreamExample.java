package src.runningProcessesInBackground;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FireAndForgetStreamExample {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        InputStream inputStream = System.in;

        executorService.submit(() -> {
           try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
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
               System.out.println("Error reading from stream: " + e.getMessage());
           }
        });

        System.out.println("Main thread is free to do other tasks");
        executorService.shutdown();
    }
}
