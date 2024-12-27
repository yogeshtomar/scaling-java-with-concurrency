package src.scatterGather;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ScatterGatherWeatherExample {
    public static void main(String[] args) {
        List<String> cities = List.of("New York", "London", "Tokyo", "Sydney", "Mumbai");

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        List<Future<String>> futures = new ArrayList<>();
        for (String city : cities) {
            Callable<String> task = () -> getWeatherForCity(city);
            futures.add(executorService.submit(task));
        }

        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        System.out.println("Weather Information:");
        results.forEach(System.out::println);
    }

    private static String getWeatherForCity(String city) throws InterruptedException {
        // Simulate API Delay for network call
        Thread.sleep((long) (Math.random() * 2000));
        return "Weather in " + city + ": " + (20 + (int) (Math.random() * 10)) + "°C";
    }
}
