import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


class Graph {
    private int[][] graph = new int[1000][4000];
    private String fileName;
    private int numberOfVertices;
    private int numberOfEdges;

    Graph(String fileName) {
        this.fileName = fileName;
        this.numberOfVertices = 0;
        this.numberOfEdges = 0;
    }

    void readGraph() throws FileNotFoundException {
        File file = new File(this.fileName);
        Scanner scanner = new Scanner(file);
        String firstLine = scanner.nextLine();
        String[] firstLineSplit = firstLine.split(" ");
        this.numberOfVertices = Integer.parseInt(firstLineSplit[0]);
        this.numberOfEdges = Integer.parseInt(firstLineSplit[1]);

        while (scanner.hasNextLine()) {
            String pair = scanner.nextLine();
            String[] pairSplit = pair.split(" ");
            int x = Integer.parseInt(pairSplit[0]);
            int y = Integer.parseInt(pairSplit[1]);
            graph[x][y] = 1;
        }

        scanner.close();
    }

    int getNumberOfVertices() {
        return numberOfVertices;
    }

    int[][] getGraph() {
        return graph;
    }
}
