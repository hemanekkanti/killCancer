import java.util.List;

public class Chemo extends Particle {
    public static List<Chemo> drugsList;
    public static boolean started;
    private int lifetime_remaining = rand.nextInt(600, 800);

    public Chemo(double radius) throws OutOfSpaceException {
        super(radius);
    }

    public static void setDrugsList(List<Chemo> d) {
        drugsList = d;
    }

    public static void injectDrugs(int n) {
        started = true;
        for (int i = 0; i < n; i++) {
            try {
                drugsList.add(new Chemo(8));
            } catch (OutOfSpaceException e) {
                throw new RuntimeException("No space for chemo, even though it can overlap");
            }
        }
    }

    @Override
    protected void spawnParticle() {
        x = 5;
        y = rand.nextDouble(lowerEdge + radius, upperEdge - radius);
    }

    @Override
    protected void randomizeDirection() {
        dx = rand.nextDouble(-10, 10);
        dy = rand.nextDouble(-10, 10);
        if (isInsideVessel()) {
            dx += 3;
        } else {
            if (y < ySize / 2) dy -= 0.35;
            else dy += 0.35;
        }
    }

    @Override
    public void move() {
        super.move();
        if (!isInsideVessel()) lifetime_remaining--;
        if (x >= xSize || lifetime_remaining == 0) killSelf();
    }

    @Override
    public void bounceScreen() {
        if (!isInsideVessel()) super.bounceScreen();
    }

    @Override
    public void bounceVessel() {
    }
}
