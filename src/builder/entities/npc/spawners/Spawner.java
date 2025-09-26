package builder.entities.npc.spawners;

import builder.GameState;
import builder.Tickable;

import engine.EngineState;
import engine.game.HasPosition;
import engine.timing.TickTimer;

/**
 * A spawner is responsible for spawning specific types of {@link builder.entities.npc.Npc}s or
 * {@link builder.entities.npc.enemies.Enemy}s
 */
public interface Spawner extends HasPosition, Tickable {

    public TickTimer getTimer();

    @Override
    public void tick(EngineState state, GameState game);

    @Override
    public int getX();

    @Override
    public void setX(int x);

    @Override
    public int getY();

    @Override
    public void setY(int y);
}
