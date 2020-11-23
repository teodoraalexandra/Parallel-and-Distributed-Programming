package controller;


public class MultiplyPolynomials {
    // (1) O(n2) algorithm - sequential form
    public int[] multiply_1(int[] A, int[] B, int m, int n) {
        // Create a product array prod[] of size m + n - 1
        int[] product = new int[m + n - 1];

        // Initialize all entries in prod[] as 0
        for (int i = 0; i < m + n - 1; i++) {
            product[i] = 0;
        }

        // Multiply two polynomials term by term
        // Take ever term of first polynomial
        for (int i = 0; i < m; i++) {
            // Multiply the current term of first polynomial
            // with every term of second polynomial.
            for (int j = 0; j < n; j++) {
                product[i + j] += A[i] * B[j];
            }
        }

        return product;
    }

    // (2) The Karatsuba algorithm - sequential form
    public int[] multiply_2(int[] A, int[] B) {

        int[] product = new int[2 * A.length];

        //Handle the base case where the polynomial has only one coefficient
        if (A.length == 1) {
            product[0] = A[0] * B[0];
            return product;
        }

        int halfArraySize = A.length / 2;

        //Declare arrays to hold halved factors
        int[] ALow = new int[halfArraySize];
        int[] AHigh = new int[halfArraySize];
        int[] BLow = new int[halfArraySize];
        int[] BHigh = new int[halfArraySize];

        int[] ALowHigh = new int[halfArraySize];
        int[] BLowHigh = new int[halfArraySize];

        //Fill in the low and high arrays
        for (int halfSizeIndex = 0; halfSizeIndex < halfArraySize; ++halfSizeIndex) {

            ALow[halfSizeIndex] = A[halfSizeIndex];
            AHigh[halfSizeIndex] = A[halfSizeIndex + halfArraySize];
            ALowHigh[halfSizeIndex] = ALow[halfSizeIndex] + AHigh[halfSizeIndex];

            BLow[halfSizeIndex] = B[halfSizeIndex];
            BHigh[halfSizeIndex] = B[halfSizeIndex + halfArraySize];
            BLowHigh[halfSizeIndex] = BLow[halfSizeIndex] + BHigh[halfSizeIndex];

        }

        //Recursively call method on smaller arrays and construct the low and high parts of the product
        int[] productLow = multiply_2(ALow, BLow);
        int[] productHigh = multiply_2(AHigh, BHigh);

        int[] productLowHigh = multiply_2(ALowHigh, BLowHigh);

        //Construct the middle portion of the product
        int[] productMiddle = new int[A.length];
        for (int halfSizeIndex = 0; halfSizeIndex < A.length; ++halfSizeIndex) {
            productMiddle[halfSizeIndex] = productLowHigh[halfSizeIndex] - productLow[halfSizeIndex] - productHigh[halfSizeIndex];
        }

        //Assemble the product from the low, middle and high parts. Start with the low and high parts of the product.
        for (int halfSizeIndex = 0, middleOffset = A.length / 2; halfSizeIndex < A.length; ++halfSizeIndex) {
            product[halfSizeIndex] += productLow[halfSizeIndex];
            product[halfSizeIndex + A.length] += productHigh[halfSizeIndex];
            product[halfSizeIndex + middleOffset] += productMiddle[halfSizeIndex];
        }

        return product;
    }

    // A utility function to print a polynomial
    public void printPoly(int[] poly, int n)
    {
        for (int i = 0; i < n; i++) {
            System.out.print(poly[i]);
            if (i != 0) {
                System.out.print("x^" + i);
            }
            if (i != n - 1) {
                System.out.print(" + ");
            }
        }
    }
}
