package builder.world;

import engine.game.ImmutablePosition;

/**
 * PlayerDetails interface representing the immutable position and
 * starting resources of a player in the game world.
 */
public interface PlayerDetails extends ImmutablePosition {
    int getStartingFood();

    int getStartingCoins();
}
