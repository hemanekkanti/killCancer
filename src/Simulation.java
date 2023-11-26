import nano.Canvas;
import nano.Pen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Master class that renders the whole design
 * @author Hema Nekkanti
 */

public class Simulation {
    /**
     * This method intializes the graphic screen and all the cell over the iteration of time
     */
    public Simulation(Environment options) {
        //Initializing the screens
        int X_LENGTH = 1024;
        int Y_LENGTH = 800;
        //Screen dimensions for the graph
        int X_AXIS = 300;
        int Y_AXIS = 400;

        //initilazing the main simulation screen
        Canvas screen = new Canvas(X_LENGTH, Y_LENGTH, 0, 0);
        Pen pen = new Pen(screen);
        //Initializing the screen for graph
        Canvas graph = new Canvas(X_AXIS, Y_AXIS, X_LENGTH + 50, 50);
        Pen ink = new Pen(graph);
        Graph bars = new Graph(ink);

        double radius = 18;
        int ncells = options.getInitialCells();
        int ncancer = 3;
        int ndrugs;
        int chemoInjectionQuant = 400;
        double thickness = 250; //does not work above a certain value; program crashes
        double lowerEdge = (Y_LENGTH - thickness) / 2;

        //setting the static variables
        Particle.setDimensions(X_LENGTH, Y_LENGTH, lowerEdge, thickness);
        Particle.setPen(pen);
        BloodVessel bloodstream = new BloodVessel(pen);
        BloodVessel.setDimensions(thickness, lowerEdge, X_LENGTH);
        Graph.setConstants(X_AXIS, Y_AXIS);


        //making the lists
        List<Cell> cells = new ArrayList<>(options.getInitialCells() + ncancer);
        List<Cell> newCells = new ArrayList<>();
        List<Chemo> drugs = new ArrayList<>(chemoInjectionQuant);

        //setting the static lists
        Cell.setNewCellList(newCells);
        Particle.setRestrainList(cells);
        Chemo.setDrugsList(drugs);
        Graph.setCellList(cells);

        int t = 0;

        try {
            for (int i = 0; i < options.getInitialCells(); i++) cells.add(new Cell(radius));
            for (int i = 0; i < ncancer; i++) cells.add(new Cancer(radius));
        } catch (Particle.OutOfSpaceException s) {
            throw new RuntimeException(s);
        }

        while (true) {
            //draw background
            pen.drawRectangle(0, 0, X_LENGTH, Y_LENGTH, new Color(35, 8, 12), true);
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
            if (ncancer > 15 && !Chemo.started || ncancer > 0 && ndrugs < 0.08 * chemoInjectionQuant && Chemo.started)
                Chemo.injectDrugs(chemoInjectionQuant);
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

            //another panel showing some statistics
            bars.plot(ndrugs);

            //update all the drawing on the screen and set the refresh rate
            screen.update();
            graph.update();
            screen.pause(10);
            graph.pause(10);
            screen.clear();
            graph.clear();
            t++;
        }
    }

}
