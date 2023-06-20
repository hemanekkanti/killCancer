import java.awt.*;

public class Cancer extends Cell{
    private int outerR = 35;

    public Cancer(double radius) throws OutOfSpaceException {
        super(radius);
        phase = spawnPhase();
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
        if(t==60){
            phase = cellPhase.S;
        } else if (t==100) {
            phase = cellPhase.G2;
        } else if (t==140) {
            phase = cellPhase.M;
        } else if (t==150) {
            phase = cellPhase.G1;
            t=1;
        }
        phases();
    }

    @Override
    public void draw(){
        changePhase();
        pen.drawCircle((int)x,(int)y,30, Color.WHITE, false);
        for(int i=0; i<12; i++){
            double theta = i*(Math.PI/6);
            double x2 = x+outerR * Math.cos(theta);
            double y2 = y+outerR * Math.sin(theta);
            pen.drawLine((int)x,(int)y,(int)x2,(int)y2,Color.WHITE);
        }
        pen.drawCircle((int)x,(int)y,(int) radius, colour, true);
    }
}
