package spaceinv.model.enemyships;

/*
 *   Type of space ship
 */
public class Frigate extends EnemyShip {

    // Default value
    public static final int FRIGATE_POINTS = 300;


    public Frigate(int x, int y, int width, int height, int projectileSpeed) {
        super(x, y, width, height, projectileSpeed);
    }

    public Frigate(int x, int y) {
        super(x, y);
    }

    @Override
    public int getPoints() {
        return FRIGATE_POINTS;
    }
}
