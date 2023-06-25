import java.awt.*;
import java.util.List;

public class Cell extends Particle{
    protected final static int PHASE_CHANGE_TIME = 1;
    protected CellPhase phase;

    protected static List<Cell> newCellList;

    public static void setNewCellList(List<Cell> l) {newCellList = l;}

    protected int t;

    protected CellPhase lastPhase;

    private int phaseTransitionTime = PHASE_CHANGE_TIME;

    private int numberOfdivisions;

    public Cell(double radius) throws OutOfSpaceException {
        super(radius);
        phase = spawnPhase();
        lastPhase = phase;
    }

    protected Cell(double radius, Cell parent) throws OutOfSpaceException {
        super(radius, parent);
        phase = CellPhase.G1;
        lastPhase=phase;
    }
    protected enum CellPhase {
        G1(0.5, new Color(86,102,165)),
        S(0.65, new Color(155,171,183)),
        G2(0.8, new Color(215,191,119)),
        M(0.95, new Color(255, 99, 44)),
        G0(0.95, new Color(33,12,72));
        final double radius;
        final Color colour;

        CellPhase(double radius, Color colour){
            this.radius = radius;
            this.colour = colour;
        }
    }

    protected CellPhase spawnPhase(){
        int stage = rand.nextInt(4);
        switch (stage){
            case 0 -> t=1;
            case 1 -> t=120;
            case 2 -> t=200;
            case 3 -> t=240;
        }
        return CellPhase.values()[stage];
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
        } else if (t>255 && phase == CellPhase.M && chance<0.06) {
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
        animatePhase();
        pen.drawCircle(XtoDraw(),YtoDraw(), (int) radiusToDraw(),colourToDraw().darker().darker(), true);
        pen.drawCircle(XtoDraw(),YtoDraw(),innerRadiusToDraw(),colourToDraw(), true);
        animateRadius();
    }

    public boolean vulnerableToChemo() {
        return phase == CellPhase.M;
    }

    protected void animatePhase(){
        //reactivate animation if the phase changes
        if(phaseTransitionTime == 0 && phase != lastPhase) phaseTransitionTime = PHASE_CHANGE_TIME+1;
        //finish animation by setting the current phase as the last phase
        if (phaseTransitionTime == 1) lastPhase = phase;
        //phase transition must be between 0 and 20
        if(phaseTransitionTime > 0) phaseTransitionTime--;
    }

    protected Color colourToDraw(){
        float t = phaseTransitionTime/(float)PHASE_CHANGE_TIME;
        Color currentColour = phase.colour;
        Color previousColour = lastPhase.colour;
        return new Color(
                (int) (previousColour.getRed()*t + currentColour.getRed()*(1f-t)),
                (int) (previousColour.getGreen()*t + currentColour.getGreen()*(1f-t)),
                (int) (previousColour.getBlue()*t + currentColour.getBlue()*(1f-t))
        );
    }

    protected int innerRadiusToDraw(){
        float t = phaseTransitionTime/(float)PHASE_CHANGE_TIME;
        double currentRadius = phase.radius;
        double previousRadius = lastPhase.radius;
        return (int) (radiusToDraw()*((currentRadius*(1f-t))+(previousRadius*t)));
    }
}
