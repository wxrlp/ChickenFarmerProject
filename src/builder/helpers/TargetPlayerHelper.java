package builder.helpers;

import builder.entities.npc.enemies.Enemy;
import engine.art.sprites.SpriteGroup;

/** Helper class for targeting the player or other entities.
 */
public class TargetPlayerHelper {
    /**
     * Sets the direction and sprite of the eagle towards a target position.
     * @param targetX The x-coordinate of the target position.
     * @param targetY The y-coordinate of the target position.
     * @param isAttacking Boolean indicating if the eagle is attacking (true) or returning (false).
     */
    public static void setDirectionAndSpriteTowards(int targetX, int targetY, boolean isAttacking,
                                              Enemy enemy,
                                                    SpriteGroup art) {
        setNewDirection(targetX, targetY, enemy);
        if (isAttacking) {
            if (targetY > enemy.getY()) {
                enemy.setSprite(art.getSprite("down"));
            } else {
                enemy.setSprite(art.getSprite("up"));
            }
        } else {
            if (targetY < enemy.getY()) {
                enemy.setSprite(art.getSprite("up"));
            } else {
                enemy.setSprite(art.getSprite("down"));
            }
        }

    }

    /**
     * Sets a new direction for the Eagle to face towards the specified target coordinates.
     * @param trackedTarget The x-coordinate of the target position.
     * @param trackedTarget1 The y-coordinate of the target position.
     */
    public static void setNewDirection(int trackedTarget, int trackedTarget1, Enemy enemy) {
        double deltaX = trackedTarget - enemy.getX();
        double deltaY = trackedTarget1 - enemy.getY();
        enemy.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));
    }
}
