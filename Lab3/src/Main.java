import controller.PatternController;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        // Necessary parameters
        int min = 1;
        int max = 10;
        int SIZE_OF_MATRIX = 100;
        int NUMBER_OF_THREADS = 5;

        // Generate 2 random matrices
        int[][] a = new int[SIZE_OF_MATRIX][SIZE_OF_MATRIX];
        int[][] b = new int[SIZE_OF_MATRIX][SIZE_OF_MATRIX];

        for (int i = 0; i < SIZE_OF_MATRIX; i++) {
            for (int j = 0; j < SIZE_OF_MATRIX; j++) {
                a[i][j] = (int)(Math.random() * (max - min + 1) + min);
                b[i][j] = (int)(Math.random() * (max - min + 1) + min);
            }
        }

        PatternController patternController = new PatternController(a, b, SIZE_OF_MATRIX, NUMBER_OF_THREADS);

        // PATTERN 1 - LOW LEVEL
        patternController.lowLevelPattern1();

        // PATTERN 2 - LOW LEVEL
        patternController.lowLevelPattern2();

        // PATTERN 3 - LOW LEVEL
        patternController.lowLevelPattern3();

        // PATTERN 1 - THREAD POOL
        patternController.threadPoolPattern1();

        // PATTERN 2 - THREAD POOL
        patternController.threadPoolPattern2();

        // PATTERN 3 - THREAD POOL
        patternController.threadPoolPattern3();
    }

}
