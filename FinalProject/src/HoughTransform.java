import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.Vector;


public class HoughTransform extends Thread {
    // The size of the neighbourhood in which to search for other local maxima 
    private final int neighbourhoodSize = 4;

    // How many discrete values of theta shall we check
    private final int maxTheta = 180;

    // Using maxTheta, work out the step 
    private final double thetaStep = Math.PI / maxTheta;

    private int width, height;
    private int[][] houghArray;
    private float centerX, centerY; // the coordinates of the centre of the image
    private int houghHeight;
    private int doubleHeight;
    private int numberOfPoints;

    // Cache of values of sin and cos for different theta values
    private double[] sinCache;
    private double[] cosCache;

    HoughTransform(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void initialise() {
        // Compute the maximum height the hough array needs to have
        houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

        // Double the height of the hough array to cope with negative r values 
        doubleHeight = 2 * houghHeight;

        // Create the hough array 
        houghArray = new int[maxTheta][doubleHeight];

        // Find edge points
        centerX = (float) width / 2;
        centerY = (float) height / 2;

        numberOfPoints = 0;

        sinCache = new double[maxTheta];
        cosCache = new double[maxTheta];
        for (int t = 0; t < maxTheta; t++) {
            double realTheta = t * thetaStep;
            sinCache[t] = Math.sin(realTheta);
            cosCache[t] = Math.cos(realTheta);
        }
    }

    void addPoints(BufferedImage image) {
        // Find edge points and update the hough array
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // Find non-black pixels 
                if ((image.getRGB(x, y) & 0x000000ff) != 0) {
                    addPoint(x, y);
                }
            }
        }
    }

    private void addPoint(int x, int y) {
        // Go through each value of theta 
        for (int t = 0; t < maxTheta; t++) {
            //Work out the r values for each theta step 
            int r = (int) (((x - centerX) * cosCache[t]) + ((y - centerY) * sinCache[t]));

            // this copes with negative values of r 
            r += houghHeight;

            if (r < 0 || r >= doubleHeight) continue;

            // Increment the hough array 
            houghArray[t][r]++;
        }
        numberOfPoints++;
    }

    Vector<HoughLine> getLines(int threshold) {
        Vector<HoughLine> lines = new Vector<HoughLine>(20);
        if (numberOfPoints == 0) return lines;

        // Search for local peaks above threshold to drawLine
        for (int t = 0; t < maxTheta; t++) {
            loop:
            for (int r = neighbourhoodSize; r < doubleHeight - neighbourhoodSize; r++) {

                // Only consider points above threshold 
                if (houghArray[t][r] > threshold) {
                    int peak = houghArray[t][r];

                    // Check that this peak is indeed the local maxima 
                    for (int dx = -neighbourhoodSize; dx <= neighbourhoodSize; dx++) {
                        for (int dy = -neighbourhoodSize; dy <= neighbourhoodSize; dy++) {
                            int dt = t + dx;
                            int dr = r + dy;
                            if (dt < 0) dt = dt + maxTheta;
                            else if (dt >= maxTheta) dt = dt - maxTheta;
                            if (houghArray[dt][dr] > peak) {
                                // found a bigger point nearby, skip 
                                continue loop;
                            }
                        }
                    }

                    // Calculate the true value of theta
                    double theta = t * thetaStep;

                    // Add the line to the vector
                    lines.add(new HoughLine(theta, r));
                }
            }
        }

        return lines;
    }

    private int getHighestValue() {
        int max = 0;
        for (int t = 0; t < maxTheta; t++) {
            for (int r = 0; r < doubleHeight; r++) {
                if (houghArray[t][r] > max) {
                    max = houghArray[t][r];
                }
            }
        }
        return max;
    }

    public BufferedImage getHoughArrayImage() {
        int max = getHighestValue();
        BufferedImage image = new BufferedImage(maxTheta, doubleHeight, BufferedImage.TYPE_INT_ARGB);
        for (int t = 0; t < maxTheta; t++) {
            for (int r = 0; r < doubleHeight; r++) {
                double value = 255 * ((double) houghArray[t][r]) / max;
                int v = 255 - (int) value;
                int c = new Color(v, v, v).getRGB();
                image.setRGB(t, r, c);
            }
        }
        return image;
    }
} 
 
