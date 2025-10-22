package builder.entities.npc.enemies;

import builder.GameState;
import builder.entities.npc.Expirable;
import builder.entities.resources.Cabbage;
import builder.entities.tiles.Tile;
import builder.ui.SpriteGallery;

import engine.EngineState;
import engine.art.sprites.SpriteGroup;
import engine.game.Entity;
import engine.game.HasPosition;
import engine.timing.FixedTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pigeon enemy that tracks cabbages, attacks to destroy them, and returns to its spawn
 * point before expiring.
 */
public class Pigeon extends Enemy implements Expirable {

    private static final SpriteGroup art = SpriteGallery.pigeon;
    private FixedTimer lifespan = new FixedTimer(3000);
    private HasPosition trackedTarget;
    public boolean attacking = true;
    private int spawnX = 0;
    private int spawnY = 0;

    /**
     * Constructs a Pigeon enemy at the specified coordinates.
     * @param x The x-coordinate of the pigeon's spawn position.
     * @param y The y-coordinate of the pigeon's spawn position.
     */
    public Pigeon(int x, int y) {
        super(x, y);
        this.spawnX = x;
        this.spawnY = y;
        this.setSprite(art.getSprite("down"));
    }

    /**
     * Constructs a Pigeon enemy at the specified coordinates, tracking the given target.
     * @param x The x-coordinate of the pigeon's spawn position.
     * @param y The y-coordinate of the pigeon's spawn position.
     * @param trackedTarget The target that the pigeon will track and attack.
     */
    public Pigeon(int x, int y, HasPosition trackedTarget) {
        super(x, y);
        this.spawnX = x;
        this.spawnY = y;
        this.trackedTarget = trackedTarget;
        this.setSpeed(1);
        this.setSprite(art.getSprite("down"));
    }

    /** Gets the lifespan timer of the Pigeon. */
    @Override
    public FixedTimer getLifespan() {
        return lifespan;
    }

    /** Sets the lifespan timer of the Pigeon. */
    @Override
    public void setLifespan(FixedTimer timer) {
        this.lifespan = timer;
    }

    /** Updates the sprite of the Pigeon based on its return direction. */
    private void updateReturnSprite() {
        if (this.spawnY < this.getY()) {
            this.setSprite(art.getSprite("up"));
        } else {
            this.setSprite(art.getSprite("down"));
        }
    }

    /** Moves the Pigeon back to its spawn point. */
    private void returnToSpawn(EngineState engine) {
        double deltaX = (this.spawnX - this.getX());
        double deltaY = (this.spawnY - this.getY());
        this.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));

        if (this.distanceFrom(this.spawnX, this.spawnY) < engine.getDimensions().tileSize()) {
            this.markForRemoval();
        }

        updateReturnSprite();
    }

    /** Finds all tiles in the game world that contain cabbages. */
    private List<Tile> findTilesWithCabbage(GameState game) {
        return game.getWorld()
                .tileSelector(
                        tile -> {
                            for (Entity entity : tile.getStackedEntities()) {
                                if (entity instanceof Cabbage) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        );
    }

    /** Updates the state of the Pigeon on each tick of the game engine. */
    @Override
    public void tick(EngineState engine, GameState game) {
        super.tick(engine, game);
        int tileSize = engine.getDimensions().tileSize();
        if (!this.attacking) {
            returnToSpawn(engine);
        }
        this.move();
        if (this.trackedTarget == null
                && this.attacking) { // if the pigeon has no target, it should go to the center of
                                      // the screen if its hunting
            returnToSpawn(engine);
        }
        if (this.trackedTarget != null && this.attacking) {
            double deltaX = (this.trackedTarget.getX() - this.getX());
            double deltaY = (this.trackedTarget.getY() - this.getY());
            this.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));
        }
        this.lifespan.tick();
        if (this.lifespan.isFinished()) {
            this.markForRemoval();
        }
        if (!attacking) {
            if (this.distanceFrom(spawnX, spawnY) < tileSize) {
                this.markForRemoval();
            }
            updateReturnSprite();
        }

        List<Tile> tiles = findTilesWithCabbage(game);
        attackCabbage(tileSize, tiles);

    }

    /** Sets the attacking status of the Pigeon.
     * @param attackStatus The new attacking status to set.
     * @return The updated attacking status.
     */
    public boolean setAttacking(boolean attackStatus){
        this.attacking = attackStatus;
        return this.attacking;
    }

    /** Attacks the closest cabbage tile if within range. */
    private void attackCabbage(int tileSize, List<Tile> tiles){
        if (!tiles.isEmpty()) {
            int distance = this.distanceFrom(tiles.getFirst());
            Tile closest = tiles.getFirst();
            for (Tile tile : tiles) {
                if (this.distanceFrom(tile) < distance) {
                    closest = tile;
                }
            }
            this.trackedTarget = closest;

            if (this.attacking
                    && this.distanceFrom(this.trackedTarget) < tileSize) {
                for (Entity entity : closest.getStackedEntities()) {
                    if (entity instanceof Cabbage cabbage) {
                        cabbage.markForRemoval();
                        this.attacking = false;
                    }
                }
            }
        } else { // no cabbages to get
            this.attacking = false;
        }

        }

    }

