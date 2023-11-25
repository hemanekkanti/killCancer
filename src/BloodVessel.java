import nano.Pen;

import java.awt.*;

public class BloodVessel {
    private static int thickness;
    private static int lowerEdge;
    private static int xSize;
    private static Pen pen;

    public BloodVessel(Pen pen) {
        BloodVessel.pen = pen;
    }

    /**
     * This function sets the dimension of the blood vessel
     * @param thickness thickness of the blood vessel
     * @param lowerEdge where the lower edge of the blood vessel is defines
     * @param xSize length of the blood vessel
     */
    public static void setDimensions(double thickness, double lowerEdge, int xSize) {
        BloodVessel.thickness = (int) thickness;
        BloodVessel.lowerEdge = (int) lowerEdge;
        BloodVessel.xSize = xSize;
        Blob.makeBlobs(150, 10);
    }

    public void draw() {
        //pen.drawRectangle(0, (int) lowerEdge, xSize-1, thickness+1, new Color(236, 90, 90), true);
    }

    public void flow(int freq) {
        for (int i = 0; i < xSize; i++) {
            Color redShade = generateRedHue(i, freq);
            pen.drawLine(i, lowerEdge, i, lowerEdge + thickness, redShade.darker());
            pen.drawLine(i, lowerEdge, i, lowerEdge + 4, redShade);
            pen.drawLine(i, lowerEdge + thickness, i, lowerEdge + thickness - 4, redShade);
        }
        Blob.drawBlobs();
    }

    /**
     * @author Hema Nekkanti
     * @param i is the vertical pixelline in the blood vessel
     * @param freq how fast the hue should change
     * @return a colour for the given pixel line
     */

    private Color generateRedHue(int i, int freq) {

        float r = (float) (0.3 * Math.cos((Math.PI * i / xSize) - (freq / 250.0) * 2 * Math.PI) + 0.6);
        float g = 0.1f;
        float b = 0.15f;

        //return Color.getHSBColor(h,s,b);

        return new Color(r, g, b);
    }

    private static class Blob extends Particle {

        private static Blob[] blobs;
        private final Color color = new Color(1f, 0.5f, 0.5f, rand.nextFloat(0.05f, 0.1f));

        private final double v = rand.nextDouble(1, 3);

        public Blob(double radius) throws OutOfSpaceException {
            super(radius);
        }

        public static void makeBlobs(int n, double size) {
            blobs = new Blob[n];
            for (int j = 0; j < blobs.length; j++) {
                try {
                    blobs[j] = new Blob(rand.nextDouble(size, size * 2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public static void drawBlobs() {
            for (Blob blob : blobs) {
                blob.draw();
            }
        }

        @Override
        protected void spawnParticle() {
            x = rand.nextDouble(-radius, xSize + radius);
            y = rand.nextDouble(lowerEdge + radius, lowerEdge + thickness - radius);
        }

        @Override
        public void move() {
            x += v;
            if (x - radius > xSize) x = -radius;
        }

        @Override
        public void draw() {
            pen.drawCircle((int) x, (int) y, (int) radius, color, true);
            move();
        }
    }


}
