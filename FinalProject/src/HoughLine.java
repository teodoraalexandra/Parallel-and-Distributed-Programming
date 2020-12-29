import java.awt.image.BufferedImage;


class HoughLine {
    private double theta;
    private double r;

    HoughLine(double theta, double r) {
        this.theta = theta; // the resulted angle
        this.r = r; // the resulted radius from the centre
    }

    void drawLine(BufferedImage image, int color) {
        int height = image.getHeight();
        int width = image.getWidth();

        int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

        // Find edge points
        float centerX = (float) width / 2;
        float centerY = (float) height / 2;

        // Draw edges in output array
        double tSin = Math.sin(theta);
        double tCos = Math.cos(theta);

        if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) {
            // Draw vertical lines
            for (int y = 0; y < height; y++) {
                int x = (int) ((((r - houghHeight) - ((y - centerY) * tSin)) / tCos) + centerX);
                if (x < width && x >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        } else {
            // Draw horizontal lines
            for (int x = 0; x < width; x++) {
                int y = (int) ((((r - houghHeight) - ((x - centerX) * tCos)) / tSin) + centerY);
                if (y < height && y >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        }
    }
}
