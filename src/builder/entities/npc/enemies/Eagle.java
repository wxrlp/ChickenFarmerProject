package builder.entities.npc.enemies;

import builder.GameState;
import builder.entities.npc.Expirable;
import builder.ui.SpriteGallery;

import engine.EngineState;
import engine.art.sprites.SpriteGroup;
import engine.game.HasPosition;
import engine.timing.FixedTimer;

/**
 * Represents an Eagle enemy that tracks a target, attacks to steal food, and returns to its spawn
 * point before expiring. When the eagle reaches the player, it will
 * steal 3 food from the player and return to spawn.
 */
public class Eagle extends Enemy implements Expirable {

    private static final SpriteGroup art = SpriteGallery.eagle;
    private FixedTimer lifespan = new FixedTimer(5000);
    public HasPosition trackedTarget;
    private boolean attacking = true;
    private final int spawnX;
    private final int spawnY;
    private int food = 0;

    /**
     * Constructs an Eagle enemy at the specified coordinates, tracking the given target.
     * @param x The x-coordinate of the eagle's spawn position.
     * @param y The y-coordinate of the eagle's spawn position.
     * @param trackedTarget The target that the eagle will track and attack.
     */
    public Eagle(int x, int y, HasPosition trackedTarget) {
        super(x, y);
        this.spawnX = x;
        this.spawnY = y;

        // derive direction based on where the eagle is and the initial target is
        int direction = 20;
        this.setDirection(direction);
        this.setSpeed(2);
        this.trackedTarget = trackedTarget;

        this.setSprite(art.getSprite("default"));

        setNewDirection(trackedTarget.getX(), trackedTarget.getY());

    }

    /**
     * Sets a new direction for the Eagle to face towards the specified target coordinates.
     * @param trackedTarget The x-coordinate of the target position.
     * @param trackedTarget1 The y-coordinate of the target position.
     */
    private void setNewDirection(int trackedTarget, int trackedTarget1) {
        double deltaX = trackedTarget - this.getX();
        double deltaY = trackedTarget1 - this.getY();
        this.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));
    }


    /**
     * Gets the lifespan timer of the Eagle.
     * @return The FixedTimer representing the lifespan of the Eagle.
     */
    @Override
    public FixedTimer getLifespan() {
        return lifespan;
    }

    /**
     * Sets the lifespan timer of the Eagle.
     * @param timer The FixedTimer to set as the lifespan of the Eagle.
     */
    @Override
    public void setLifespan(FixedTimer timer) {
        this.lifespan = timer;
    }

    /**
     * Processes a single tick for the Eagle, updating its position, lifespan, and interaction with
     * the player.
     * @param engine The state of the engine, including the mouse, keyboard information and
     *     dimension. Useful for processing keyboard presses or mouse movement.
     * @param game The state of the game, including the player and world. Can be used to query or
     *     update the game state.
     */
    @Override
    public void tick(EngineState engine, GameState game) {
        super.tick(engine);
        this.lifespan.tick();
        int playerX = game.getPlayer().getX();
        int playerY = game.getPlayer().getY();
        int tileSize = engine.getDimensions().tileSize();

        if (this.lifespan.isFinished()) {
            this.markForRemoval();
        }
        if ((this.distanceFrom(playerX, playerY)
                        < tileSize)
                && this.attacking) {
            this.attacking = false;

            if (this.food == 0) {
                game.getInventory().addFood(-3);
                this.food = 3;
            }
            this.setSpeed(4); // the eagle BOOKS it once it has the food
            //      }
        }
        if ((this.distanceFrom(this.spawnX, this.spawnY) < tileSize)
                && !this.attacking) {
            this.markForRemoval();
        }
        this.move();

        // Update sprite based on whether eagle is attacking or returning to spawn
        if (attacking) {
            setDirectionAndSpriteTowards(trackedTarget.getX(), trackedTarget.getY(), attacking);
        } else {
            setDirectionAndSpriteTowards(this.spawnX, this.spawnY, attacking);
        }

        // If the eagle is removed from the world before it
        //  reaches its spawn (e.g. is attacked by a bee) then it returns the stolen food to
        //  the player.
        if (this.isMarkedForRemoval()
                && this.distanceFrom(this.spawnX, this.spawnY)
                        > tileSize) {
            game.getInventory().addFood(this.food);
        }
    }

    /**
     * Sets the direction and sprite of the eagle towards a target position.
     * @param targetX The x-coordinate of the target position.
     * @param targetY The y-coordinate of the target position.
     * @param isAttacking Boolean indicating if the eagle is attacking (true) or returning (false).
     */
    private void setDirectionAndSpriteTowards(int targetX,int targetY, boolean isAttacking){
        setNewDirection(targetX, targetY);
        if (isAttacking){
            if (targetY > this.getY()) {
                this.setSprite(art.getSprite("down"));
            } else {
                this.setSprite(art.getSprite("up"));
            }
        }else{
            if (targetY < this.getY()) {
                this.setSprite(art.getSprite("up"));
            } else {
                this.setSprite(art.getSprite("down"));
            }
        }

    }
}
