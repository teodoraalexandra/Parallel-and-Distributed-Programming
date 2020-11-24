// The goal of this lab is to implement a simple but non-trivial parallel algorithm.
// Perform the multiplication of 2 polynomials

// (1) O(n2) algorithm - sequential form - Complexity: O(n^2)
// (2) The Karatsuba algorithm - sequential form - Complexity: O(n^log3)
// (3) O(n2) algorithm - parallelize form - Complexity: O(n^2)
// (4) The Karatsuba algorithm - parallelize form - Complexity: O(n^log3)

import controller.MultiplyPolynomials;

import java.util.Random;
import java.util.concurrent.ExecutionException;


public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int NUMBER_OF_THREADS = 10; // Only for (3) O(n2) algorithm - parallelize form
        int DEGREE = 64;

        // HARDCODED
        // The following array represents
        // polynomial 5 + 10x^2 + 6x^3
        //int[] A = {5, 0, 10, 6};

        // The following array represents
        // polynomial 1 + 2x + 4x^2 + 5x^3
        //int[] B = {1, 2, 4, 5};

        // RANDOM
        int[] A = new int[DEGREE];
        int[] B = new int[DEGREE];

        int min = 1;
        int max = 100;

        Random random = new Random();
        for (int i = 0; i < DEGREE; i++) {
            A[i] = random.nextInt(max - min) + min;
            B[i] = random.nextInt(max - min) + min;
        }

        int m = A.length;
        int n = B.length;

        MultiplyPolynomials multiplyPolynomials = new MultiplyPolynomials();

        System.out.println("First polynomial is:");
        multiplyPolynomials.printPoly(A, m);
        System.out.println("\n\nSecond polynomial is:");
        multiplyPolynomials.printPoly(B, n);

        long startTime1 = System.currentTimeMillis();
        naiveSequential(A, B);
        long endTime1 = System.currentTimeMillis();

        long startTime2 = System.currentTimeMillis();
        karatsubaSequential(A, B);
        long endTime2 = System.currentTimeMillis();

        long startTime3 = System.currentTimeMillis();
        naiveThreads(A, B, NUMBER_OF_THREADS);
        long endTime3 = System.currentTimeMillis();

        long startTime4 = System.currentTimeMillis();
        karatsubaThreads(A, B);
        long endTime4 = System.currentTimeMillis();

        System.out.println("\n\nPerformance for polynomials with " + DEGREE + " coefficients: ");
        System.out.println("(1) O(n2) algorithm - sequential form: " + (endTime1 - startTime1) + " ms");
        System.out.println("(2) The Karatsuba algorithm - sequential form: " + (endTime2 - startTime2) + " ms");
        System.out.println("(3) O(n2) algorithm - parallelize form: " + (endTime3 - startTime3) + " ms");
        System.out.println("(4) The Karatsuba algorithm - parallelize form: " + (endTime4 - startTime4) + " ms");
    }

    private static void naiveSequential(int[] A, int[] B) {
        MultiplyPolynomials multiplyPolynomials = new MultiplyPolynomials();

        // (1) O(n2) algorithm - sequential form
        int[] prod_1 = multiplyPolynomials.multiply_1(A, B);

        System.out.println("\n\nAfter O(n2) algorithm - sequential form ---> Product polynomial is:");
        multiplyPolynomials.printPoly(prod_1, A.length + B.length - 1);
        System.out.println("\n");
    }

    private static void karatsubaSequential(int[] A, int[] B) {
        MultiplyPolynomials multiplyPolynomials = new MultiplyPolynomials();

        // (2) The Karatsuba algorithm - sequential form
        int[] prod_2 = multiplyPolynomials.multiply_2(A, B);

        System.out.println("\n\nAfter the Karatsuba algorithm - sequential form ---> Product polynomial is:");
        multiplyPolynomials.printPoly(prod_2, A.length + B.length - 1);
        System.out.println("\n");
    }

    private static void naiveThreads(int[] A, int[] B, int NUMBER_OF_THREADS) throws InterruptedException {
        MultiplyPolynomials multiplyPolynomials = new MultiplyPolynomials();

        // (3) O(n2) algorithm - parallelize form
        int[] prod_3 = multiplyPolynomials.multiply_3(A, B, NUMBER_OF_THREADS);

        System.out.println("\n\nAfter O(n2) algorithm - threads ---> Product polynomial is:");
        multiplyPolynomials.printPoly(prod_3, A.length + B.length - 1);
        System.out.println("\n");
    }

    private static void karatsubaThreads(int[] A, int[] B) throws ExecutionException, InterruptedException {
        MultiplyPolynomials multiplyPolynomials = new MultiplyPolynomials();

        // (4) The Karatsuba algorithm - parallelize form
        int[] prod_4 = multiplyPolynomials.multiply_4(A, B);

        System.out.println("\n\nAfter the Karatsuba algorithm - threads ---> Product polynomial is:");
        multiplyPolynomials.printPoly(prod_4, A.length + B.length - 1);
        System.out.println("\n");
    }
}
