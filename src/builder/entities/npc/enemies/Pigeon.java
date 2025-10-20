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

public class Pigeon extends Enemy implements Expirable {

    private static final SpriteGroup art = SpriteGallery.pigeon;
    private FixedTimer lifespan = new FixedTimer(3000);
    private HasPosition trackedTarget;
    public boolean attacking = true;
    private int spawnX = 0;
    private int spawnY = 0;

    public Pigeon(int x, int y) {
        super(x, y);
        this.spawnX = x;
        this.spawnY = y;
        this.setSprite(art.getSprite("down"));
    }

    public Pigeon(int x, int y, HasPosition trackedTarget) {
        super(x, y);
        this.spawnX = x;
        this.spawnY = y;
        this.trackedTarget = trackedTarget;
        this.setSpeed(1);
        this.setSprite(art.getSprite("down"));
    }

    @Override
    public FixedTimer getLifespan() {
        return lifespan;
    }

    @Override
    public void setLifespan(FixedTimer timer) {
        this.lifespan = timer;
    }


    private void updateReturnSprite() {
        if (this.spawnY < this.getY()) {
            this.setSprite(art.getSprite("up"));
        } else {
            this.setSprite(art.getSprite("down"));
        }
    }

    private void returnToSpawn(EngineState engine) {
        double deltaX = (this.spawnX - this.getX());
        double deltaY = (this.spawnY - this.getY());
        this.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));

        if (this.distanceFrom(this.spawnX, this.spawnY) < engine.getDimensions().tileSize()) {
            this.markForRemoval();
        }

        updateReturnSprite();
    }


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

