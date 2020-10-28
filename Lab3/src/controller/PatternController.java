package controller;

import model.MatrixMultiplication;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PatternController {
    private int[][] a;
    private int[][] b;
    private int size;
    private int numberOfThreads;
    private long startTime;
    private long endTime;

    public PatternController(int[][] a, int[][] b, int size, int numberOfThreads) {
        this.a = a;
        this.b = b;
        this.size = size;
        this.numberOfThreads = numberOfThreads;
        this.startTime = 0;
        this.endTime = 0;
    }

    public void lowLevelPattern1() throws InterruptedException, IOException {
        MatrixMultiplication matrixMultiplication = new MatrixMultiplication(a, b, size, numberOfThreads);
        startTime = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>();
        // Create threads
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(matrixMultiplication.pattern_1));
        }

        // Join threads
        for (Thread thread: threads) {
            thread.join();
            thread.start();
        }

        endTime = System.currentTimeMillis();
        writeToFile("LOW LEVEL PATTERN 1: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    public void lowLevelPattern2() throws InterruptedException, IOException {
        MatrixMultiplication matrixMultiplication = new MatrixMultiplication(a, b, size, numberOfThreads);
        startTime = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>();
        // Create threads
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(matrixMultiplication.pattern_2));
        }

        // Join threads
        for (Thread thread: threads) {
            thread.join();
            thread.start();
        }

        endTime = System.currentTimeMillis();
        writeToFile("LOW LEVEL PATTERN 2: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    public void lowLevelPattern3() throws InterruptedException, IOException {
        MatrixMultiplication matrixMultiplication = new MatrixMultiplication(a, b, size, numberOfThreads);
        startTime = System.currentTimeMillis();

        List<Thread> threads = new ArrayList<>();
        // Create threads
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(matrixMultiplication.pattern_3));
        }

        // Join threads
        for (Thread thread: threads) {
            thread.join();
            thread.start();
        }

        endTime = System.currentTimeMillis();
        writeToFile("LOW LEVEL PATTERN 3: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    public void threadPoolPattern1() throws IOException, InterruptedException {
        MatrixMultiplication matrixMultiplication = new MatrixMultiplication(a, b, size, numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        startTime = System.currentTimeMillis();
        executorService.execute(matrixMultiplication.pattern_1);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        endTime = System.currentTimeMillis();

        writeToFile("THREAD POOL PATTERN 1: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    public void threadPoolPattern2() throws IOException, InterruptedException {
        MatrixMultiplication matrixMultiplication = new MatrixMultiplication(a, b, size, numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        startTime = System.currentTimeMillis();
        executorService.execute(matrixMultiplication.pattern_2);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        endTime = System.currentTimeMillis();

        writeToFile("THREAD POOL PATTERN 2: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    public void threadPoolPattern3() throws IOException, InterruptedException {
        MatrixMultiplication matrixMultiplication = new MatrixMultiplication(a, b, size, numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        startTime = System.currentTimeMillis();
        executorService.execute(matrixMultiplication.pattern_3);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        endTime = System.currentTimeMillis();

        writeToFile("THREAD POOL PATTERN 3: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    private void writeToFile(String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("/Users/teodoradan/Desktop/Parallel-and-Distributed-Programming/Lab3/src/report", true));

        writer.write(text);
        writer.close();
    }
}
