import java.awt.Color;
import nano.*;

import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class Particle {

    private static double ANIMATION_TIME = 20;
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
    private Particle parent;

    private int framesSinceSpawn;

    private static List<? extends Particle> restrain;

    public static void setRestrainList(List<? extends Particle> l) {restrain = l;}

    public Particle(double radius) throws OutOfSpaceException {
        this.radius = radius;
        //this.colour = colour;
        spawnParticle();
        randomizeDirection();
    }

    public Particle(double radius, Particle parent) throws OutOfSpaceException {
        this.radius = radius;
        this.parent = parent;
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
        return (y <= lowerEdge+radius && y >= lowerEdge-radius);
    }
    private boolean onUpperEdge(){
        return (y >= upperEdge-radius && y <= upperEdge+radius);
    }

    public void bounceVessel() {
        if(onOuterEdge() && !(this instanceof Cancer) && !gravestone){
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
        if (gravestone) return;
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

    protected void spawnParticle() throws OutOfSpaceException {
        for (int tries = 0; tries<100; tries++) {
            randomizePosition();
            if (restrain.stream().noneMatch(this::isOverlapping) ) return;
        }
        throw new OutOfSpaceException("Particle could not be spawned without overlapping :'(");
    }

    protected void randomizePosition() throws OutOfSpaceException {
        if (parent == null) {
            x = rand.nextDouble(radius, xSize - radius);
            boolean chance = rand.nextBoolean();
            y = chance ? rand.nextDouble(radius, lowerEdge - radius) : rand.nextDouble(upperEdge + radius, ySize - radius);
        } else {
            int i;
            for (i = 0; i < 120; i++) {
                double theta = rand.nextDouble(Math.PI * 2);
                x = parent.x + Math.cos(theta) * (parent.radius + this.radius + 1);
                y = parent.y + Math.sin(theta) * (parent.radius + this.radius + 1);
                if (thatsMySPOT()) break;
            }
            if (i == 120) throw new OutOfSpaceException("Particle could not be spawned even before overlapping :'(");
        }
    }

    public boolean thatsMySPOT() {
        if (x <= radius || x >= xSize-radius) return false;
        if (y <= radius || y >= ySize-radius) return false;
        if (isInsideVessel() || onOuterEdge()) return false;
        return true;
    }

    protected void randomizeDirection() {
        if (!gravestone) {
            dx = rand.nextDouble(-2, 2);
            dy = rand.nextDouble(-2, 2);
        } else {
            dx = 0;
            dy -= 0.1;
        }
    }

    private double separationDistance(Particle p){
        return Math.hypot((this.x-p.x),(this.y-p.y));
    }

    public boolean isOverlapping(Particle p){
        if (gravestone || p.gravestone) return false;
        if (p!=this) return separationDistance(p)<= radius + p.radius;
        else return false;
    }

    protected int drawRadius() {
        if (framesSinceSpawn == -1) return (int) radius;
        if (framesSinceSpawn > ANIMATION_TIME) {
            framesSinceSpawn = -1;
            return drawRadius();
        }
        return (int) (radius * ( ANIMATION_TIME / ++framesSinceSpawn));
    }

    protected int drawX() {
        if (framesSinceSpawn == -1 || parent == null) return (int) x;
        double factor = (framesSinceSpawn / ANIMATION_TIME);
        return (int) (x * factor + parent.x * (1 - factor));
    }
    protected int drawY() {
        if (framesSinceSpawn == -1 || parent == null) return (int) y;
        double factor = (framesSinceSpawn / ANIMATION_TIME);
        return (int) (y * factor + parent.y * (1 - factor));
    }

    public void draw(){
        pen.drawCircle(drawX(),drawY(), drawRadius(),Color.WHITE, true);
    }

    public static class OutOfSpaceException extends Exception{
        public OutOfSpaceException(String s) {
        }
    }

    protected void killSelf() {
        gravestone=true;
        dy=0;
    }
    public boolean mustDie() {
        return gravestone;
    }
}
