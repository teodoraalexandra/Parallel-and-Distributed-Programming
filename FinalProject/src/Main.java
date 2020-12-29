import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.*;


public class Main {
    static final int threshold = 30;
    static final int numberOfThreads = 5;

    // The size of the neighbourhood in which to search for other local maxima
    static final int neighbourhoodSize = 4;

    // How many discrete values of theta shall we check
    static final int maxTheta = 180;

    private static final int numberOfTrials = 10;

    public static void main(String[] args) throws Exception {
        List<Integer> times = new ArrayList<>();

        for (int i = 0; i < numberOfTrials; i ++) {
            long start = System.currentTimeMillis();

            String fileNameInputImage = "images/vase.png";

            // Load the file
            BufferedImage image = javax.imageio.ImageIO.read(new File(fileNameInputImage));

            // Create a hough transform object with the right dimensions
            HoughTransform houghTransform = new HoughTransform(image.getWidth(), image.getHeight(), image);
            houghTransform.initialise();

            // Add the points from the image
            houghTransform.addPoints();

            ImageIO.write(houghTransform.getHoughArrayImage(), "PNG", new File("images/houghSpace.png"));

            long end = System.currentTimeMillis();
            times.add((int) (end - start));

            // Add image from hough space
            // Get the lines
            Vector<HoughLine> lines = houghTransform.getLines();

            // Draw the lines
            for (int j = 0; j < lines.size(); j++) {
                HoughLine line = lines.elementAt(j);
                line.drawLine(image, Color.RED.getRGB());
            }

            ImageIO.write(image, "PNG", new File("images/houghTransform.png"));
        }

        OptionalDouble average = times
                .stream()
                .mapToDouble(a -> a)
                .average();

        System.out.println("Thread performance: " + (average.isPresent() ? average.getAsDouble() : 0));
    }
}
