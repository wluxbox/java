package spaceinv.model;

import com.sun.webkit.Timer;
import spaceinv.event.EventHandler;
import spaceinv.event.ModelEvent;

import static spaceinv.model.SI.*;

/*
       A projectile fired from the Gun or dropped by a spaceship

       This class should later be refactored (and inherit most of what it needs)
 */
//TODO make entity
public class Projectile extends Entity implements StepAble {

    private double dy;

    public Projectile(double x, double y, double width, double height, double dy ) {
        super(x,y,width,height);
        this.dy = dy;
    }

    public Projectile(double dy) {
        this(0, 0, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, dy);
        //IGNORE::: super(0, 0, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, 0, dy);
    }
    public Projectile(Positionable p, double dy) {
        this(p.getX(), p.getY(), PROJECTILE_WIDTH, PROJECTILE_HEIGHT, dy);
    }

    public void step() {
        setY(getY() - getDy());
        setDy(PROJECTILE_ACCELERATION * getDy());
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }
}
