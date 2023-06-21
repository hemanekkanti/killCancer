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
    private double dx;
    private double dy;
    private final Random rand = new Random();
    private static double xSize;
    private static double ySize;
    private int t;
    private static double lowerEdge;
    private static double upperEdge;

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

    private boolean onInnerEdge(){
        return y > (int) (ySize/2-150/2) || y < (int) (ySize/2+150/2);
    }

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
            randomizeDirection();
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
            x = rand.nextDouble(radius, xSize - radius);
            boolean chance = rand.nextBoolean();
            y = chance ? rand.nextDouble(radius, lowerEdge) : rand.nextDouble(upperEdge, ySize - radius);
            if (restrain.parallelStream().noneMatch(this::isOverlapping)) return;
        }
        throw new OutOfSpaceException("Particle could not be spawned :'(");
    }
    private void randomizeDirection() {
        dx = rand.nextDouble(-5, 5);
        dy = rand.nextDouble(-5, 5);
    }

    private double separationDistance(Particle p){
        return Math.hypot((this.x-p.x),(this.y-p.y));
    }

    public boolean isOverlapping(Particle p){
        if (p!=this) return separationDistance(p)<= radius + p.radius;
        else return false;
    }
    public void draw(){
        pen.drawCircle((int)x,(int)y,(int) radius,Color.WHITE, false);
        //pen.drawCircle((int)x,(int)y,(int) (0.7*radius),Color.RED, true);
    }

    public static class OutOfSpaceException extends Exception{
        public OutOfSpaceException(String s) {
        }
    }
}
