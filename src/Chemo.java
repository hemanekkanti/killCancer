import java.util.List;
import java.util.Random;

public class Chemo extends Particle{
    public static List<Chemo> drugsList;

    private int lifetime_remaining = (int) rand.nextInt(500, 800);

    public Chemo(double radius) throws OutOfSpaceException {
        super(radius);
    }
    public static void setDrugsList(List<Chemo> d) {
        drugsList = d;
    }

    @Override
    protected void spawnParticle() {
        x=5;
        y = rand.nextDouble(BloodVessel.getLowerEdge()+radius, BloodVessel.getUpperEdge()-radius);
    }

    @Override
    protected void randomizeDirection() {
        dx = rand.nextDouble(-8, 8);
        dy = rand.nextDouble(-8, 8);
        if (isInsideVessel()) {
            dx += 2;
            // not needed; distribution is now more even
            //dy *= 1 - (x/(xSize*2));
        } else {
            if (y < ySize/2) dy -= 0.5;
            else dy += 0.5;
        }
    }

    @Override
    public void move() {
        super.move();
        if (!isInsideVessel()) lifetime_remaining--;
        if (x >= xSize || lifetime_remaining <= 0) killSelf();
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
