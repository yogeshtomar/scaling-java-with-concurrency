package src.dependencyChoreography;

import src.scatterGather.ScatterGatherSumWIthAtomicInteger;

import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.ReentrantLock;

public class DependencyChoreographyWithForkJoinPool {
    public static void main(String[] args) {
        System.out.println("Starting Dependency Choreography...");

        ForkJoinPool pool = new ForkJoinPool();

        ReentrantLock lock = new ReentrantLock();
        SharedData sharedData = new SharedData();

        Task taskB = new Task("Task B", 1000, lock, sharedData);

        Task taskC = new Task("Task C", 3000, lock, sharedData);

        Task taskD = new Task("Task D", 1500, lock, sharedData);

        TaskA taskA = new TaskA("Task A", taskB, taskC, taskD, lock, sharedData);

        pool.invoke(taskA);

        System.out.println("All tasks completed");
        pool.shutdown();
    }

    static class SharedData {
        private int counter = 0;
        public void increment() {
            counter++;
        }

        public int getCounter() {
            return counter;
        }
    }

    static class Task extends RecursiveTask<Void> {
        private final String taskName;
        private final long workDuration;
        private final ReentrantLock lock;
        private final SharedData sharedData;

        public Task(String taskName, long workDuration, ReentrantLock lock, SharedData sharedData) {
            this.taskName = taskName;
            this.workDuration = workDuration;
            this.lock = lock;
            this.sharedData = sharedData;
        }

        @Override
        protected Void compute() {
            System.out.println(taskName + " is running...");
            simulateWork(workDuration);
            lock.lock();
            try {
                sharedData.increment();
            } finally {
                lock.unlock();
            }
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
        private final ReentrantLock lock;
        private final SharedData sharedData;

        public TaskA(String taskName, Task taskB, Task taskC, Task taskD, ReentrantLock lock, SharedData sharedData) {
            this.taskName = taskName;
            this.taskB = taskB;
            this.taskC = taskC;
            this.taskD = taskD;
            this.lock = lock;
            this.sharedData = sharedData;
        }

        @Override
        protected Void compute() {
            System.out.println(taskName + " is waiting for dependencies to complete...");
            invokeAll(taskB, taskC, taskD);
            System.out.println(taskName + " is running after dependencies...");
            lock.lock();
            try {
                System.out.println("Shared Counter: " + sharedData.getCounter());
            } finally {
                lock.unlock();
            }
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
