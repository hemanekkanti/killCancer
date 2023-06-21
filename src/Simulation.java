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
        double thickness = 150;
        double lowerEdge = (ySize-thickness)/2 ;
        double upperEdge = (ySize+thickness)/2 ;

        Particle.setDimensions(xSize,ySize, lowerEdge, upperEdge);
        Particle.setPen(pen);

        List<Cell> cells = new ArrayList<>();
        List<Cell> newCells = new ArrayList<>();
        List<Cell> hitList = new ArrayList<>();
        Cell.setNewCellList(newCells);
        Cell.setHitList(hitList);
        Particle.setRestrainList(cells);

        try {
            for (int i = 0; i < ncells; i++) cells.add(new Cell(radius));
            //cells.add(new Cancer(radius));
        } catch (Particle.OutOfSpaceException s) {
            throw new RuntimeException(s);
        }

        while(true) {
            pen.drawRectangle(0, (int) lowerEdge, xSize-1, (int) thickness, Color.PINK, true);
            newCells.clear();
            cells.forEach(c -> {
                        c.move();
                        int i = 0;
                        while (cells.parallelStream().anyMatch(c::isOverlapping)) {
                            c.unmove();
                            if (i++ > 20) break;
                            c.move();
                        }
                        c.draw();
                    });
            cells.addAll(newCells);
            cells.removeAll(hitList);
            pen.drawString(5, 5, Color.WHITE, "cell count:"+cells.size());
            screen.update();
            screen.pause(50);
            screen.clear();
        }
    }


    public static void main(String[] args) {
        Simulation e = new Simulation();
    }
}
