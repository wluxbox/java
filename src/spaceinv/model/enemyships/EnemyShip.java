package spaceinv.model.enemyships;

import spaceinv.event.EventHandler;
import spaceinv.event.ModelEvent;
import spaceinv.model.Positionable;
import spaceinv.model.SI;
import spaceinv.model.ShooterEntity;
import spaceinv.model.StepAble;

import java.lang.management.GarbageCollectorMXBean;

import static spaceinv.model.SI.*;

public abstract class EnemyShip extends ShooterEntity implements StepAble, EventHandler {
    public enum Direction {
        Left,Right;
    }

    public EnemyShip(int x, int y, int width, int height, int projectileSpeed) {
        super(x, y, width, height, projectileSpeed);
    }
    public EnemyShip(int x,int y){
        this(x,y, SI.SHIP_WIDTH,SI.SHIP_HEIGHT, (int)ENEMY_BULLET_SPEED);
    }

    private boolean goLeft = false;
    private boolean moveDown = false;

    private double desiredMove = 0;
    private double nSkippedMove = 0;
    private int JAGGED = 10;

    public void step() {
        setX(getNextX());
    }

    public void moveDown(){
        setY(getY()+20);
    }

    public int getNextX(){
        int pixelSpeed = Math.max(1,JAGGED*(int)(ENEMY_SHIP_SPEED));
        return (int)getX()+pixelSpeed*(goLeft?-1:1);
    }

    @Override
    public void onModelEvent(ModelEvent evt) {
        super.onModelEvent(evt);

        if(evt.type == ModelEvent.Type.SHIP_HIT_LIMIT){
            moveDown();
            switch ((Direction)evt.data){
                case Left: goLeft = false; break;
                case Right: goLeft = true; break;
            }
        }
    }

    public abstract int getPoints();
}
