import java.awt.*;

public class Cancer extends Cell{
    private static Color BGCOLOUR = new Color(0.0f,0.0f,0.0f,0.5f);

    public Cancer(double radius) throws OutOfSpaceException {
        super(radius);
        phase = spawnPhase();
        phases();
    }

    private Cancer(double radius, Cancer parent) throws OutOfSpaceException{
        super(radius, parent);
        phase = cellPhase.G1;
        phases();
    }

    @Override
    protected cellPhase spawnPhase(){
        int stage = rand.nextInt(4);
        switch (stage){
            case 0 -> {t=1;}
            case 1 -> {t=60;}
            case 2 -> {t=100;}
            case 3 -> {t=140;}
        }
        return cellPhase.values()[stage];
    }
    @Override
    public void changePhase(){
        t+=1;
        double chance = rand.nextDouble();
        if(t>=60 && phase == cellPhase.G1 && chance<0.1){
            phase = cellPhase.S;
        } else if (t>=90 && phase == cellPhase.S && chance<0.12) {
            phase = cellPhase.G2;
        } else if (t>=120 && phase == cellPhase.G2 && chance<0.2) {
            phase = cellPhase.M;
        } else if (t>=130 && phase == cellPhase.M && chance<0.08) {
            t=1;
            phase = cellPhase.G1;
            try {
                addChildCell();
            } catch (OutOfSpaceException e) {
                phase = cellPhase.G2;
                t=90;
            }
        }
        phases();
    }

    @Override
    public void draw(){
        //changePhase(); // moved to simulation
        pen.drawCircle(XtoDraw(),YtoDraw(),radiusToDraw(),BGCOLOUR , true);
        pen.drawCircle(XtoDraw(),YtoDraw(),radiusToDraw(), colour, false);

        //spikes for the cancer cells
        int numSpikes = 12;
        for(int i=0; i<numSpikes; i++){
            double theta = i*(2*Math.PI/numSpikes);
            int x2 = (int)(XtoDraw()+1.2*radiusToDraw() * Math.cos(theta));
            int y2 = (int) (YtoDraw()+1.2*radiusToDraw() * Math.sin(theta));
            pen.drawLine(XtoDraw(),YtoDraw(), x2, y2, colour);
            //pen.drawLine(x2+1,y2, x2-1, y2+1, colour);

        }

        //inner nucleus
        int innerR = (int) (innerRadius * radiusToDraw()/radius);
        pen.drawCircle(XtoDraw(),YtoDraw(),innerR, colour, true);
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
