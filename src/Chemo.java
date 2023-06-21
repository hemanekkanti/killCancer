import java.util.List;

public class Chemo extends Particle{
    public static List<Chemo> drugsList;

    public Chemo(double radius) throws OutOfSpaceException {
        super(radius);
    }
    public static void setDrugsList(List<Chemo> d) {
        drugsList = d;
    }

    @Override
    protected void randomizePosition() {
        x=5;
        y = ySize / 2;
    }

    @Override
    protected void randomizeDirection() {
        dx = rand.nextDouble(-8, 8);
        dy = rand.nextDouble(-8, 8);
        if (isInsideVessel()) {
            dx += 2;
            dy *= 1 - (x/(xSize*2));
        }
    }

    @Override
    public void move() {
        super.move();
        if (x >= xSize) killSelf();
    }

    public static void injectDrugs(int n) {
        for(int i=0; i<n; i++) {
            try {
                drugsList.add(new Chemo(10));
            } catch (OutOfSpaceException e) {
                throw new RuntimeException("No space for chemo, even though it can overlap");
            }
        }
    }
    @Override
    public void bounceScreen(){
        if (!isInsideVessel()) super.bounceScreen();
    }

    @Override
    public void bounceVessel(){}
}
