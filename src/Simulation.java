import nano.Canvas;
import nano.Pen;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
public class Simulation {
    public Simulation() {
        final int SCREEN_X = 1000;
        final int SCREEN_Y = 700;
        final double RADIUS = 40;
        final int INITIAL_CELLS = 20;
        final int INITIAL_CANCER = 1;
        final int CHEMO_INJECTION = 500;
        final double VEIN_THICKNESS = 300;

        Canvas screen = new Canvas(SCREEN_X, SCREEN_Y, 150, 50);
        Pen pen = new Pen(screen);
        Random rand = new Random();

        int ncells;
        int ncancer;
        int ndrugs;

        double lowerEdge = (SCREEN_Y-VEIN_THICKNESS)/2 ;

        //setting the static variables
        Particle.setDimensions(SCREEN_X,SCREEN_Y);
        Particle.setPen(pen);
        BloodVessel bloodstream = new BloodVessel(pen);
        BloodVessel.setDimensions(VEIN_THICKNESS, lowerEdge, SCREEN_X);

        //making the lists
        List<Cell> cells = new ArrayList<>(INITIAL_CELLS + INITIAL_CANCER);
        List<Cell> newCells = new ArrayList<>();
        List<Chemo> drugs = new ArrayList<>((CHEMO_INJECTION * 3)/2);

        //setting the static lists
        Cell.setNewCellList(newCells);
        Particle.setRestrainList(cells);
        Chemo.setDrugsList(drugs);


        int t = 0;

        int flowrate=0;

        while(true) {
            //drawBloodvessel
            bloodstream.draw();
            bloodstream.flow(flowrate);

            if (t == 50) { try {
                for (int i = 0; i < INITIAL_CELLS; i++) cells.add(new Cell(RADIUS));
                for (int i = 0; i < INITIAL_CANCER; i++) cells.add(new Cancer(RADIUS));
            } catch (Particle.OutOfSpaceException s) {
                throw new RuntimeException("Initial particle count too high. Can't spawn!");
            }}

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

            ncancer = (int) cells.stream().filter(c->c instanceof Cancer).count();
            ncells = cells.size()-ncancer;
            ndrugs = drugs.size();

            //chemo dosage
            if(ncancer>4 && ndrugs < 0.08 * CHEMO_INJECTION) Chemo.injectDrugs(CHEMO_INJECTION);
            drugs.forEach(chemo -> {
                chemo.move();
                //cells.stream().filter(Cell::vulnerableToChemo).filter(chemo::isOverlapping).forEach(Cell::killSelf);
                cells.stream().filter(Cell::vulnerableToChemo).filter(chemo::isOverlapping).findAny().ifPresent( c -> {
                    c.killSelf();
                    chemo.killSelf();
                });
                chemo.draw();
            });
            drugs.removeIf(Chemo::mustDie);
            pen.drawString(5, 5, Color.WHITE, "cell count:"+ncells);

            screen.update();
            screen.pause(10);
            screen.clear();
            t++;

            if( flowrate ++ >= 250) flowrate = 0;
        }
    }


    public static void main(String[] args) {
        Simulation e = new Simulation();
    }
}
