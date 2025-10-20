package builder.entities.npc.spawners;

import builder.GameState;

import engine.EngineState;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

public class EagleSpawner extends HelperSpawner implements Spawner {
    private static final int DEFAULT_SPAWN_INTERVAL = 1000;

    public EagleSpawner(int x, int y) {
        super(x, y, DEFAULT_SPAWN_INTERVAL);
    }

    public EagleSpawner(int x, int y, int duration) {
        super(x, y, new RepeatingTimer(duration));

    }


    @Override
    public void tick(EngineState state, GameState game) {
        timer.tick();
        if (this.getTimer().isFinished()) {
            game.getEnemies().spawnEagle(this.getX(), this.getY(), game.getPlayer());
        }
    }


}
