package builder.entities.npc.spawners;

import builder.GameState;

import builder.helpers.HelperSpawner;
import engine.EngineState;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

/**
 * Spawner for Magpie enemies
 */
public class MagpieSpawner extends HelperSpawner implements Spawner {
    private static final int DEFAULT_SPAWN_INTERVAL =
            SpawnerBaseValues.MAGPIE_SPAWN_INTERVAL;

    /**
     * Creates a magpie spawner at the given coordinates with the
     * default spawn interval.
     */
    public MagpieSpawner(int x, int y) {
        super(x, y, DEFAULT_SPAWN_INTERVAL);
    }

    /**
     * Creates a magpie spawner at the given coordinates with a
     * custom spawn interval.
     */
    public MagpieSpawner(int x, int y, int duration) {
        super(x, y, new RepeatingTimer(duration));
    }

    /**
     * Returns the timer for this spawner
     */
    @Override
    public TickTimer getTimer() {
        return timer;
    }

    /**
     * Spawns a magpie at the spawner's location when the timer is
     * finished.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        this.timer.tick();
        if (this.getTimer().isFinished()) {
            if (game.getEnemies() == null) {
                return;
            }
            game.getEnemies().setSpawnX(this.getX());
            game.getEnemies().setSpawnY(this.getY());
            game.getEnemies().getBirds().add(
                    game.getEnemies().makeMagpie(game.getPlayer()));

        }
    }


}
