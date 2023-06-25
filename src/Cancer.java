import java.awt.*;

public class Cancer extends Cell{
    private static final Color BGCOLOUR = new Color(0.0f,0.0f,0.0f,0.5f);

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
        if(t>=40 && phase == CellPhase.G1 && chance<0.1){
            phase = CellPhase.S;
        } else if (t>=80 && phase == CellPhase.S && chance<0.12) {
            phase = CellPhase.G2;
        } else if (t>=110 && phase == CellPhase.G2 && chance<0.2) {
            phase = CellPhase.M;
        } else if (t>=130 && phase == CellPhase.M && chance<0.08) {
            t=1;
            phase = CellPhase.G1;
            try {
                addChildCell();
            } catch (OutOfSpaceException e) {
                phase = CellPhase.G2;
                t=80;
            }
        }
    }

    @Override
    public void draw(){
        animatePhase();
        Color colour = colourToDraw();
        //changePhase(); // moved to simulation
        pen.drawCircle(XtoDraw(),YtoDraw(), (int) radiusToDraw(),BGCOLOUR , true);
        pen.drawCircle(XtoDraw(),YtoDraw(),(int) radiusToDraw(), colour, false);

        //spikes for the cancer cells
        int numSpikes = 12;
        for(int i=0; i<numSpikes; i++){
            double theta = i*(2*Math.PI/numSpikes);
            int x2 = (int)(XtoDraw()+1.2*radiusToDraw() * Math.cos(theta));
            int y2 = (int) (YtoDraw()+1.2*radiusToDraw() * Math.sin(theta));
            pen.drawLine(XtoDraw(),YtoDraw(), x2, y2, colour);

        }

        //inner nucleus
        pen.drawCircle(XtoDraw(),YtoDraw(),innerRadiusToDraw(), colour, true);
        animateRadius();
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
