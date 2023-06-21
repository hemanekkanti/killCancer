import java.awt.*;
import java.util.List;

import java.util.Random;

public class Cell extends Particle{
    protected Color colour = Color.BLUE;
    protected double innerRadius;
    protected final Random rand = new Random();
    protected cellPhase phase;
    protected static List<Cell> newCellList;
    protected static List<Cell> hitList;
    public static void setNewCellList(List<Cell> l) {newCellList = l;}
    public static void setHitList(List<Cell> h) {hitList = h;}
    int t;
    private int numberOfdivisions;

    public Cell(double radius) throws OutOfSpaceException {
        super(radius);
        phase = spawnPhase();
        phases();
    }

    private Cell(double radius, Cell parent) throws OutOfSpaceException {
        super(radius);
        // set position according to parent?
        phase = cellPhase.G1;
        phases();
    }
    protected enum cellPhase{
        G1,
        S,
        G2,
        M,
        G0;
    }

    protected cellPhase spawnPhase(){
        int stage = rand.nextInt(4);
        switch (stage){
            case 0 -> {t=1;}
            case 1 -> {t=120;}
            case 2 -> {t=200;}
            case 3 -> {t=240;}
        }
        return cellPhase.values()[stage];
    }

    public void phases(){
        double outerRadius = radius;
        switch (phase) {
            case G1 -> {
                colour = Color.GREEN;
                innerRadius = 0.5 * outerRadius;
            }
            case S -> {
                colour = Color.RED;
                innerRadius = 0.7 * outerRadius;
            }
            case G2 -> {
                colour = Color.YELLOW;
                innerRadius = 0.8 * outerRadius;
            }
            case M -> {
                colour = Color.CYAN;
                innerRadius = outerRadius;
            }
            case G0 -> {
                colour = Color.GRAY;
                innerRadius = outerRadius;
            }
        }
    }

    public void changePhase(){
        t+=1;
        double chance = rand.nextDouble();
        if(t>120 && phase == cellPhase.G1 && chance<0.07){
            phase = cellPhase.S;
        } else if (t>200 && phase == cellPhase.S && chance<0.08) {
            phase = cellPhase.G2;
        } else if (t>240 && phase == cellPhase.G2 && chance<0.1) {
            phase = cellPhase.M;
        } else if (t>260 && phase == cellPhase.M && chance<0.06) {
            t=1;
            phase = cellPhase.G1;
            try {
                addChildCell();
            } catch (OutOfSpaceException e) {
                phase = cellPhase.G0;
                t=261;
            }
        }
        phases();
    }

    protected synchronized void addChildCell() throws OutOfSpaceException{
        Cell addedCell = null;
        for(int i = 0; i < 1000; i++) {
            addedCell = new Cell(radius, this);
            if (newCellList.parallelStream().anyMatch(addedCell::isOverlapping)) addedCell = null;
            else break;
        }
        if (addedCell == null) throw new OutOfSpaceException("Child cell no spawn me cry");
        newCellList.add(addedCell);
        numberOfdivisions++;
        if(numberOfdivisions>=5) killSelf();
    }

    private synchronized void killSelf(){
        hitList.add(this);
    }


    @Override
    public void draw(){
        changePhase();
        pen.drawCircle((int)x,(int)y,(int)radius,Color.WHITE, false);
        pen.drawCircle((int)x,(int)y,(int) innerRadius,colour, true);
    }
}
