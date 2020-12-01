// Given a directed graph, find a Hamiltonian cycle, if one exists. Use multiple threads to parallelize the search.

import java.io.FileNotFoundException;


public class Main {

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

        // PRINTING SOLUTIONS
        if (!controllerB.isHasSolution()) {
            System.out.println("Hamiltonian cycle does not exists");
        } else {
            System.out.println("Hamiltonian cycle is: " + controllerB.getSolution());
        }

        if (!controllerT.isHasSolution()) {
            System.out.println("Hamiltonian cycle does not exists");
        } else {
            System.out.println("Hamiltonian cycle is: " + controllerT.getSolution());
        }

        System.out.println("\nPerformance for simple backtracking: " + (endTime1 - startTime1) + " ms");
        System.out.println("Performance for thread backtracking: " + (endTime2 - startTime2) + " ms");
    }
}
