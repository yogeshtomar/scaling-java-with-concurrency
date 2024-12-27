package src.runningProcessesInBackground;

import java.util.concurrent.*;

public class FireAndForgetFactorialThreadedRecursion {
    private BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<>();

    public static void main(String[]  args) throws InterruptedException {
        FireAndForgetFactorialThreadedRecursion obj = new FireAndForgetFactorialThreadedRecursion();
        obj.runForkJoinPool();
        Thread.sleep(100);
    }

    public void runForkJoinPool() {
        tasks.offer(() -> factorial(8)); // add your new runnable task here
        tasks.offer(() -> factorial(6)); // add your new runnable task here
        tasks.offer(() -> factorial(11)); // add your new runnable task here


        new ForkJoinPool(4).execute(new Consumer());
    }

    private void factorial(int n) {
        int num = n;
        int result = 1;
        while(num>=2) {
            result = result * num--;
        }

        // This probably wont print as the main thread will exit out even before this gets executed.
        // To see the output of print method yo can add a sleep to main method after the initialization of ForkJoinPool
        System.out.println("Factorial of " + n + " is : " + result);
    }

    private class Consumer extends RecursiveAction {

        @Override
        protected void compute() {
            try {
                Runnable r = tasks.take(); // the take method of the queue will wait for any tasks to be added in the queue
                new Consumer().fork(); // create a new consumer which will consume the next task recursively
                r.run(); // call run directly as we are using ForkJoinPool's worker
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
