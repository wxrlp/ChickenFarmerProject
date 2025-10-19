package builder.entities.npc.enemies;

import builder.GameState;
import builder.entities.npc.Npc;

import engine.EngineState;

/**
 * An enemy is a type of NPC that is hostile to the player
 */
public class Enemy extends Npc {
    /**
     * Creates a new enemy at the given coordinates
     * @param x The x coordinate of the enemy
     * @param y The y coordinate of the enemy
     */
    public Enemy(int x, int y) {
        super(x, y);
    }

    /**
     * Tick method for the enemy
     * @param state The current engine state
     * @param game The current game state
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state, game);
    }

    /**
     * Interaction method for the enemy
     * @param state The current engine state
     * @param game The current game state
     */
    @Override
    public void interact(EngineState state, GameState game) {}
}
