package builder.entities.npc.spawners;

import builder.GameState;
import engine.EngineState;
import engine.game.HasPosition;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

public class HelperSpawner implements Spawner{
    private int x;
    private int y;
    protected final TickTimer timer;

    protected HelperSpawner(int x, int y, RepeatingTimer timer) {
        this.x = x;
        this.y = y;
        this.timer = timer;
    }

    protected HelperSpawner(int x, int y, int defaultDuration) {
        this.x = x;
        this.y = y;
        this.timer = new RepeatingTimer(defaultDuration);
    }

    @Override
    public TickTimer getTimer() {
        return timer;
    }

    @Override
    public void tick(EngineState state, GameState game) {
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int i) {
        this.x = i;
    }

    @Override
    public void setY(int i) {
        this.y = i;
    }

    /**
     * Return how far away this npc is from the given position
     *
     * @param position the position we are measuring to from this npcs position!
     * @return integer representation for how far apart they are
     */
    protected final int distanceFrom(HasPosition position) {
        int deltaX = position.getX() - this.getX();
        int deltaY = position.getY() - this.getY();
        return (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
}
}
