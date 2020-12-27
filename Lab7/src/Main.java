import mpi.MPI;

import java.util.concurrent.ExecutionException;


public class Main {

    private static Polynomial ComputeFinalResult(Polynomial[] results) {
        Polynomial result = new Polynomial(results[0].coefficients.length * 2 - 1);

        // Multiply two polynomials term by term
        // Take ever term of first polynomial
        for (int i = 0; i < results[0].coefficients.length; i++) {
            // Multiply the current term of first polynomial
            // with every term of second polynomial.
            for (int j = 0; j < results[1].coefficients.length; j++) {
                result.coefficients[i + j] += results[0].coefficients[i] * results[1].coefficients[j];
            }
        }

        return result;
    }

    private static synchronized void MPIMultiplicationMaster(Object polynomial1, Object polynomial2, int size) {
        int n = MPI.COMM_WORLD.Size();
        int begin = 0;
        int end = 0;
        int length = size / n;

        for (int i = 0; i < n; i++) {
            begin = end;
            end = end + length;
            if (i == n - 1)
                end = size;

            int[] beginArray = new int[1];
            int[] endArray = new int[1];
            beginArray[0] = begin;
            endArray[0] = end;

            Object[] poly1 = new Object[1];
            Object[] poly2 = new Object[1];
            poly1[0] = polynomial1;
            poly2[0] = polynomial2;

            //MPI.COMM_WORLD.Issend(beginArray, 0, 1, MPI.INT, i, 0);
            //MPI.COMM_WORLD.Issend(endArray, 0, 1, MPI.INT, i, 0);
            MPI.COMM_WORLD.Issend(poly1, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Issend(poly2, 0, 1, MPI.OBJECT, i, 0);
        }

        Object[] results = new Object[n + 1];
        Polynomial[] polynomials = new Polynomial[n + 1];

        for (int i = 0; i < n; i++) {
            MPI.COMM_WORLD.Recv(results, 0, 1, MPI.OBJECT, i, 0);
            polynomials[i] = (Polynomial) results[i];
            MPI.COMM_WORLD.Recv(results, 0, 1, MPI.OBJECT, i, 0);
            polynomials[i + 1] = (Polynomial) results[i];
        }

        Polynomial result = ComputeFinalResult(polynomials);
        System.out.println("MPI Multiplication: " + result.toString());
    }

    private static void MPIMultiplicationWorker()
    {
        Polynomial polynomial1 = new Polynomial(), polynomial2 = new Polynomial();
        int begin = 0, end = 0;
        Object[] poly1Array = new Object[5];
        Object[] poly2Array = new Object[5];
        poly1Array[0] = (Object) polynomial1;
        poly2Array[0] = polynomial2;

        //TODO
        MPI.COMM_WORLD.Recv(poly1Array, 4, poly1Array.length, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(poly2Array[0], 1, 32, MPI.OBJECT, 0, 0);

        int[] beginArray = new int[5];
        int[] endArray = new int[5];
        beginArray[0] = begin;
        endArray[0] = end;

        //TODO
        MPI.COMM_WORLD.Recv(beginArray, 1, 32, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(endArray, 1, 32, MPI.INT, 0, 0);

        Polynomial result = PolynomialOperations.MPIMultiply(polynomial1, polynomial2, begin, end);

        MPI.COMM_WORLD.Send(result, 1, result.coefficients.length, MPI.INT, 0, 0);
    }

    private static void MPIKaratsubaMaster(Polynomial polynomial1, Polynomial polynomial2, int size) throws ExecutionException, InterruptedException {
        Polynomial result = new Polynomial(polynomial1.degree * 2);

        if (MPI.COMM_WORLD.Size() == 1) {
            result = PolynomialOperations.AsynchronousKaratsubaMultiply(polynomial1, polynomial2);
        }
        else {
            //TODO
            MPI.COMM_WORLD.Send(0, 1, 1, MPI.INT, 1, 0);
            MPI.COMM_WORLD.Send(polynomial1.getCoefficients(), 1, polynomial1.getCoefficients().length, MPI.INT, 1, 0);
            MPI.COMM_WORLD.Send(polynomial2.getCoefficients(), 1, polynomial2.getCoefficients().length, MPI.INT, 1, 0);

            /*if (MPI.COMM_WORLD.Size() == 2)
                MPI.COMM_WORLD.Send(mul, 1, 32, MPI.INT, 1, 0);
            else
                MPI.COMM_WORLD.Send(mul2, 1, 32, MPI.INT, 1, 0);

            MPI.COMM_WORLD.Recv(coefs, 1, 32, MPI.INT, 1, 0);
            result.coefficients = coefs;*/
        }

        System.out.println("MPI Karatsuba: " + result.toString());
    }

    public static void MPIKaratsubaWorker() throws ExecutionException, InterruptedException {
        PolynomialOperations.MPIKaratsubaMultiply();
    }

    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int numProc = MPI.COMM_WORLD.Size();

        System.out.println("Program start with rank: " + rank + " and size: " + numProc);

        if (MPI.COMM_WORLD.Rank() == 0) {
            // Master process
            int totalProcessors = MPI.COMM_WORLD.Size() - 1;

            int polynomialsLength = 3;
            Polynomial polynomial1 = new Polynomial(polynomialsLength);
            polynomial1.generateRandomPolynomial();
            Thread.sleep(500);
            Polynomial polynomial2 = new Polynomial(polynomialsLength);
            polynomial2.generateRandomPolynomial();
            System.out.println("Poly 1: " + polynomial1.toString());
            System.out.println("Poly 2: " + polynomial2.toString());

            int size = polynomial1.size;
            MPIMultiplicationMaster(polynomial1, polynomial2, size);
            MPIKaratsubaMaster(polynomial1, polynomial2, size);
        } else {
            // Child process
            System.out.println("Child process");
            //MPIMultiplicationWorker();
            //MPIKaratsubaWorker();
        }

        MPI.Finalize();
    }
}
