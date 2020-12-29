import java.awt.image.BufferedImage;


public class HoughTask implements Runnable {
    private BufferedImage image;
    private int x;
    private HoughTransform houghTransform;

    HoughTask(int x, BufferedImage image, HoughTransform houghTransform) {
        this.x = x;
        this.image = image;
        this.houghTransform = houghTransform;
    }

    @Override
    public void run() {
        for (int y = 0; y < image.getHeight(); y++) {
            houghTransform.addPoint(x, y);
        }
    }
}
