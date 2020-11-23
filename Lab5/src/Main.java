// The goal of this lab is to implement a simple but non-trivial parallel algorithm.
// Perform the multiplication of 2 polynomials

// (1) O(n2) algorithm - sequential form - Complexity: O(n^2)
// (2) The Karatsuba algorithm - sequential form - Complexity: O(n^log3)
// (3) O(n2) algorithm - parallelize form
// (4) The Karatsuba algorithm - parallelize form

import controller.MultiplyPolynomials;


public class Main {

    public static void main(String[] args) {
        // The following array represents
        // polynomial 5 + 10x^2 + 6x^3
        int[] A = {5, 0, 10, 6};

        // The following array represents
        // polynomial 1 + 2x + 4x^2 + 5x^3
        int[] B = {1, 2, 4, 5};
        int m = A.length;
        int n = B.length;

        MultiplyPolynomials multiplyPolynomials = new MultiplyPolynomials();

        System.out.println("First polynomial is:");
        multiplyPolynomials.printPoly(A, m);
        System.out.println("\n\nSecond polynomial is:");
        multiplyPolynomials.printPoly(B, n);

        // (1) O(n2) algorithm - sequential form
        int[] prod_1 = multiplyPolynomials.multiply_1(A, B, m, n);

        System.out.println("\n\nAfter O(n2) algorithm - sequential form ---> Product polynomial is:");
        multiplyPolynomials.printPoly(prod_1, m + n - 1);
        System.out.println("\n");

        // (2) The Karatsuba algorithm - sequential form
        int[] prod_2 = multiplyPolynomials.multiply_2(A, B);

        System.out.println("\n\nAfter the Karatsuba algorithm - sequential form ---> Product polynomial is:");
        multiplyPolynomials.printPoly(prod_2, m + n - 1);
        System.out.println("\n");
    }
}
