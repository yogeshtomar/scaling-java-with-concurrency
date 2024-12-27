package src.scatterGather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ScatterGatherParallelSum {
    public static void main(String[] args) {
        int[] array = new int[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = i+1;
        }

        int chunkSize = 20;
        List<int[]> chunks = divideArray(array, chunkSize);
        for (int[] chunk : chunks) {
            System.out.println(Arrays.toString(chunk));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<Integer>> futuresChunksSumList = new ArrayList<>();

        for (int[] chunk : chunks) {
            Callable<Integer> task = () -> calculateSum(chunk);
            futuresChunksSumList.add(executorService.submit(task));
        }

        int totalSum = 0;
        for (Future<Integer> future : futuresChunksSumList) {
            try {
                totalSum += future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        System.out.println("Total sum : " + totalSum);
    }

    private static List<int[]> divideArray(int[] array, int chunkSize) {
        List<int[]> chunks = new ArrayList<>();
        for (int i = 0; i < array.length; i = i + chunkSize) {
            int end = i + chunkSize;
            int[] chunk = new int[chunkSize];
            System.arraycopy(array, i, chunk, 0,end - i);
            chunks.add(chunk);
        }
        return chunks;
    }

    private static int calculateSum(int[] chunk) {
        int sum = 0;
        for (int num : chunk) {
            sum += num;
        }
        System.out.println("The calculated sum for chunk: " + Arrays.toString(chunk) + " is: \n" + sum);
        return sum;
    }
}
