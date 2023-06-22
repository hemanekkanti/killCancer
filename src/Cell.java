import java.awt.*;
import java.util.List;

public class Cell extends Particle{
    protected Color colour = Color.BLUE;
    protected CellPhase phase;
    protected static List<Cell> newCellList;
    public static void setNewCellList(List<Cell> l) {newCellList = l;}
    protected int t;

    protected CellPhase lastPhase = CellPhase.G0;

    private int phaseTransitionTime = 20;

    private int numberOfdivisions;

    public Cell(double radius) throws OutOfSpaceException {
        super(radius);
        phase = spawnPhase();
        lastPhase = phase;
    }

    protected Cell(double radius, Cell parent) throws OutOfSpaceException {
        super(radius, parent);
        phase = CellPhase.G1;
        lastPhase = phase;
    }
    protected enum CellPhase{
        G1,
        S,
        G2,
        M,
        G0;
    }

    protected CellPhase spawnPhase(){
        int stage = rand.nextInt(4);
        switch (stage){
            case 0 -> {t=1;}
            case 1 -> {t=120;}
            case 2 -> {t=200;}
            case 3 -> {t=240;}
        }
        return CellPhase.values()[stage];
    }

    public double phaseRadius(CellPhase phase){
        switch (phase) {
            case G1 -> {return 0.5 * radius;}
            case S -> {return 0.7 * radius;}
            case G2 -> {return 0.8 * radius;}
            case M, G0 -> {return radius;}
            default -> {throw new IllegalStateException("No phase");}
        }
    }

    public Color phaseColor(CellPhase phase){
        switch (phase) {
            case G1 -> {return Color.GREEN;}
            case S -> {return Color.RED;}
            case G2 -> {return Color.YELLOW;}
            case M -> {return Color.CYAN;}
            case G0 -> {return Color.GRAY;}
            default -> {throw new IllegalStateException("No phase");}
        }
    }

    public void changePhase(){
        t+=1;
        double chance = rand.nextDouble();
        if(t>120 && phase == CellPhase.G1 && chance<0.07){
            phase = CellPhase.S;
        } else if (t>200 && phase == CellPhase.S && chance<0.08) {
            phase = CellPhase.G2;
        } else if (t>240 && phase == CellPhase.G2 && chance<0.1) {
            phase = CellPhase.M;
        } else if (t>260 && phase == CellPhase.M && chance<0.06) {
            t=1;
            phase = CellPhase.G1;
            try {
                addChildCell();
            } catch (OutOfSpaceException e) {
                phase = CellPhase.G0;
                t=261;
            }
        }
    }

    protected Cell createChild() throws OutOfSpaceException {
        return new Cell(radius, this);
    }

    protected synchronized void addChildCell() throws OutOfSpaceException{
        Cell addedCell = null;
        for(int i = 0; i < 1000; i++) {
            addedCell = createChild();
            if (newCellList.stream().anyMatch(addedCell::isOverlapping)) addedCell = null;
            else break;
        }
        if (addedCell == null) throw new OutOfSpaceException("Child cell no spawn me cry");
        newCellList.add(addedCell);
        numberOfdivisions++;
        if(numberOfdivisions>=5) killSelf();
    }


    @Override
    public void draw(){
        phaseTransition();
        //changePhase();  // moved to simulation
        int innerR = (int) (innerRadiusToDraw()*radiusToDraw()/radius);
        pen.drawCircle(XtoDraw(),YtoDraw(),radiusToDraw(),Color.WHITE, false);
        pen.drawCircle(XtoDraw(),YtoDraw(),innerR,colorToDraw(), true);
        animationProgress();
    }

    public boolean vulnerableToChemo() {
        return phase == CellPhase.M;
    }

    @Override
    public boolean mustDie() {
        return gravestone && y >= ySize+radius;
    }

    protected void phaseTransition() {
        if (phaseTransitionTime == 0 && phase != lastPhase) phaseTransitionTime = 21;
        if (phaseTransitionTime == 1) lastPhase = phase;
        if (phaseTransitionTime >0) phaseTransitionTime--;
    }

    protected Color colorToDraw() {
        float t = phaseTransitionTime /20f;
        Color color2 = phaseColor(phase);
        Color color1 = phaseColor(lastPhase);
        return new Color(
                (int) (color1.getRed() *t + color2.getRed() *(1f-t)),
                (int) (color1.getGreen() *t + color2.getGreen() *(1f-t)),
                (int) (color1.getBlue() *t + color2.getBlue() *(1f-t))
        );
    }

    protected int innerRadiusToDraw() {
        float t = phaseTransitionTime /20f;
        double r2 = phaseRadius(phase);
        double r1 = phaseRadius(lastPhase);
        return (int) (r1 *t + r2 *(1f-t));
    }
}
