package spaceinv.model;


import org.w3c.dom.Entity;

import static spaceinv.model.SI.*;

/*
    The ground where the Gun lives.
    Used to check if projectiles from enemyships have hit the ground
 */
public class Ground implements Positionable {


    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return GAME_HEIGHT-this.getHeight();
    }

    @Override
    public double getWidth() {
        return GAME_WIDTH;
    }

    @Override
    public double getHeight() {
        return GROUND_HEIGHT;
    }

    @Override
    public void setX(double d) {

    }

    @Override
    public void setY(double d) {

    }
}
