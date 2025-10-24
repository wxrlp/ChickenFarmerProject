package builder.entities.npc.spawners;

import builder.GameState;

import builder.helpers.HelperSpawner;
import engine.EngineState;
import engine.timing.RepeatingTimer;

/**
 * Spawner for Eagle enemies
 */
public class EagleSpawner extends HelperSpawner implements Spawner {
    private static final int DEFAULT_SPAWN_INTERVAL =
            SpawnerBaseValues.EAGLE_SPAWN_INTERVAL;

    /**
     * Creates an eagle spawner at the given coordinates with the
     * default spawn interval.
     */
    public EagleSpawner(int x, int y) {
        super(x, y, DEFAULT_SPAWN_INTERVAL);
    }

    /**
     * Creates an eagle spawner at the given coordinates with a
     * custom spawn interval.
     */
    public EagleSpawner(int x, int y, int duration) {
        super(x, y, new RepeatingTimer(duration));

    }


    /**
     * Returns the timer for this spawner
     */
    @Override
    public void tick(EngineState state, GameState game) {
        getTimer().tick();
        if (this.getTimer().isFinished()) {
            if (game.getEnemies() == null) {
                return; // Check if enemies manager exists
            }
            game.getEnemies().spawnEagle(
                    this.getX(), this.getY(), game.getPlayer());
        }
    }


}
