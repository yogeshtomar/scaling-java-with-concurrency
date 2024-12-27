package src.scatterGather;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ScatterGatherBinarySearch {
    public static void main(String[] args) {
        int[][] dataPartitions = {
                {1, 3, 5, 7, 9},
                {11, 13, 15, 17, 19},
                {21, 23, 25, 27, 29},
                {31, 33, 35, 37, 39}
        };

        int target = 17;

        ExecutorService executorService = Executors.newFixedThreadPool(dataPartitions.length);

        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < dataPartitions.length; i++) {
            final int partitionIndex = i;
            Callable<Integer> task = () -> binarySearch(dataPartitions[partitionIndex], target, partitionIndex);
            futures.add(executorService.submit(task));
        }

        int partitionFound = -1;
        int indexFound = -1;

        for (Future<Integer> future : futures) {
            try {
                int result = future.get();
                if (result != -1) {
                    partitionFound = futures.indexOf(future);
                    indexFound = result;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        if (partitionFound != -1) {
            System.out.println("Number " + target + " found in partition " + partitionFound +
                    " at index " + indexFound + ".");
        } else {
            System.out.println("Number " + target + " not found in any partition.");
        }
    }

    private static int binarySearch(int[] array, int target, int partitionIndex) {
        int low = 0, high = array.length-1;
        while (low <= high) {
            int mid = low + (high - low)/2;
            if (array[mid] == target) {
                System.out.println("Partition " + partitionIndex + ": Found target at index " + mid);
                return mid;
            }
            else if (array[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        System.out.println("Partition " + partitionIndex + ": Target not found.");
        return -1;
    }
}
