public class Chemo extends Particle{

    public Chemo(double radius) throws OutOfSpaceException {
        super(radius);
    }

    @Override
    protected void randomizePosition() {
        x=5;
        y = ySize / 2;
    }

    @Override
    protected void randomizeDirection() {
        dx = rand.nextDouble(-1, 10);
        dy = rand.nextDouble(-10, 10);
    }

}
