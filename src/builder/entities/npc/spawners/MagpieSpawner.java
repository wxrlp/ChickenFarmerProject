package builder.entities.npc.spawners;

import builder.GameState;

import engine.EngineState;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

public class MagpieSpawner extends HelperSpawner implements Spawner {
    private static final int DEFAULT_SPAWN_INTERVAL = 1000;

    public MagpieSpawner(int x, int y) {
        super(x, y, DEFAULT_SPAWN_INTERVAL);
    }

    public MagpieSpawner(int x, int y, int duration) {
        super(x, y, new RepeatingTimer(duration));
    }

    @Override
    public TickTimer getTimer() {
        return timer;
    }

    @Override
    public void tick(EngineState state, GameState game) {
        this.timer.tick();
        if (this.getTimer().isFinished()) {
            game.getEnemies().spawnX = this.getX();
            game.getEnemies().spawnY = this.getY();
            game.getEnemies().Birds.add(game.getEnemies().makeMagpie(game.getPlayer()));

        }
    }


}
