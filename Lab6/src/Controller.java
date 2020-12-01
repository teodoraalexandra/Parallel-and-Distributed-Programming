import java.util.ArrayList;
import java.util.List;


class Controller  {
    private final int LEVEL = 10;
    private Graph graph;
    private List<Integer> solution = new ArrayList<>();
    private boolean hasSolution = false;

    Controller(Graph graph) {
        this.graph = graph;

        // Supposing the starting point of the solution is 0
        this.solution.add(0);
    }

    private boolean exists(int node, List<Integer> solution) {
        for (Integer x: solution) {
            if (x == node) {
                return true;
            }
        }

        return false;
    }

    private boolean isSolution(List<Integer> solution, int n) {
        int[][] G = graph.getGraph();
        return solution.size() == n && G[solution.get(solution.size() - 1)][0] == 1;
    }

    void backTracking(List<Integer> solution, int k, int n) {
        int[][] G = graph.getGraph();
        if (isSolution(solution, n)) {
            hasSolution = true;
        } else {
            for(int i = 0; i < n; i++) {
                if(G[solution.get(k)][i] == 1 && !exists(i, solution)) {
                    solution.add(i);
                    backTracking(solution, k + 1, n);
                }
            }
        }
    }

    void threadBackTracking(List<Integer> solution, int k, int n) throws InterruptedException {
        int[][] G = graph.getGraph();
        if (isSolution(solution, n)) {
            hasSolution = true;
        } else {
            for(int i = 0; i < n; i++) {
                if(G[solution.get(k)][i] == 1 && !exists(i, solution)) {
                    solution.add(i);

                    Thread thread = new Thread();
                    boolean init = false;
                    if (k < LEVEL) {
                        threadBackTracking(solution, k + 1, n);
                    } else {
                        thread = new Thread(() -> backTracking(solution, k + 1, n));
                        init = true;
                    }

                    if (init) {
                        thread.join();
                    }
                }
            }
        }
    }

    List<Integer> getSolution() {
        return solution;
    }

    boolean isHasSolution() {
        return hasSolution;
    }
}
