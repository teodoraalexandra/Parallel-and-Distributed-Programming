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

    void generateRandomPolynomial() {
        Random rnd = new Random();
        int min = -10;
        int max = 10;

        for (int i = 0; i < size; i++) {
            /*coefficients[i] = rnd.nextInt(max - min) + min;
            if(i == size - 1) {
                while(coefficients[i] == 0) {
                    coefficients[i] = rnd.nextInt(max - min) + min;
                }
            }*/
            coefficients[i] = i;
        }
    }

    public Polynomial getLast(int m) {
        Polynomial result = new Polynomial(m - 1);

        for (int i = 0; i < m; i++) {
            result.coefficients[i] = coefficients[i];
        }

        return result;
    }

    public Polynomial getFirst(int m)
    {
        Polynomial result = new Polynomial(m - 1);
        int k = 0;

        for (int i = size - m; i < size; i++) {
            result.coefficients[k] = coefficients[i];
            k++;
        }

        return result;
    }

    public Polynomial sum(Polynomial b) {
        int size1 = size;
        int size2 = b.size;

        int sizeMax = Math.max(size1, size2);

        Polynomial result = new Polynomial(sizeMax - 1);

        for(int i = 0; i < sizeMax; i++) {
            int res = 0;
            if(i < size1) {
                res = res + coefficients[i];
            }

            if(i < size2) {
                res = res + b.coefficients[i];
            }

            result.coefficients[i] = res;
        }
        return result;
    }

    public Polynomial difference(Polynomial b) {
        int size1 = size;
        int size2 = b.size;

        int sizeMax = Math.max(size1, size2);

        Polynomial result = new Polynomial(sizeMax - 1);

        for (int i = 0; i < sizeMax; i++) {
            int res = 0;

            if (i < size1) {
                res = coefficients[i];
            }

            if (i < size2) {
                res = res - b.coefficients[i];
            }

            result.coefficients[i] = res;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Polynomial{" +
                "Coefficients=" + Arrays.toString(coefficients) +
                '}';
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(int[] coefficients) {
        this.coefficients = coefficients;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
