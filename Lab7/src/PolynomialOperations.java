import java.util.Arrays;
import java.util.concurrent.*;
import mpi.*;
import java.util.ArrayList;
import java.util.List;


class PolynomialOperations {
    static int[] result;

    static void MPIMultiply(Polynomial polynomial1, Polynomial polynomial2, int begin, int end) {
        int[] poly1 = polynomial1.getCoefficients();
        int[] poly2 = polynomial2.getCoefficients();

        for (int i = begin; i < end; i++) {
            // Find all the pairs that we add to obtain the value of a result coefficient
            for (int j = 0; j < poly2.length; j++) {
                result[i + j] += poly1[i] * poly2[j];
            }
        }
    }

    static Polynomial AsynchronousKaratsubaMultiply(Polynomial p1, Polynomial p2) throws ExecutionException, InterruptedException {
        Polynomial result = new Polynomial(p1.degree + p2.degree);
        result.coefficients = AsynchronousKaratsubaMultiplyRecursive(p1.coefficients, p2.coefficients);

        return result;
    }

    private static int[] AsynchronousKaratsubaMultiplyRecursive(int[] coefficients1, int[] coefficients2) throws InterruptedException, ExecutionException {
        int[] product = new int[2 * coefficients1.length];

        //Handle the base case where the polynomial has only one coefficient
        if (coefficients1.length == 1)
        {
            product[0] = coefficients1[0] * coefficients2[0];
            return product;
        }

        int halfArraySize = coefficients1.length / 2;

        //Declare arrays to hold halved factors
        int[] coefficients1Low = new int[halfArraySize];
        int[] coefficients1High = new int[halfArraySize];
        int[] coefficients2Low = new int[halfArraySize];
        int[] coefficients2High = new int[halfArraySize];

        int[] coefficients1LowHigh = new int[halfArraySize];
        int[] coefficients2LowHigh = new int[halfArraySize];

        //Fill in the low and high arrays
        for (int halfSizeIndex = 0; halfSizeIndex < halfArraySize; halfSizeIndex++) {
            coefficients1Low[halfSizeIndex] = coefficients1[halfSizeIndex];
            coefficients1High[halfSizeIndex] = coefficients1[halfSizeIndex + halfArraySize];
            coefficients1LowHigh[halfSizeIndex] = coefficients1Low[halfSizeIndex] + coefficients1High[halfSizeIndex];

            coefficients2Low[halfSizeIndex] = coefficients2[halfSizeIndex];
            coefficients2High[halfSizeIndex] = coefficients2[halfSizeIndex + halfArraySize];
            coefficients2LowHigh[halfSizeIndex] = coefficients2Low[halfSizeIndex] + coefficients2High[halfSizeIndex];
        }

        //Recursively call method on smaller arrays and construct the low and high parts of the product
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        Callable<int[]> t1 = () -> AsynchronousKaratsubaMultiplyRecursive(coefficients1Low, coefficients2Low);
        Callable<int[]> t2 = () -> AsynchronousKaratsubaMultiplyRecursive(coefficients1High, coefficients2High);
        Callable<int[]> t3 = () ->  AsynchronousKaratsubaMultiplyRecursive(coefficients1LowHigh, coefficients2LowHigh);

        Future<int[]> f1 = executor.submit(t1);
        Future<int[]> f2 = executor.submit(t2);
        Future<int[]> f3 = executor.submit(t3);

        executor.shutdown();

        int[] productLow = f1.get();
        int[] productHigh = f2.get();
        int[] productLowHigh = f3.get();

        executor.awaitTermination(50, TimeUnit.SECONDS);

        //Construct the middle portion of the product
        int[] productMiddle = new int[coefficients1.length];
        for (int halfSizeIndex = 0; halfSizeIndex < coefficients1.length; halfSizeIndex++)
            productMiddle[halfSizeIndex] = productLowHigh[halfSizeIndex] - productLow[halfSizeIndex] - productHigh[halfSizeIndex];

        //Assemble the product from the low, middle and high parts. Start with the low and high parts of the product.
        for (int halfSizeIndex = 0, middleOffset = coefficients1.length / 2; halfSizeIndex < coefficients1.length; ++halfSizeIndex) {
            product[halfSizeIndex] += productLow[halfSizeIndex];
            product[halfSizeIndex + coefficients1.length] += productHigh[halfSizeIndex];
            product[halfSizeIndex + middleOffset] += productMiddle[halfSizeIndex];
        }

        return product;
    }

    static void MPIKaratsubaMultiply() throws ExecutionException, InterruptedException {
        Object[] results = new Object[MPI.COMM_WORLD.Size() + 1];
        Polynomial[] polynomials = new Polynomial[MPI.COMM_WORLD.Size() + 1];

        MPI.COMM_WORLD.Recv(results, 0, 1, MPI.OBJECT, 0, 0);
        polynomials[0] = (Polynomial) results[0];
        MPI.COMM_WORLD.Recv(results, 0, 1, MPI.OBJECT, 0, 0);
        polynomials[1] = (Polynomial) results[0];

        int[] coefficients1 = polynomials[0].getCoefficients();
        int[] coefficients2 = polynomials[1].getCoefficients();

        int[] product = new int[2 * coefficients1.length];

        int halfArraySize = coefficients1.length / 2;

        //Declare arrays to hold halved factors
        int[] coefficients1Low = new int[halfArraySize];
        int[] coefficients1High = new int[halfArraySize];
        int[] coefficients2Low = new int[halfArraySize];
        int[] coefficients2High = new int[halfArraySize];

        int[] coefficients1LowHigh = new int[halfArraySize];
        int[] coefficients2LowHigh = new int[halfArraySize];

        //Fill in the low and high arrays
        for (int halfSizeIndex = 0; halfSizeIndex < halfArraySize; halfSizeIndex++) {
            coefficients1Low[halfSizeIndex] = coefficients1[halfSizeIndex];
            coefficients1High[halfSizeIndex] = coefficients1[halfSizeIndex + halfArraySize];
            coefficients1LowHigh[halfSizeIndex] = coefficients1Low[halfSizeIndex] + coefficients1High[halfSizeIndex];

            coefficients2Low[halfSizeIndex] = coefficients2[halfSizeIndex];
            coefficients2High[halfSizeIndex] = coefficients2[halfSizeIndex + halfArraySize];
            coefficients2LowHigh[halfSizeIndex] = coefficients2Low[halfSizeIndex] + coefficients2High[halfSizeIndex];
        }

        //Recursively call method on smaller arrays and construct the low and high parts of the product
        int[] productLow = new int[0], productHigh = new int[0], productLowHigh = new int[0];

        productLow = AsynchronousKaratsubaMultiplyRecursive(coefficients1Low, coefficients2Low);
        productHigh = AsynchronousKaratsubaMultiplyRecursive(coefficients1High, coefficients2High);
        productLowHigh = AsynchronousKaratsubaMultiplyRecursive(coefficients1LowHigh, coefficients2LowHigh);

        //Construct the middle portion of the product
        int[] productMiddle = new int[coefficients1.length];
        for (int halfSizeIndex = 0; halfSizeIndex < coefficients1.length; halfSizeIndex++) {
            productMiddle[halfSizeIndex] = productLowHigh[halfSizeIndex] - productLow[halfSizeIndex] - productHigh[halfSizeIndex];
        }

        //Assemble the product from the low, middle and high parts. Start with the low and high parts of the product.
        for (int halfSizeIndex = 0, middleOffset = coefficients1.length / 2; halfSizeIndex < coefficients1.length; ++halfSizeIndex) {
            product[halfSizeIndex] += productLow[halfSizeIndex];
            product[halfSizeIndex + coefficients1.length] += productHigh[halfSizeIndex];
            product[halfSizeIndex + middleOffset] += productMiddle[halfSizeIndex];
        }

        Object[] resultObj = new Object[1];
        Polynomial polynomial = new Polynomial(product);
        resultObj[0] = polynomial;

        MPI.COMM_WORLD.Issend(resultObj, 0, 1, MPI.OBJECT, 0, 0);
    }
}
