import nano.Canvas;
import nano.Pen;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
public class Simulation {
    public Simulation() {
        int xSize = 1000;
        int ySize = 700;
        Canvas screen = new Canvas(xSize, ySize, 150, 50);
        Pen pen = new Pen(screen);
        Random rand = new Random();

        double radius = 15;
        int ncells = 20;
        int ncancer = 1;
        int ndrugs = 0;
        int chemoInjectionQuant = 100;
        double thickness = 150;
        double lowerEdge = (ySize-thickness)/2 ;

        //setting the static variables
        Particle.setDimensions(xSize,ySize, lowerEdge, thickness);
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

        int flowrate=0;

        while(true) {
            //drawBloodvessel
            bloodstream.draw();
            bloodstream.flow(flowrate);

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
            if(ncancer>4 && ndrugs < 0.08 * chemoInjectionQuant) Chemo.injectDrugs(chemoInjectionQuant);
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
            pen.drawString(5, 5, Color.WHITE, "cell count:"+cells.size());

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
