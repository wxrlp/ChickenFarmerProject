package builder.entities.npc;

/**
 * An entity that can be directed to move in a specific direction.
 */
public interface Directable {

    /**
     * Gets the current direction of the entity.
     *
     * @return The direction as an integer (e.g., 0-3 for cardinal directions).
     */
    int getDirection();

    /**
     * Sets the direction of the entity.
     *
     * @param direction The new direction as an integer (e.g., 0-3 for cardinal directions).
     */
    void setDirection(int direction);

    /**
     * Moves the entity in the current direction based on its speed.
     */
    void move();
}
