package src.dependencyChoreography;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class DependencyChoreographyWithForkJoinPool {
    public static void main(String[] args) {
        System.out.println("Starting Dependency Choreography...");

        ForkJoinPool pool = new ForkJoinPool();

        Task taskB = new Task("Task B", 1000);

        Task taskC = new Task("Task C", 3000);

        Task taskD = new Task("Task D", 1500);

        TaskA taskA = new TaskA("Task A", taskB, taskC, taskD);

        pool.invoke(taskA);

        System.out.println("All tasks completed");
        pool.shutdown();
    }

    static class Task extends RecursiveTask<Void> {
        private final String taskName;
        private final long workDuration;

        public Task(String taskName, long workDuration) {
            this.taskName = taskName;
            this.workDuration = workDuration;
        }

        @Override
        protected Void compute() {
            System.out.println(taskName + " is running...");
            simulateWork(workDuration);
            System.out.println(taskName + " completed.");
            return null;
        }

        private void simulateWork(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(taskName + " was interrupted");
            }
        }
    }

    static class TaskA extends RecursiveTask<Void> {
        private final String taskName;
        private final Task taskB;
        private final Task taskC;
        private final Task taskD;

        public TaskA(String taskName, Task taskB, Task taskC, Task taskD) {
            this.taskName = taskName;
            this.taskB = taskB;
            this.taskC = taskC;
            this.taskD = taskD;
        }

        @Override
        protected Void compute() {
            System.out.println(taskName + " is waiting for dependencies to complete...");
            invokeAll(taskB, taskC, taskD);
            System.out.println(taskName + " is running after dependencies...");
            simulateWork(500);
            System.out.println(taskName + " completed");
            return null;
        }

        private void simulateWork(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(taskName + " was interrupted.");
            }
        }
    }
}
