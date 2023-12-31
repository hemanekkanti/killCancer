import java.awt.Color;
import nano.*;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class Particle {
    private static final double ANIMATION_TIME = 30;
    protected double radius;
    protected static Pen pen;
    protected double x;
    protected double y;
    protected double dx;
    protected double dy;
    protected final static Random rand = new Random();
    protected static double xSize;
    protected static double ySize;
    protected static double lowerEdge;
    protected static double upperEdge;
    protected boolean gravestone;
    private Particle parent;
    private int animationPhase;

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
    public static void setDimensions(double xsize, double ysize, double lowerEdge, double thickness) {
        Particle.xSize = xsize;
        Particle.ySize = ysize;
        Particle.lowerEdge = lowerEdge;
        Particle.upperEdge = lowerEdge+thickness;
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
        if(onOuterEdge() && !(this instanceof Cancer)){
            dy = -dy;
            if(onLowerEdge()) y=lowerEdge-radius;
            else y=upperEdge+radius;
        }
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
        return !isInsideVessel() && !onOuterEdge();
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
        pen.drawCircle(XtoDraw(), YtoDraw(), (int) radiusToDraw(),new Color(1f,1f,1f,0.5f), true);
        animateRadius();
    }
    protected void animateRadius(){
        if (animationPhase==-1) return;
        if(++animationPhase >= ANIMATION_TIME) animationPhase = -1;
    }
    protected double getAnimationRatio(){
        if (animationPhase==-1) return 1;
        return (animationPhase/ANIMATION_TIME);
    }
    protected double radiusToDraw(){
        return  radius*getAnimationRatio();
    }
    protected int XtoDraw(){
        if (animationPhase==-1 || parent==null) return (int) x;
        return (int) (parent.x + (x-parent.x) * getAnimationRatio());
    }
    protected int YtoDraw(){
        if (animationPhase==-1 || parent==null) return (int) y;
        return (int) (parent.y + (y-parent.y) * getAnimationRatio());
    }

    public static class OutOfSpaceException extends Exception{
        public OutOfSpaceException(String s) {
        }
    }

    protected void killSelf() {gravestone=true;}
    public boolean mustDie() {return gravestone;}
}
