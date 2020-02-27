package spaceinv.model.enemyships;

/*
 *   Type of space ship
 */
public class Bomber extends EnemyShip{

    // Default value
    public static final int BOMBER_POINTS = 200;


    public Bomber(int x, int y, int width, int height, int projectileSpeed) {
        super(x, y, width, height, projectileSpeed);
    }

    public Bomber(int x, int y) {
        super(x, y);
    }

    @Override
    public int getPoints() {
        return BOMBER_POINTS;
    }
}
