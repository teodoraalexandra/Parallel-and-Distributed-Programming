import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;


public class Main {
    public static void main(String[] args) throws Exception {
        String fileNameInputImage = "images/vase.png";
        int threshold = 30;

        // Load the file
        BufferedImage image = javax.imageio.ImageIO.read(new File(fileNameInputImage));

        // Create a hough transform object with the right dimensions
        HoughTransform houghTransform = new HoughTransform(image.getWidth(), image.getHeight());
        houghTransform.initialise();

        // Add the points from the image
        houghTransform.addPoints(image);

        // Get the lines
        Vector<HoughLine> lines = houghTransform.getLines(threshold);

        // Draw the lines
        for (int j = 0; j < lines.size(); j++) {
            HoughLine line = lines.elementAt(j);
            line.drawLine(image, Color.RED.getRGB());
        }

        ImageIO.write(image, "PNG", new File("images/houghTransform.png"));
    }
}
