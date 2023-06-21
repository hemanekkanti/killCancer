import java.awt.Color;
import nano.*;

import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class Particle {
    protected double radius;
    private Color colour;
    protected static Pen pen;
    protected double x;
    protected double y;
    protected double dx;
    protected double dy;
    protected final Random rand = new Random();
    protected static double xSize;
    protected static double ySize;
    protected int t;
    protected static double lowerEdge;
    protected static double upperEdge;
    protected boolean gravestone;

    private static List<? extends Particle> restrain;

    public static void setRestrainList(List<? extends Particle> l) {restrain = l;}

    public Particle(double radius) throws OutOfSpaceException {
        this.radius = radius;
        //this.colour = colour;
        spawnParticle();
        randomizeDirection();
    }
    public static void setDimensions(double xsize, double ysize, double lowerEdge, double upperEdge) {
        Particle.xSize = xsize;
        Particle.ySize = ysize;
        Particle.lowerEdge = lowerEdge;
        Particle.upperEdge = upperEdge;
    }

    public static void setPen(Pen pen) {
        Particle.pen = pen;
    }

    public void move(){
        randomizeDirection();
        bounceScreen();
        bounceVessel();
        x += dx;
        y += dy;
    }

    public void unmove(){
        x -= dx;
        y -= dy;
    }

    protected boolean isInsideVessel(){ return y > lowerEdge+radius && y < upperEdge-radius; }
    private boolean onOuterEdge(){
        return (onLowerEdge() || onUpperEdge());
    }
    private boolean onLowerEdge(){
        return (y <= lowerEdge && y >= lowerEdge-radius);
    }
    private boolean onUpperEdge(){
        return (y >= upperEdge && y <= upperEdge+radius);
    }

    public void bounceVessel() {
        if(onOuterEdge() && !(this instanceof Cancer)){
            dy = -dy;
            if(onLowerEdge()) y=lowerEdge-radius;
            else y=upperEdge+radius;
        }
    }

    public void changeDirection(){
        dx = -dx;
        dy = -dy;
    }


    public void bounceScreen() {
        if (x <= radius || x >= xSize-radius) {
            dx = -dx;
            if (x <= radius) x = radius;
            else x = xSize-radius;
        }
        if (y <= radius || y >= ySize-radius) {
            dy = -dy;
            if (y <= radius) y = radius;
            else y = ySize-radius;
        }
    }

    private void spawnParticle() throws OutOfSpaceException {
        for (int tries = 0; tries<100; tries++) {
            randomizePosition();
            if (restrain.stream().noneMatch(this::isOverlapping)) return;
        }
        throw new OutOfSpaceException("Particle could not be spawned :'(");
    }

    protected void randomizePosition() {
        x = rand.nextDouble(radius, xSize - radius);
        boolean chance = rand.nextBoolean();
        y = chance ? rand.nextDouble(radius, lowerEdge-radius) : rand.nextDouble(upperEdge+radius, ySize - radius);
    }

    protected void randomizeDirection() {
        dx = rand.nextDouble(-2, 2);
        dy = rand.nextDouble(-2, 2);
    }

    private double separationDistance(Particle p){
        return Math.hypot((this.x-p.x),(this.y-p.y));
    }

    public boolean isOverlapping(Particle p){
        if (p!=this) return separationDistance(p)<= radius + p.radius;
        else return false;
    }
    public void draw(){
        pen.drawCircle((int)x,(int)y,(int) radius,Color.WHITE, true);
    }

    public static class OutOfSpaceException extends Exception{
        public OutOfSpaceException(String s) {
        }
    }

    protected void killSelf() {gravestone=true;}
    public boolean mustDie() {return gravestone;}
}
