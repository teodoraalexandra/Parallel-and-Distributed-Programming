// Given a directed graph, find a Hamiltonian cycle, if one exists. Use multiple threads to parallelize the search.

import java.io.FileNotFoundException;
import java.util.Random;


public class Main {

    private static int[][] generate(int n, int m) {
        int[][] adj = new int[n][n];
        Random random = new Random();
        while (m > 0) {
            int x = Math.abs(random.nextInt()) % n;
            int y = Math.abs(random.nextInt()) % n;
            if (x != y && adj[x][y] == 0) {
                adj[x][y] = 1;
                adj[y][x] = 1;
                m --;
            }
        }
        return adj;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        String fileName = "/Users/teodoradan/Desktop/Parallel-and-Distributed-Programming/Lab6/src/graph1k";

        // BACKTRACKING
        Graph graphB = new Graph(fileName);
        graphB.readGraph();

        Controller controllerB = new Controller(graphB);

        long startTime1 = System.currentTimeMillis();
        controllerB.backTracking(controllerB.getSolution(), 0, graphB.getNumberOfVertices());
        long endTime1 = System.currentTimeMillis();

        // THREADS
        Graph graphT = new Graph(fileName);
        graphT.readGraph();

        Controller controllerT = new Controller(graphT);

        long startTime2 = System.currentTimeMillis();
        controllerT.threadBackTracking(controllerT.getSolution(), 0, graphB.getNumberOfVertices());
        long endTime2 = System.currentTimeMillis();

        // RANDOM
        int n = 10;
        int m = 45;
        Graph graph = new Graph(n, m, generate(n, m));
        Controller controllerRandomB = new Controller(graph);
        Controller controllerRandomT = new Controller(graph);

        long startTime3 = System.currentTimeMillis();
        controllerRandomB.backTracking(controllerRandomB.getSolution(), 0, graph.getNumberOfVertices());
        long endTime3 = System.currentTimeMillis();

        long startTime4 = System.currentTimeMillis();
        controllerRandomT.threadBackTracking(controllerRandomT.getSolution(), 0, graph.getNumberOfVertices());
        long endTime4 = System.currentTimeMillis();

        // PRINTING SOLUTIONS
        if (!controllerB.isHasSolution()) {
            System.out.println("Hamiltonian cycle does not exists");
        } else {
            System.out.println("Hamiltonian cycle is: " + controllerB.printSolution());
        }

        if (!controllerT.isHasSolution()) {
            System.out.println("Hamiltonian cycle does not exists");
        } else {
            System.out.println("Hamiltonian cycle is: " + controllerT.printSolution());
        }

        if (!controllerRandomB.isHasSolution()) {
            System.out.println("Hamiltonian cycle does not exists");
        } else {
            System.out.println("Hamiltonian cycle is: " + controllerRandomB.printSolution());
        }

        if (!controllerRandomT.isHasSolution()) {
            System.out.println("Hamiltonian cycle does not exists");
        } else {
            System.out.println("Hamiltonian cycle is: " + controllerRandomT.printSolution());
        }

        System.out.println("\nPerformance for simple backtracking: " + (endTime1 - startTime1) + " ms");
        System.out.println("Performance for thread backtracking: " + (endTime2 - startTime2) + " ms");
        System.out.println("Performance for simple backtracking: " + (endTime3 - startTime3) + " ms");
        System.out.println("Performance for thread backtracking: " + (endTime4 - startTime4) + " ms");
    }
}
