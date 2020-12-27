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
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10); //TODO: hardcoded
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
        int from = 0;
        int[] coefficients1 = new int[0];
        int[] coefficients2 = new int[0];
        int[] sendTo = new int[0];

        //TODO
        MPI.COMM_WORLD.Recv(from, 0, 1, MPI.INT, MPI.ANY_SOURCE, 0);
        MPI.COMM_WORLD.Recv(coefficients1, 0, 1, MPI.INT, MPI.ANY_SOURCE, 0);
        MPI.COMM_WORLD.Recv(coefficients2, 0, 1, MPI.INT, MPI.ANY_SOURCE, 0);
        MPI.COMM_WORLD.Recv(sendTo, 0, 1, MPI.INT, MPI.ANY_SOURCE, 0);

        int[] product = new int[2 * coefficients1.length];

        //Handle the base case where the polynomial has only one coefficient
        if (coefficients1.length == 1) {
            product[0] = coefficients1[0] * coefficients2[0];

            //TODO
            MPI.COMM_WORLD.Send(product, 1, product.length, MPI.INT, from, 0);
            return;
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
        int[] productLow = new int[0], productHigh = new int[0], productLowHigh = new int[0];

        if (sendTo.length == 0) {
            productLow = AsynchronousKaratsubaMultiplyRecursive(coefficients1Low, coefficients2Low);
            productHigh = AsynchronousKaratsubaMultiplyRecursive(coefficients1High, coefficients2High);
            productLowHigh = AsynchronousKaratsubaMultiplyRecursive(coefficients1LowHigh, coefficients2LowHigh);
        }
        else if (sendTo.length == 1) {
            int[] mul = new int[0];
            //TODO
            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients1Low, 1, coefficients1Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients2Low, 1, coefficients2Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(mul, 1, mul.length, MPI.INT, sendTo[0], 0);

            productHigh = AsynchronousKaratsubaMultiplyRecursive(coefficients1High, coefficients2High);
            productLowHigh = AsynchronousKaratsubaMultiplyRecursive(coefficients1LowHigh, coefficients2LowHigh);

            MPI.COMM_WORLD.Recv(productLow, 1, 32, MPI.INT, sendTo[0], 0);
        }
        else if (sendTo.length == 2)
        {
            int[] mul = new int[0];
            //TODO
            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients1Low, 1, coefficients1Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients2Low, 1, coefficients2Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(mul, 1, mul.length, MPI.INT, sendTo[0], 0);

            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(coefficients1High, 1, coefficients1High.length, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(coefficients2High, 1, coefficients2High.length, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(mul, 1, mul.length, MPI.INT, sendTo[1], 0);

            productLowHigh = AsynchronousKaratsubaMultiplyRecursive(coefficients1LowHigh, coefficients2LowHigh);

            MPI.COMM_WORLD.Recv(productLow, 1, 32, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Recv(productHigh, 1, 32, MPI.INT, sendTo[1], 0);
        }
        else if(sendTo.length == 3)
        {
            int[] mul = new int[0];
            //TODO
            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients1Low, 1, coefficients1Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients2Low, 1, coefficients2Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(mul, 1, mul.length, MPI.INT, sendTo[0], 0);

            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(coefficients1High, 1, coefficients1High.length, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(coefficients2High, 1, coefficients2High.length, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(mul, 1, mul.length, MPI.INT, sendTo[1], 0);

            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[2], 0);
            MPI.COMM_WORLD.Send(coefficients1LowHigh, 1, coefficients1LowHigh.length, MPI.INT, sendTo[2], 0);
            MPI.COMM_WORLD.Send(coefficients2LowHigh, 1, coefficients2LowHigh.length, MPI.INT, sendTo[2], 0);
            MPI.COMM_WORLD.Send(mul, 1, mul.length, MPI.INT, sendTo[2], 0);

            MPI.COMM_WORLD.Recv(productLow, 1, 32, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Recv(productHigh, 1, 32, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Recv(productLowHigh, 1, 32, MPI.INT, sendTo[2], 0);
        }
        else
        {
            List<Integer> auxSendTo = new ArrayList<>();
            for (int i = 3; i < sendTo.length; i++) {
                auxSendTo.add(sendTo[i]);
            }
            int auxLength = auxSendTo.size() / 3;

            //TODO
            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients1Low, 1, coefficients1Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(coefficients2Low, 1, coefficients2Low.length, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Send(auxSendTo.get(auxLength), 1, 32, MPI.INT, sendTo[0], 0);

            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(coefficients1High, 1, coefficients1High.length, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Send(coefficients2High, 1, coefficients2High.length, MPI.INT, sendTo[1], 0);
            List<Integer> auxSendTo2 = new ArrayList<>();
            for (int i = auxLength; i < auxSendTo.size(); i++) {
                auxSendTo2.add(auxSendTo.get(i));
            }
            MPI.COMM_WORLD.Send(auxSendTo2.get(auxLength), 1, 32, MPI.INT, sendTo[1], 0);

            MPI.COMM_WORLD.Send(MPI.COMM_WORLD.Rank(), 1, 32, MPI.INT, sendTo[2], 0);
            MPI.COMM_WORLD.Send(coefficients1LowHigh, 1, coefficients1LowHigh.length, MPI.INT, sendTo[2], 0);
            MPI.COMM_WORLD.Send(coefficients2LowHigh, 1, coefficients2LowHigh.length, MPI.INT, sendTo[2], 0);
            List<Integer> auxSendTo3= new ArrayList<>();
            for (int i = 2 * auxLength; i < auxSendTo.size(); i++) {
                auxSendTo3.add(auxSendTo.get(i));
            }
            MPI.COMM_WORLD.Send(auxSendTo3, 1, 32, MPI.INT, sendTo[2], 0);

            MPI.COMM_WORLD.Recv(productLow, 1, 32, MPI.INT, sendTo[0], 0);
            MPI.COMM_WORLD.Recv(productHigh, 1, 32, MPI.INT, sendTo[1], 0);
            MPI.COMM_WORLD.Recv(productLowHigh, 1, 32, MPI.INT, sendTo[2], 0);
        }

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

        //TODO
        MPI.COMM_WORLD.Send(product, 1, product.length, MPI.INT, from, 0);
    }
}
