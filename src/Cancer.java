import java.awt.*;

public class Cancer extends Cell{

    public Cancer(double radius) throws OutOfSpaceException {
        super(radius);
        phase = spawnPhase();
        phases();
    }

    private Cancer(double radius, Cancer parent) throws OutOfSpaceException{
        super(radius);
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
        if(t==60 && chance<0.7){
            phase = cellPhase.S;
        } else if (t==120 && chance<0.8) {
            phase = cellPhase.G2;
        } else if (t==170 && chance<0.9) {
            phase = cellPhase.M;
        } else if (t==180) {
            t=1;
            phase = cellPhase.G1;
            try {
                addChildCell();
            } catch (OutOfSpaceException e) {
                phase = cellPhase.G2;
                t=120;
            }
        }
        phases();
    }

    @Override
    public void draw(){
        changePhase();
        pen.drawCircle((int)x,(int)y,(int)radius, Color.WHITE, false);
        int numSpikes = 12;
        for(int i=0; i<numSpikes; i++){
            double theta = i*(2*Math.PI/numSpikes);
            double x2 = x+1.2*radius * Math.cos(theta);
            double y2 = y+1.2*radius * Math.sin(theta);
            pen.drawLine((int)x,(int)y,(int)x2,(int)y2,Color.WHITE);
        }
        pen.drawCircle((int)x,(int)y,(int) innerRadius, colour, true);
    }

    @Override
    protected synchronized void addChildCell() throws OutOfSpaceException{
        Cell addedCell = null;
        for(int i = 0; i < 1000; i++) {
            addedCell = new Cancer(radius, this);
            if (newCellList.stream().anyMatch(addedCell::isOverlapping)) addedCell = null;
            else break;
        }
        if (addedCell == null) throw new OutOfSpaceException("Child cell no spawn me cry");
        newCellList.add(addedCell);
    }
}
