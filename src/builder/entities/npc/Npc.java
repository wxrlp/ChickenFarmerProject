package builder.entities.npc;

import builder.GameState;
import builder.Tickable;
import builder.entities.Interactable;

import engine.EngineState;
import engine.game.Entity;
import engine.game.HasPosition;

/**
 * An Npc is a non-player character that can interact with the
 * player and the game world.
 */
public class Npc extends Entity implements Interactable, Tickable,
        Directable {

    private int direction = 0;
    private double speed = 1;

    /**
     * Creates a new Npc at the given coordinates
     */
    public Npc(int x, int y) {
        super(x, y);
    }

    /**
     * Get the speed of {@link Npc}
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Set the speed of {@link Npc}
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Get the direction of {@link Npc}
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * Set the direction of {@link Npc}
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Adjust the X and Y of {@link Npc}
     */
    public void move() {
        final int deltaX = (int) Math.round(
                Math.cos(Math.toRadians(this.direction))
                        * this.speed);
        final int deltaY = (int) Math.round(
                Math.sin(Math.toRadians(this.direction))
                        * this.speed);
        this.setX(this.getX() + deltaX);
        this.setY(this.getY() + deltaY);
    }

    /**
     * Tick method for the npc
     */
    @Override
    public void tick(EngineState state) {
        this.move();
    }

    /**
     * Tick method for the npc
     */
    @Override
    public void tick(EngineState state, GameState game) {
        tick(state);
    }

    /**
     * Interaction method for the npc
     */
    @Override
    public void interact(EngineState state, GameState game) {
    }


    /**
     * Return how far away this npc is from the given position
     *
     * @param position the position we are measuring to from this
     *                 npc's position!
     * @return integer representation for how far apart they are
     */
    public int distanceFrom(HasPosition position) {
        return distanceFrom(position.getX(), position.getY());
    }

    /**
     * Return how far away this npc is from the given position
     *
     * @param xcoordinate - x coordinate
     * @param ycoordinate - y coordinate
     * @return integer representation for how far apart they are
     */
    public int distanceFrom(int xcoordinate, int ycoordinate) {
        int deltaX = xcoordinate - this.getX();
        int deltaY = ycoordinate - this.getY();
        return (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
