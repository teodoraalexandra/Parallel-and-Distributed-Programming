package controller;

public class MultiplicationTask implements Runnable {
    private int start;
    private int end;
    private int[] A, B, result;

    public MultiplicationTask(int start, int end, int[] A, int[] B, int[] result) {
        this.start = start;
        this.end = end;
        this.A = A;
        this.B = B;
        this.result = result;
    }

    // Calculate coefficients from the result in the interval: [starting index, ending index)
    @Override
    public void run() {
        for (int index = start; index < end; index++) {
            // No more elements to calculate
            if (index > result.length) {
                return;
            }

            // Find all the pairs that we add to obtain the value of a result coefficient
            for (int j = 0; j <= index; j++) {
                if (j < A.length && (index - j) < B.length) {
                    int value = A[j] * B[index - j];
                    result[index] += value;
                }
            }
        }
    }
}
