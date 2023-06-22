import java.util.List;

public class Chemo extends Particle{
    public static List<Chemo> drugsList;

    private int pulseTimer = -1;

    private int lifetime_remaining = (int) rand.nextGaussian(500, 300);
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
        y = ySize / 2;
    }

    @Override
    protected void randomizeDirection() {
        dx = rand.nextDouble(-8, 8);
        dy = rand.nextDouble(-8, 8);
        if (isInsideVessel()) {
            dx += 2;
            dy *= 1 - (x/(xSize*2));
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

    @Override
    protected int drawRadius() {

        if (pulseTimer == -1) return super.drawRadius();
        if (pulseTimer++ >= 30) {
            pulseTimer = -1;
            return super.drawRadius();
        }
        return (int) (radius + 50 * Math.sin(Math.PI * ((pulseTimer-1) / 30.0)));

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

    public void pulsate() {if (pulseTimer == -1) pulseTimer = 0;}
}
