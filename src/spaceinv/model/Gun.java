package spaceinv.model;


import spaceinv.event.EventHandler;
import spaceinv.event.ModelEvent;

/*
 *    A Gun for the game
 *    Can only fire one projectile at the time
 */
public class Gun extends ShooterEntity implements EventHandler {
    double dx = 0;

    public Gun(double x, double y, double width, double height, double projectileSpeed) {
        super(x, y, width, height, projectileSpeed);
    }

    public void move() {
        setX(getX()+dx);
    }

    public void setXspeed(double dx) { this.dx = dx; }
    public double getXspeed() { return dx; }

    @Override
    public void onModelEvent(ModelEvent evt) {
        super.onModelEvent(evt);

        if(evt.type == ModelEvent.Type.GUN_HIT_SHIP){
            removeProjectile();
        }
    }

    public enum GunDirection {LEFT,RIGHT}
}
