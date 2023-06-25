import nano.Pen;

import java.awt.*;

public class Graph {
    private static Pen ink;
    private static int [] stats;
    private static int X_AXIS;
    private static int Y_AXIS;
    private int BAR_WIDTH = 75;
    private int SPACING = 15;
    private int LEGEND_MARGIN = 10;
    private int LEGEND_SPACING = 20;
    private int LEGEND_SIDE = 15;
    private int YMAX = 400;
    public Graph(Pen ink){
        Graph.ink = ink;
    }

    public static void setConstants(int X_AXIS, int Y_AXIS){
        Graph.X_AXIS = X_AXIS;
        Graph.Y_AXIS = Y_AXIS;
    }

    //Cell dummyEgg = new Cell(69);
    public void plot(int [] stats) {
        //background colour
        ink.drawRectangle(0, 0, X_AXIS, Y_AXIS, new Color(35, 8, 12), true);

        //initialzing the constants

        //Hey, it's the legend
        for (int i = 0; i < 5; i++) {
            ink.drawRectangle(LEGEND_MARGIN, Y_AXIS - (i + 1) * LEGEND_SPACING, LEGEND_SIDE, LEGEND_SIDE, Cell.CellPhase.values()[i].colour, true);
            ink.drawString(2 * LEGEND_MARGIN + LEGEND_SIDE, Y_AXIS - (i + 1) * LEGEND_SPACING + 2, Color.WHITE, Cell.CellPhase.values()[i].toString());
        }

        //normalizing values for the screen
        int normalCells = stats[0] * (Y_AXIS - 50) / YMAX;
        int cancerCells = stats[1] * (Y_AXIS - 50) / YMAX;
        int chemoParticles = stats[2] * (Y_AXIS - 50) / YMAX;

        //drawing the bars
        ink.drawRectangle(SPACING, 20, BAR_WIDTH, normalCells, new Color(100, 170, 190), true);
        ink.drawRectangle(2 * SPACING + BAR_WIDTH, 20, BAR_WIDTH, cancerCells, new Color(100, 100, 220), true);
        ink.drawRectangle(3 * SPACING + 2 * BAR_WIDTH, 20, BAR_WIDTH, chemoParticles, new Color(200, 100, 220), true);

        //adding numbers to the bars
        ink.drawString(SPACING, normalCells + 25, Color.white, String.valueOf(stats[0]));
        ink.drawString(2 * SPACING + BAR_WIDTH, cancerCells + 25, Color.white, String.valueOf(stats[1]));
        ink.drawString(3 * SPACING + 2 * BAR_WIDTH, chemoParticles + 25, Color.white, String.valueOf(stats[2]));

        //adding description text below the bars
        ink.drawString(SPACING, 5, Color.white, "Normal Cells");
        ink.drawString(2 * SPACING + BAR_WIDTH, 5, Color.white, "Cancer Cells");
        ink.drawString(3 * SPACING + 2 * BAR_WIDTH, 5, Color.white, "Chemo dose");
    }
}
