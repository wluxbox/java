package spaceinv.model;

import spaceinv.event.EventHandler;
import spaceinv.event.ModelEvent;
import spaceinv.model.functions.Shooter;

public abstract class ShooterEntity extends Entity implements Shootable,EventHandler {

    private Projectile projectile;
    private double projectileSpeed;

    public ShooterEntity(double x, double y, double width, double height, double projectileSpeed) {
        super(x, y, width, height);
        this.projectileSpeed = projectileSpeed;
    }

    public boolean projectileIsPresent(){
        return projectile != null;
    } //TODO do not forget to fix this


    public Projectile fire() {
        projectile = Shooter.fire(this, projectileSpeed);
        return projectile;
    }

    public void removeProjectile(){
        projectile = null;
    }

    public Projectile getProjectile() {
        return projectile;
    }



    @Override
    public void onModelEvent(ModelEvent evt) {
        if(evt.data == projectile){
            if(evt.type == ModelEvent.Type.BOMB_HIT_GROUND || evt.type == ModelEvent.Type.OUT_OF_BOUNDS){
                removeProjectile();
            }
        }
    }
}
