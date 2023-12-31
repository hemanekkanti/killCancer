import nano.Pen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    private static Pen ink;
    private static int X_AXIS;
    private static int Y_AXIS;
    private static final int NUM_PHASES = Cell.CellPhase.values().length;
    int [] healthyBar = new int [NUM_PHASES];
    int [] cancerBar = new int [NUM_PHASES];
    private final int BAR_WIDTH = 75;
    private final int SPACING = 15;
    private final int YMAX = 250;
    protected static java.util.List<Cell> cellList;

    public static void setCellList(List<Cell> l) {cellList = l;}
    public Graph(Pen ink){
        Graph.ink = ink;
    }

    public static void setConstants(int X_AXIS, int Y_AXIS){
        Graph.X_AXIS = X_AXIS;
        Graph.Y_AXIS = Y_AXIS;
    }

    //Cell dummyEgg = new Cell(69);
    public void plot(int nchemo) {
        legend();
        numberCountBars(nchemo);
        //looping throught enum and drawing the bars
        calculateBars();
        int healthyHeight = 0;
        int cancerHeight = 0;
        for(int i=0; i<healthyBar.length; i++){

            int healthyThisPhase = normalize(healthyBar[i]);
            int cancerThisPhase = normalize(cancerBar[i]);
            Color colour = Cell.CellPhase.values()[i].colour;

            ink.drawRectangle(
                    SPACING,
                    cancerHeight+20,
                    BAR_WIDTH,
                    cancerThisPhase,
                    colour,
                    true
            );

            ink.drawRectangle(
                    2*SPACING+BAR_WIDTH,
                    healthyHeight+20,
                    BAR_WIDTH,
                    healthyThisPhase,
                    colour,
                    true
            );


            healthyHeight += healthyThisPhase;
            cancerHeight += cancerThisPhase;
        }

        //draw the outer bars
        ink.drawRectangle((SPACING-1), 20, BAR_WIDTH+1, cancerHeight+1, Color.WHITE, false);
        ink.drawRectangle(2 * SPACING + BAR_WIDTH-1, 20, BAR_WIDTH+1, healthyHeight+1, Color.WHITE,false);


        xLabels();
    }

    private void legend(){
        //Hey, it's the legend
        for (int i = 0; i < 5; i++) {
            int LEGEND_MARGIN = 10;
            int LEGEND_SPACING = 20;
            int LEGEND_SIDE = 15;
            ink.drawRectangle(LEGEND_MARGIN, Y_AXIS - (i + 1) * LEGEND_SPACING, LEGEND_SIDE, LEGEND_SIDE, Cell.CellPhase.values()[i].colour, true);
            ink.drawRectangle(LEGEND_MARGIN, Y_AXIS - (i + 1) * LEGEND_SPACING, LEGEND_SIDE, LEGEND_SIDE, Color.WHITE, false);
            ink.drawString(2 * LEGEND_MARGIN + LEGEND_SIDE, Y_AXIS - (i + 1) * LEGEND_SPACING + 2, Color.WHITE, Cell.CellPhase.values()[i].toString());
        }
    }

    private void numberCountBars(int nchemo){
        //sum of cells
        int normalCells = Arrays.stream(healthyBar).sum();
        int cancerCells = Arrays.stream(cancerBar).sum();
        int chemoParticles = nchemo;


        //adding numbers to the bars
        ink.drawString(SPACING, normalize(cancerCells) + 25, Color.white, String.valueOf(cancerCells));
        ink.drawString(2 * SPACING + BAR_WIDTH, normalize(normalCells) + 25, Color.white, String.valueOf(normalCells));
        ink.drawString(3 * SPACING + 2 * BAR_WIDTH, normalize(chemoParticles) + 25, Color.white, String.valueOf(nchemo));

        //draw the big bar
        ink.drawRectangle(3 * SPACING + 2 * BAR_WIDTH, 20, BAR_WIDTH, normalize(chemoParticles), new Color(255,255,255,100),true);
        ink.drawRectangle(3 * SPACING + 2*BAR_WIDTH-1, 20, BAR_WIDTH+1, normalize(chemoParticles)+1, Color.WHITE,false);
    }

    private int normalize(int i){
        return (int) Math.round((i*(Y_AXIS-50))/(double) YMAX);
    }

    private void xLabels(){
        //adding description text below the bars
        ink.drawString(SPACING, 5, Color.white, "Cancer Cells");
        ink.drawString(2 * SPACING + BAR_WIDTH, 5, Color.white, "Normal Cells");
        ink.drawString(3 * SPACING + 2 * BAR_WIDTH, 5, Color.white, "Chemo dose");
    }

    private void calculateBars(){
        int i =0;
        for(Cell.CellPhase pHase:Cell.CellPhase.values()){
            healthyBar[i] = (int) cellList.stream().filter(cell-> !(cell instanceof Cancer)).filter(c -> c.phase==pHase).count();
            cancerBar[i++] = (int) cellList.stream().filter(cell-> cell instanceof Cancer).filter(c -> c.phase==pHase).count();
        }
    }

}
