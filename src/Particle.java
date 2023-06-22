import java.awt.Color;
import nano.*;

import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class Particle {
    protected double radius;
    private static final double ANIMATION_TIME = 30;
    protected static Pen pen;
    protected double x;
    protected double y;
    protected double dx;
    protected double dy;
    protected final static Random rand = new Random();
    protected static double xSize;
    protected static double ySize;
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
    public static void setDimensions(double xsize, double ysize) {
        Particle.xSize = xsize;
        Particle.ySize = ysize;
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

    protected boolean isInsideVessel(){
        return y > BloodVessel.getLowerEdge()+radius && y < BloodVessel.getUpperEdge()-radius;
    }
    private boolean onOuterEdge(){
        return (onLowerEdge() || onUpperEdge());
    }
    private boolean onLowerEdge(){
        return (y <= BloodVessel.getLowerEdge()+radius && y >= BloodVessel.getLowerEdge()-radius);
    }
    private boolean onUpperEdge(){
        return (y >= BloodVessel.getUpperEdge()-radius && y <= BloodVessel.getUpperEdge()+radius);
    }

    public void bounceVessel() {
        if(onOuterEdge() && !(this instanceof Cancer) && !gravestone){
            dy = -dy;
            if(onLowerEdge()) y=BloodVessel.getLowerEdge()-radius;
            else y=BloodVessel.getUpperEdge()+radius;
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
            y = chance ? rand.nextDouble(radius, BloodVessel.getLowerEdge() - radius) :
                    rand.nextDouble(BloodVessel.getUpperEdge() + radius, ySize - radius);
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
            dx = 5*Math.sin(8*Math.PI*y/ySize);
            dy += 0.1;
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
    public void draw(){
        pen.drawCircle(XtoDraw(), YtoDraw(), radiusToDraw(),new Color(1f,1f,1f,0.5f), true);
        animationProgress();
    }
    protected void animationProgress(){
        if (animationPhase==-1) return;
        if(++animationPhase >= ANIMATION_TIME) animationPhase = -1;
    }
    protected double getAnimationRatio(){
        if (animationPhase==-1) return 1;
        return (animationPhase/ANIMATION_TIME);
    }
    protected int radiusToDraw(){
        return (int) (radius*getAnimationRatio());
    }
    protected int XtoDraw(){
        if (animationPhase==-1 || parent==null) return (int) x;
        return (int) (parent.x+(x-parent.x)*getAnimationRatio());
    }
    protected int YtoDraw(){
        if (animationPhase==-1 || parent==null) return (int) y;
        return (int) (parent.y+(y-parent.y)*getAnimationRatio());
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
