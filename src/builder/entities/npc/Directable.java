package builder.entities.npc;

/**
 * An entity that can be directed to move in a specific direction.
 */
public interface Directable {

    int getDirection();

    void setDirection(int direction);

    void move();
}
