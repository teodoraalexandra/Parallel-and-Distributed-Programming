import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.*;
import mpi.MPI;


public class Main {
    static final int threshold = 30;
    static final int numberOfThreads = 10;

    // The size of the neighbourhood in which to search for other local maxima
    static final int neighbourhoodSize = 4;

    // How many discrete values of theta shall we check
    static final int maxTheta = 180;

    private static void MPIMaster(BufferedImage image) throws IOException {
        long startMPI = System.currentTimeMillis();
        int n = MPI.COMM_WORLD.Size();
        int begin = 0;
        int end = image.getWidth() / (n - 1);

        for (int i = 0; i < n; i++) {
            if (i != 0) {
                int[] beginArray = new int[end];
                for (int x = begin; x < end; x++) {
                    // Construct the array with the needed x-es
                    beginArray[x] = x + (end) * (i - 1);
                }
                MPI.COMM_WORLD.Issend(beginArray, 0, beginArray.length, MPI.INT, i, 0);
            }
        }

        Vector<HoughLine> lines = new Vector<>();
        for (int i = 0; i < n; i++) {
            if (i != 0) {
                int[] size = new int[1];
                MPI.COMM_WORLD.Recv(size, 0, 1, MPI.INT, i, 0);
                int linesSize = size[0];

                Object[] objectLines = new Object[linesSize];
                MPI.COMM_WORLD.Recv(objectLines, 0, objectLines.length, MPI.OBJECT, i, 0);

                for (Object objectLine : objectLines) {
                    lines.add((HoughLine) objectLine);
                }
            }
        }

        // Draw the lines
        drawLines(image, lines);
        long endMPI = System.currentTimeMillis();
        System.out.println("MPI performance: " + (endMPI - startMPI));
    }

    private static void MPIWorker(BufferedImage image) {
        int[] xArray = new int[image.getWidth() / (MPI.COMM_WORLD.Size() - 1)];
        MPI.COMM_WORLD.Recv(xArray, 0, xArray.length, MPI.INT, 0, 0);

        for (int x: xArray) {
            for (int y = 0; y < image.getHeight(); y++) {
                HoughTransform.addPoint(x, y, image);
            }
        }

        List<HoughLine> lines = HoughTransform.getLines();
        Object[] objectLines = new Object[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            objectLines[i] = lines.get(i);
        }
        int[] size = new int[1];
        size[0] = lines.size();
        MPI.COMM_WORLD.Issend(size, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Issend(objectLines, 0, objectLines.length, MPI.OBJECT, 0, 0);
    }

    public static void main(String[] args) throws Exception {
        String fileNameInputImage = "images/vase.png";
        //houghMPI(args, fileNameInputImage);
        houghThreads(fileNameInputImage);
    }

    static void houghMPI(String[] args, String fileNameInputImage) throws IOException {
        MPI.Init(args);

        // Load the file
        BufferedImage image = javax.imageio.ImageIO.read(new File(fileNameInputImage));

        // Create a hough transform object with the right dimensions
        HoughTransform houghTransform = new HoughTransform(image.getWidth(), image.getHeight(), image);
        houghTransform.initialise();

        if (MPI.COMM_WORLD.Rank() == 0) {
            // Master process
            MPIMaster(image);
        } else {
            // Child process
            MPIWorker(image);
        }

        MPI.Finalize();
    }

    static void houghThreads(String fileNameInputImage) throws InterruptedException, IOException {
        long startThreads = System.currentTimeMillis();

        // Load the file
        BufferedImage image = javax.imageio.ImageIO.read(new File(fileNameInputImage));

        // Create a hough transform object with the right dimensions
        HoughTransform houghTransform = new HoughTransform(image.getWidth(), image.getHeight(), image);
        houghTransform.initialise();

        // Add the points from the image
        houghTransform.addPoints();

        // Add image from hough space
        // --- FOR THREADS
        Vector<HoughLine> lines = HoughTransform.getLines();

        // Draw the lines
        drawLines(image, lines);
        long endThreads = System.currentTimeMillis();
        System.out.println("Thread performance: " + (endThreads - startThreads));
    }

    private static void drawLines(BufferedImage image, Vector<HoughLine> lines) throws IOException {
        for (int j = 0; j < lines.size(); j++) {
            HoughLine line = lines.elementAt(j);
            line.drawLine(image, Color.RED.getRGB());
        }

        ImageIO.write(image, "PNG", new File("images/houghTransform.png"));
    }
}
