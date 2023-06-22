
import java.awt.Color;
import nano.*;
import java.util.Random;
public class BloodVessel {
    private static int thickness;
    private static double lowerEdge;
    private static int xSize;
    private static Pen pen;
    private static final Random rand = new Random();
    private static Color colour;

    public BloodVessel(Pen pen){
        BloodVessel.pen = pen;
        setColour();
    }

    public static void setDimensions(double thickness, double lowerEdge, int xSize){
        BloodVessel.thickness = (int) thickness;
        BloodVessel.lowerEdge = lowerEdge;
        BloodVessel.xSize = xSize;
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
