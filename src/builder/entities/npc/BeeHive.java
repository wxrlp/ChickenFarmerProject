package builder.entities.npc;

import builder.GameState;
import builder.entities.npc.enemies.Enemy;
import builder.ui.SpriteGallery;

import engine.EngineState;
import engine.art.sprites.SpriteGroup;
import engine.timing.RepeatingTimer;

import java.util.ArrayList;

/** Spawns bees it fires at enemy's within a set range
 * A bee hive should reload every 240 ticks but if the player is standing on it then it should
 * increase the rate by 3 (so if the player stands on it for the whole time, it will
 * reload in 80 ticks).
 * If a bird comes within 350 pixels of the beehive, a single
 * guard bee will be spawned if it is loaded.
 * */
public class BeeHive extends Npc {

    public static final int DETECTION_DISTANCE = 350;
    public static final int RELOAD_COOLDOWN_TICKS = 240;
    public static final int FOOD_COST = 2;
    public static final int COIN_COST = 2;
    private static final SpriteGroup art = SpriteGallery.hive;
    private boolean loaded = true;

    private final RepeatingTimer timer = new RepeatingTimer(RELOAD_COOLDOWN_TICKS);

    /** Create a new beehive at the given coordinates
     *
     * @param x horizontal position
     * @param y vertical position
     */
    public BeeHive(int x, int y) {
        super(x, y);
        this.setSprite(art.getSprite("default"));
        this.setSpeed(0);
    }

    /** Progress the state of the beehive, updating how it is rendered as required. */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state);
        this.timer.tick();
    }

    /** Interact with the beehive to increase spawn rate of bees at enemies within range */
    @Override
    public void interact(EngineState state, GameState game) {
        super.interact(state, game);

        timer.tick();
        Npc npc = this.checkAndSpawnBee(game.getEnemies().Birds);
        if (npc != null) {
            game.getNpcs().addNpc(npc);
        }
        if (timer.isFinished()) {
            this.loaded = true;
        }
    }
/** Check for enemies within range and spawn a bee if loaded
     *
     * @param targets The list of enemies to check distance from
     * @return A guard bee if an enemy is within range and the beehive is loaded, null otherwise
     */
    public Npc checkAndSpawnBee(ArrayList<Enemy> targets) {
        for (Enemy enemy : targets) {
            if (this.distanceFrom(enemy) < DETECTION_DISTANCE && this.loaded) {
                this.loaded = false;
                return new GuardBee(
                        this.getX(), this.getY(), enemy); // can only spawn one bee in a frame
            }
        }
        return null;
    }
}
