package builder.world;

import engine.game.ImmutablePosition;

/**
 * PlayerDetails interface representing the immutable position and
 * starting resources of a player in the game world.
 */
public interface PlayerDetails extends ImmutablePosition {
    /**
     * Get the starting food amount for the player
     */
    int getStartingFood();

    /**
     * Get the starting coin amount for the player
     */
    int getStartingCoins();
}
