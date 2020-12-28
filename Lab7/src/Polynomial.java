import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;


public class Polynomial implements Serializable {
    public int degree;
    public int[] coefficients;
    public int size = 0;

    Polynomial(int s) {
        degree = s;
        size = s + 1;
        coefficients = new int[size];
    }

    Polynomial() {
    }

    Polynomial(int[] coefficients) {
        this.coefficients = coefficients;
        size = coefficients.length;
        degree = coefficients.length - 1;
    }

    void generateRandomPolynomial() {
        Random rnd = new Random();
        int min = -10;
        int max = 10;

        for (int i = 0; i < size; i++) {
            coefficients[i] = rnd.nextInt(max - min) + min;
            if(i == size - 1) {
                while(coefficients[i] == 0) {
                    coefficients[i] = rnd.nextInt(max - min) + min;
                }
            }

            // Used only for correctness of the program
            //coefficients[i] = i;
        }
    }

    @Override
    public String toString() {
        return "Polynomial{" +
                "Coefficients=" + Arrays.toString(coefficients) +
                '}';
    }

    public int[] getCoefficients() {
        return coefficients;
    }
}
