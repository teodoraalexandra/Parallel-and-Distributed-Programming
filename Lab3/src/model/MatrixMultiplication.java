package model;


public class MatrixMultiplication {
    private int size;
    private int[][] a;
    private int[][] b;
    private int[][] result;
    private int step_i;
    private int numberOfThreads;

    public MatrixMultiplication(int[][] a, int[][] b, int size, int numberOfThreads) {
        this.a = a;
        this.b = b;
        this.size = size;
        this.result = new int[size][size];
        this.step_i = 0;
        this.numberOfThreads = numberOfThreads;
    }

    private void multiplySubTask(int row_a_index, int col_b_index) {
        // computes a single element of the resulting matrix
        for (int i = 0; i < size; i++) {
            result[row_a_index][col_b_index] += a[row_a_index][i] * b[i][col_b_index];
        }
    }

    public Runnable pattern_1 = () -> {
        int core = step_i++;
        for (int row_a_index = core * size / numberOfThreads; row_a_index < (core + 1) * size / numberOfThreads; row_a_index++) {
            for (int col_b_index = 0; col_b_index < size; col_b_index++) {
                multiplySubTask(row_a_index, col_b_index);
            }
        }
    };

    public Runnable pattern_2 = () -> {
        int core = step_i++;
        for (int row_a_index = 0; row_a_index < size; row_a_index++) {
            for (int col_b_index = core * size / numberOfThreads; col_b_index < (core + 1) * size / numberOfThreads; col_b_index++) {
                multiplySubTask(row_a_index, col_b_index);
            }
        }
    };

    public Runnable pattern_3 = () -> {
        int core = step_i++;
        for (int row_a_index = core; row_a_index < size; row_a_index += numberOfThreads) {
            for (int col_b_index = 0; col_b_index < size; col_b_index++) {
                multiplySubTask(row_a_index, col_b_index);
            }
        }
    };

    public int[][] getResult() {
        return result;
    }
}
