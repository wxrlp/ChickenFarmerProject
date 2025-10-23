package builder.world;

import engine.game.HasPosition;


/**
 * SpawnerDetails interface representing the position and duration
 * of a spawner in the game world.
 */
public interface SpawnerDetails extends HasPosition {
    /**
     * Get the duration for the spawner
     */
    int getDuration();
}
