import nano.Canvas;
import nano.Pen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {
    public Simulation() {
        //Initializing the screens
        int xSize = 1000;
        int ySize = 700;
        int xAxis = 300;
        int yAxis = 400;
        Canvas screen = new Canvas(xSize, ySize, 30, 50);
        Pen pen = new Pen(screen);
        Random rand = new Random();
        //Initializing the screen for graph
        Canvas graph = new Canvas(xAxis, yAxis, 1100, 50);
        Pen ink = new Pen(graph);

        double radius = 15;
        int ncells = 20;
        int ncancer = 1;
        int ndrugs = 0;
        int chemoInjectionQuant = 300;
        double thickness = 200;
        double lowerEdge = (ySize - thickness) / 2;

        //setting the static variables
        Particle.setDimensions(xSize, ySize, lowerEdge, thickness);
        Particle.setPen(pen);
        BloodVessel bloodstream = new BloodVessel(pen);
        BloodVessel.setDimensions(thickness, lowerEdge, xSize);

        //making the lists
        List<Cell> cells = new ArrayList<>(ncells + ncancer);
        List<Cell> newCells = new ArrayList<>();
        List<Chemo> drugs = new ArrayList<>(chemoInjectionQuant);

        //setting the static lists
        Cell.setNewCellList(newCells);
        Particle.setRestrainList(cells);
        Chemo.setDrugsList(drugs);


        int t = 0;

        try {
            for (int i = 0; i < ncells; i++) cells.add(new Cell(radius));
            for (int i = 0; i < ncancer; i++) cells.add(new Cancer(radius));
        } catch (Particle.OutOfSpaceException s) {
            throw new RuntimeException(s);
        }

        while (true) {
            //drawBloodvessel
            bloodstream.draw();
            bloodstream.flow(t % 250);

            //move cells all the while bouncing them properly within the space
            newCells.clear();
            boolean phaseTick = t % 5 == 0;
            cells.forEach(c -> {
                c.move();
                int i = 0;
                while (cells.stream().anyMatch(c::isOverlapping)) {
                    c.unmove();
                    if (i++ > 20) break;
                    c.move();
                }
                if (phaseTick) c.changePhase();
                c.draw();
            });
            cells.addAll(newCells);
            cells.removeIf(Cell::mustDie);

            ncancer = (int) cells.stream().filter(c -> c instanceof Cancer).count();
            ncells = cells.size() - ncancer;
            ndrugs = drugs.size();

            //chemo dosage
            if (ncancer > 4 && ndrugs < 0.08 * chemoInjectionQuant) Chemo.injectDrugs(chemoInjectionQuant);
            drugs.forEach(chemo -> {
                chemo.move();
                //cells.stream().filter(Cell::vulnerableToChemo).filter(chemo::isOverlapping).forEach(Cell::killSelf);
                cells.stream().filter(Cell::vulnerableToChemo).filter(chemo::isOverlapping).findAny().ifPresent(c -> {
                    c.killSelf();
                    chemo.killSelf();
                });
                chemo.draw();
            });
            drugs.removeIf(Chemo::mustDie);
            pen.drawString(5, 5, Color.WHITE, "cell count:" + cells.size());

            //another panel showing some statistics
            //panels ymax is 200 cells
            int [] cellCount = {ncells,ncancer,ndrugs};
            graph(ink,cellCount,yAxis);

            //update all the drawing on the screen and set the refersh rate
            screen.update();
            graph.update();
            screen.pause(10);
            graph.pause(10);
            screen.clear();
            graph.clear();
            t++;


        }
    }
    public void graph(Pen ink, int [] cellCount, int yAxis) {
        int barWidth = 75;
        int spacing = 15;
        int ymax = 400;

        //normalizing values for the screen
        int normalCells = cellCount[0]*(yAxis-50)/ymax;
        int cancerCells = cellCount[1]*(yAxis-50)/ymax;
        int chemoParticles = cellCount[2]*(yAxis-50)/ymax;

        //drawing the bars
        ink.drawRectangle(spacing, 20, barWidth, normalCells, new Color(100, 170, 190), true);
        ink.drawRectangle(2*spacing+barWidth, 20, barWidth, cancerCells, new Color(100, 100, 220), true);
        ink.drawRectangle(3*spacing+2*barWidth, 20, barWidth, chemoParticles, new Color(200, 100, 220), true);

        //adding numbers to the bars
        ink.drawString(spacing,normalCells+25, Color.white, String.valueOf(cellCount[0]));
        ink.drawString(2*spacing+barWidth,cancerCells+25, Color.white, String.valueOf(cellCount[1]));
        ink.drawString(3*spacing+2*barWidth,chemoParticles+25, Color.white, String.valueOf(cellCount[2]));

        //adding description text below the bars
        ink.drawString(spacing,5, Color.white, "Normal Cells");
        ink.drawString(2*spacing+barWidth,5, Color.white, "Cancer Cells");
        ink.drawString(3*spacing+2*barWidth,5, Color.white, "Chemo dose");
    }

    public static void main(String[] args) {
        Simulation e = new Simulation();
    }

}
