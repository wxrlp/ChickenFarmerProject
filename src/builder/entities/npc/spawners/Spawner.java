package builder.entities.npc.spawners;

import builder.Tickable;

import engine.game.HasPosition;
import engine.timing.TickTimer;

/**
 * A spawner is responsible for spawning specific types of
 * {@link builder.entities.npc.Npc}s or
 * {@link builder.entities.npc.enemies.Enemy}s
 */
public interface Spawner extends HasPosition, Tickable {

    /** *
     * Returns the timer for this spawner
     */
    TickTimer getTimer();

}
