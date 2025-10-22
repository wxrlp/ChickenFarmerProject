package builder.world;

import engine.game.HasPosition;
import engine.game.ImmutablePosition;

public interface SpawnerDetails extends HasPosition {
    int getDuration();
}
