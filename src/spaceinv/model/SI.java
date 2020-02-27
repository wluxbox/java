package spaceinv.model;


import javafx.event.Event;
import spaceinv.event.EventBus;
import spaceinv.event.EventHandler;
import spaceinv.event.ModelEvent;
import spaceinv.model.enemyships.BattleCruiser;
import spaceinv.model.enemyships.Bomber;
import spaceinv.model.enemyships.EnemyShip;
import spaceinv.model.enemyships.Frigate;

import java.util.*;

/*
 *  SI (Space Invader) class representing the overall
 *  data and logic of the game
 *  (nothing about the look/rendering here)
 */
public class SI {

    // Default values (not to use directly). Make program adaptable
    // by letting other programmers set values if they wish.
    // If not, set default values (as a service)
    public static final int GAME_WIDTH = 500;
    public static final int GAME_HEIGHT = 500;
    public static final int LEFT_LIMIT = 50;
    public static final int RIGHT_LIMIT = 450;
    public static final int SHIP_WIDTH = 20;
    public static final int SHIP_HEIGHT = 20;
    public static final int SHIP_MAX_DX = 3;
    public static final int SHIP_MAX_DY = 0;
    public static final int GUN_WIDTH = 20;
    public static final int GUN_HEIGHT = 20;
    public static final double GUN_MAX_DX = 2;
    public static final double PROJECTILE_WIDTH = 5;
    public static final double PROJECTILE_HEIGHT = 5;
    public static final int GROUND_HEIGHT = 20;
    public static final int OUTER_SPACE_HEIGHT = 10;
    public static final double ENEMY_SHIP_SPEED = 1;
    public static final double PLAYER_BULLET_SPEED = 10;
    public static final double ENEMY_BULLET_SPEED = -2;
    public static final double ENEMY_FIRING_CHANCE = 0.025;
    public static final double PROJECTILE_ACCELERATION = 1.02;

    public static final long ONE_SEC = 1_000_000_000;
    public static final long HALF_SEC = 500_000_000;
    public static final long TENTH_SEC = 100_000_000;

    private static final Random rand = new Random();

    // TODO More references here

    private final List<Projectile> enemyProjectiles = new ArrayList<>(); //TODO delete
    private int points;
    private Ground ground;

    // TODO Constructor here


    // Timing. All timing handled here!
    private long lastTimeForMove;
    private long lastTimeForFire;
    //

    private List<EnemyShip> enemyShips;
    private List<EnemyShip> dyingShips;
    private int shipToMoveIndex = 0;

    //
    private List<Positionable> entities;
    private Gun player;
    private EventBus eventBus = EventBus.INSTANCE;

    // ------ Game loop (called by timer) -----------------

    public void update(long now) {

        //Player Movement
        if(player.getXspeed()!=0){
            player.setX(
                    Math.min(GAME_WIDTH-player.getWidth(),
                            Math.max(0,
                                    player.getX()+player.getXspeed())));
        }

        EnemyShip.Direction wallHit = null;

        //Entity Handler
        List<Positionable> entitiesToBeDeleted = new ArrayList<>();

        for (Positionable p:entities) {
            //Handle Collision with enemy ships
            if(p instanceof EnemyShip){

                //player shoots enemy
                if(collision(p,player.getProjectile())){
                    points += ((EnemyShip) p).getPoints();
                    entitiesToBeDeleted.add(p);
                    entitiesToBeDeleted.add(player.getProjectile());
                    eventBus.publish(new ModelEvent(ModelEvent.Type.GUN_HIT_SHIP,p)); //sends in ship to be destoryed
                }

                //Ship Collides with edges
                if(wallHit == null && shipToMoveIndex == 0){
                    double nextX = ((EnemyShip)p).getNextX();

                    if(nextX < LEFT_LIMIT) {
                        wallHit = EnemyShip.Direction.Left;
                    }
                    else if(nextX+p.getWidth() > RIGHT_LIMIT) {
                        wallHit = EnemyShip.Direction.Right;
                    }
                }
            }

            //Handle OOB and Collisions between player and enemy Bombs
            else if(p instanceof Projectile) {
                ((Projectile) p).step();

                //Does Enemy Bomb it player
                if(p != player.getProjectile()) {
                    if(collision(player,p)){
                        //entitiesToBeDeleted.add(player);
                        eventBus.publish(new ModelEvent(ModelEvent.Type.BOMB_HIT_GUN,player)); //Send Player to event
                    }
                }

                //OOB
                if(collision(ground,p)){
                    entitiesToBeDeleted.add(p);
                    eventBus.publish(new ModelEvent(ModelEvent.Type.BOMB_HIT_GROUND,p));
                } else if(p.getY()+p.getHeight()<0){
                    entitiesToBeDeleted.add(p);
                    eventBus.publish(new ModelEvent(ModelEvent.Type.OUT_OF_BOUNDS,p));
                }
            }
        }

        //If any hit wall turn around
        if(wallHit != null){
            eventBus.publish(new ModelEvent(ModelEvent.Type.SHIP_HIT_LIMIT, wallHit));
        }


        for(Positionable p : entitiesToBeDeleted){
            entities.remove(p);
            if(p instanceof EventHandler){
                eventBus.unRegister((EventHandler)p);
            }
            if(p instanceof EnemyShip){
                dyingShips.add((EnemyShip)p);
            }
        }

        //ONLY UPDATE ENEMYSHIPS AT FIRST CYCLE R EVERYTHING WILL DESYNC.
        if(enemyShips.size()>0){

            EnemyShip currentShip = enemyShips.get(shipToMoveIndex);
            currentShip.step();
            if(rand.nextInt((int)(1/ENEMY_FIRING_CHANCE))==0){
                addPositionable(currentShip.fire());
            }
            if(shipToMoveIndex == 0){
                for(EnemyShip dyingShip:dyingShips){
                    enemyShips.remove(dyingShip);
                }
                dyingShips.clear();
            }
            if(enemyShips.size()<=0){
                //WIN
                eventBus.publish(new ModelEvent(ModelEvent.Type.HAS_WON));
            }
            else {
                shipToMoveIndex = (shipToMoveIndex+1)%enemyShips.size();
            }
        }

    }

    private boolean collision(Positionable ship, Positionable projectile){
        if(ship == null || projectile == null) return false;
        //TODO make better, with vectors and stuff
        double axMin = ship.getX();
        double axMax = axMin + ship.getWidth();
        double bxMin = projectile.getX();
        double bxMax = bxMin + projectile.getWidth();

        double ayMin = ship.getY();
        double ayMax = ayMin + ship.getHeight();
        double byMin = projectile.getY();
        double byMax = byMin + projectile.getHeight();

        if((axMin <= bxMax && bxMax <= axMax) || (axMin <= bxMin && bxMin <= axMax)) {
            if((ayMin <= byMax && byMax <= ayMax) || (ayMin <= byMin && byMin <= ayMax)) {
                return true;
            }
        }
        return false;
    }

    private boolean shipHitRightLimit() {
        // TODO
        return false;
    }

    private boolean shipHitLeftLimit() {
        // TODO
        return false;
    }


    // ---------- Interaction with GUI  -------------------------

    public void fireGun() {
        addPositionable(player.fire());
    }

    // TODO More methods called by GUI
    public void addPositionable(Positionable positionable){
        if(positionable instanceof EventHandler){
            eventBus.register((EventHandler)positionable);
        }
        if(positionable instanceof EnemyShip) {
            enemyShips.add((EnemyShip)positionable);
        }
        entities.add(positionable);
    }

    public Gun getPlayer() { return player; }

    public List<Positionable> getPositionables() {
        return entities;
    }

    public int getPoints() {
        return points;
    }

    public SI(){
        entities = new ArrayList<Positionable>();
        enemyShips = new ArrayList<EnemyShip>();
        dyingShips = new ArrayList<EnemyShip>();
        addPositionable(ground = new Ground());

        player = new Gun((GAME_WIDTH-GUN_WIDTH)/2, GAME_HEIGHT-GUN_HEIGHT-GROUND_HEIGHT, GUN_WIDTH,GUN_HEIGHT, PLAYER_BULLET_SPEED);
        addPositionable(player);

        int nShips = 12 * (SHIP_WIDTH + 10);
        for (int i = LEFT_LIMIT + 1 ; i < nShips; i += 30) {
            addPositionable(new Frigate(i, 50));
        }
        for (int i = LEFT_LIMIT + 1 ; i < nShips; i += 30) {
            addPositionable(new BattleCruiser(i, 80));
        }
        for (int i = LEFT_LIMIT + 1 ; i < nShips; i += 30) {
            addPositionable(new Bomber(i, 110));
        }
    }
}
