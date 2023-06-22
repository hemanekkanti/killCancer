import java.awt.*;

public class Cancer extends Cell{

    public Cancer(double radius) throws OutOfSpaceException {
        super(radius);
        phase = spawnPhase();
        lastPhase = phase;
    }

    private Cancer(double radius, Cancer parent) throws OutOfSpaceException{
        super(radius, parent);
        phase = CellPhase.G1;
        lastPhase = phase;
    }

    @Override
    public void bounceScreen() {
        if (!isInsideVessel()) super.bounceScreen();
        if (x > xSize + radius) killSelf();
    }

    @Override
    protected CellPhase spawnPhase(){
        int stage = rand.nextInt(4);
        switch (stage){
            case 0 -> {t=1;}
            case 1 -> {t=60;}
            case 2 -> {t=100;}
            case 3 -> {t=140;}
        }
        return CellPhase.values()[stage];
    }
    @Override
    public void changePhase(){
        t+=1;
        double chance = rand.nextDouble();
        if(t>=60 && phase == CellPhase.G1 && chance<0.07){
            phase = CellPhase.S;
        } else if (t>=90 && phase == CellPhase.S && chance<0.08) {
            phase = CellPhase.G2;
        } else if (t>=120 && phase == CellPhase.G2 && chance<0.1) {
            phase = CellPhase.M;
        } else if (t>=140 && phase == CellPhase.M && chance<0.06) {
            t=1;
            phase = CellPhase.G1;
            try {
                addChildCell();
            } catch (OutOfSpaceException e) {
                phase = CellPhase.G2;
                t=90;
            }
        }
    }

    @Override
    public void draw(){
        phaseTransition();
        //changePhase(); // moved to simulation
        pen.drawCircle(XtoDraw(),YtoDraw(),radiusToDraw(), Color.WHITE, false);

        //spikes for the cancer cells
        int numSpikes = 12;
        for(int i=0; i<numSpikes; i++){
            double theta = i*(2*Math.PI/numSpikes);
            int x2 = (int)(XtoDraw()+1.2*radiusToDraw() * Math.cos(theta));
            int y2 = (int) (YtoDraw()+1.2*radiusToDraw() * Math.sin(theta));
            pen.drawLine(XtoDraw(),YtoDraw(), x2, y2, Color.WHITE);
        }

        //inner nucleus
        int innerR = (int) (innerRadiusToDraw() * radiusToDraw()/radius);
        pen.drawCircle(XtoDraw(),YtoDraw(),innerR, colorToDraw(), true);
        animationProgress();
    }

    @Override
    protected Cell createChild() throws OutOfSpaceException {
        return new Cancer(radius, this);
    }

    @Override
    public boolean thatsMySPOT() {
        if (x <= radius || x >= xSize-radius) return false;
        if (y <= radius || y >= ySize-radius) return false;
        return true;
    }
}
