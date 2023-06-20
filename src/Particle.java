import java.awt.Color;
import nano.*;

import java.util.List;
import java.util.Random;

public class Particle {
    protected double radius;
    private Color colour;
    protected static Pen pen;
    protected double x;
    protected double y;
    private double dx;
    private double dy;
    private Canvas screen;
    private final Random rand = new Random();
    private static double xSize;
    private static double ySize;
    private boolean canBounce = true;
    private int t;
    private static double lowerEdge;
    private static double upperEdge;

    private static List<? extends Particle> restrain;

    public double getRadius(){
        return radius;
    }

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
        return (y <= lowerEdge && y >= lowerEdge-20);
    }
    private boolean onUpperEdge(){
        return (y >= upperEdge && y <= upperEdge+20);
    }

    public void bounceVessel() {
        if(onOuterEdge()){
            dy = -dy;
            if(onLowerEdge()) y=lowerEdge-20;
            else y=upperEdge+20;
        }
    }

    public void changeDirection(){
        dx = -dx;
        dy = -dy;
    }


    public void bounceScreen() {
        canBounce = true;
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
        for (int tries = 0; tries<1000; tries++) {
            x = rand.nextDouble(radius, xSize - radius);
            int chance = rand.nextInt(2);
            y = chance == 0 ? rand.nextDouble(radius, ySize / 2 - (double) 150 / 2 - radius) : rand.nextDouble(ySize / 2 + (double) 150 / 2 + radius, ySize - radius);
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
        if (p!=this) return separationDistance(p)<=2*30;
        else return false;
    }
    public void draw(){
        pen.drawCircle((int)x,(int)y,(int) radius,Color.WHITE, false);
        //pen.drawCircle((int)x,(int)y,(int) (0.7*radius),Color.RED, true);
    }
}
