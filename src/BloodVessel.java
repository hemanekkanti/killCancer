
import java.awt.Color;
import nano.*;

import java.util.Arrays;
import java.util.Random;
public class BloodVessel {
    private static int thickness;

    public static double getUpperEdge() {
        return (lowerEdge+thickness);
    }
    public static double getLowerEdge() {
        return lowerEdge;
    }

    private static double lowerEdge;
    private static int xSize;
    private static Pen pen;
    private static final Random rand = new Random();
    private static Color colour;

    private static class Blob extends Particle {

        private static Blob[] blobs;
        private final Color color = new Color(1f,0.5f,0.5f,rand.nextFloat(0.1f, 0.2f));

        private final double v = rand.nextDouble(1,3);

        public Blob(double radius) throws OutOfSpaceException {super(radius);}
        public static void makeBlobs(int n, double size) {
            blobs = new Blob[n];
            for (int j = 0; j<blobs.length; j++) {
                try {blobs[j] = new Blob(rand.nextDouble(size, size*2));} catch (Exception e) {e.printStackTrace();}
            }
        }
        public static void drawBlobs() {for (int i = 0; i< blobs.length; i++){blobs[i].draw();}}
        @Override
        protected void spawnParticle() {
            x = rand.nextDouble(-radius, xSize+radius);
            y = rand.nextDouble(lowerEdge+radius, lowerEdge+thickness-radius);
        }
        @Override
        public void move() {
            x+=v;
            if (x - radius > xSize) x = -radius;
        }
        @Override
        public void draw() {
            pen.drawCircle((int)x, (int)y, (int)radius, color, true);
            move();
        }
    }

    public BloodVessel(Pen pen){
        BloodVessel.pen = pen;
        setColour();
    }

    public static void setDimensions(double thickness, double lowerEdge, int xSize){
        BloodVessel.thickness = (int) thickness;
        BloodVessel.lowerEdge = lowerEdge;
        BloodVessel.xSize = xSize;
        Blob.makeBlobs(100, 10);
    }

    public void draw(){
        pen.drawRectangle(0, (int) lowerEdge, xSize-1, thickness+1, Color.white, false);
    }

    public void flow(int freq){
        for (int i = 0; i < xSize; i++) {
            Color redShade = generateRedHue(i, freq);
            if (i % 8 == 0) {
                pen.drawRectangle(i, (int) lowerEdge, 4, thickness, redShade.darker(), true);
                pen.drawRectangle(i + 4, (int) lowerEdge, 4, thickness, redShade, true);
            }
        }
        Blob.drawBlobs();
    }
    private Color generateRedHue(int i, int flowrate){
        float h = 0;
        float s = (float) (0.3*Math.cos((Math.PI*i/xSize)-(flowrate / 250.0) * 2*Math.PI)+0.6);
        float b = s;

        return Color.getHSBColor(h,s,b);
    }

    private Color generateColor(){
        float h = rand.nextFloat(0,1);
        float s = rand.nextFloat(0.7F,1);
        float b = rand.nextFloat(0.9F,1);

        return Color.getHSBColor(h,s,b);
    }
    private void setColour(){
        BloodVessel.colour = generateColor();
    }
}
