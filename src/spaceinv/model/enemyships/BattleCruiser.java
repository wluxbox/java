package spaceinv.model.enemyships;

/*
 *   Type of space ship
 */
public class BattleCruiser extends EnemyShip {

    // Default value
    public static final int BATTLE_CRUISER_POINTS = 100;


    public BattleCruiser(int x, int y, int width, int height, int projectileSpeed) {
        super(x, y, width, height, projectileSpeed);
    }

    public BattleCruiser(int x, int y) {
        super(x, y);
    }

    @Override
    public int getPoints() {
        return BATTLE_CRUISER_POINTS;
    }
}
