package controller;

import model.MatrixMultiplication;
import java.util.ArrayList;
import java.util.List;


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

    public void lowLevelPattern1() throws InterruptedException {
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
        System.out.println("LOW LEVEL PATTERN 1: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    public void lowLevelPattern2() throws InterruptedException {
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
        System.out.println("LOW LEVEL PATTERN 2: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }

    public void lowLevelPattern3() throws InterruptedException {
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
        System.out.println("LOW LEVEL PATTERN 3: " + (endTime - startTime) +
                "; SIZE: " + size + "; THREADS: " + numberOfThreads + "\n");

        // "reset" timer
        startTime = 0;
        endTime = 0;
    }
}
