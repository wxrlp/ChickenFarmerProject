package builder.helpers;

import builder.GameState;
import builder.entities.npc.spawners.Spawner;
import engine.EngineState;
import engine.game.HasPosition;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

/**
 * A helper spawner is a basic implementation of the spawner interface
 * that can be extended to create specific types of spawners.
 */
public class HelperSpawner implements Spawner {
    private int x;
    private int y;
    protected final TickTimer timer;

    /**
     * Creates a new HelperSpawner at the given coordinates
     *
     * @param x     The x coordinate of the spawner
     * @param y     The y coordinate of the spawner
     * @param timer The timer for the spawner
     */
    protected HelperSpawner(int x, int y, RepeatingTimer timer) {
        this.x = x;
        this.y = y;
        this.timer = timer;
    }

    /**
     * Creates a new HelperSpawner at the given coordinates
     * with a default timer duration
     *
     * @param x               The x coordinate of the spawner
     * @param y               The y coordinate of the spawner
     * @param defaultDuration The default duration for the timer
     */
    protected HelperSpawner(int x, int y, int defaultDuration) {
        this.x = x;
        this.y = y;
        this.timer = new RepeatingTimer(defaultDuration);
    }

    /**
     * Returns the timer for this spawner
     */
    @Override
    public TickTimer getTimer() {
        return timer;
    }

    /**
     * Tick method for the spawner
     *
     * @param state The current engine state
     * @param game  The current game state
     */
    @Override
    public void tick(EngineState state, GameState game) {
    }

    /**
     * Returns the x coordinate of the spawner
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the spawner
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Sets the x coordinate of the spawner
     */
    @Override
    public void setX(int i) {
        this.x = i;
    }

    /**
     * Sets the y coordinate of the spawner
     */
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
