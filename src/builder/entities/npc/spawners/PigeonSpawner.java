package builder.entities.npc.spawners;

import builder.GameState;
import builder.entities.resources.Cabbage;
import builder.entities.tiles.Tile;

import builder.helpers.HelperSpawner;
import engine.EngineState;
import engine.game.Entity;
import engine.timing.RepeatingTimer;

import java.util.List;

public class PigeonSpawner extends HelperSpawner implements Spawner {
    private static final int DEFAULT_SPAWN_INTERVAL = SpawnerBaseValues.PIGEON_SPAWN_INTERVAL;
    public PigeonSpawner(int x, int y) {
        super(x, y, DEFAULT_SPAWN_INTERVAL);
    }

    public PigeonSpawner(int x, int y, int duration) {
        super(x, y, new RepeatingTimer(duration));
    }



    @Override
    public void tick(EngineState state, GameState game) {
        timer.tick();
        List<Tile> tiles =
                game.getWorld()
                        .tileSelector(
                                tile -> {
                                    for (Entity entity : tile.getStackedEntities()) {
                                        if (entity instanceof Cabbage) {
                                            return true;
                                        }
                                    }
                                    return false;
                                });

        if (tiles.size() > 0) {
            int distance = this.distanceFrom(tiles.getFirst());
            Tile closest = tiles.getFirst();
            for (Tile tile : tiles) {
                if (this.distanceFrom(tile) < distance) {
                    closest = tile;
                }
            }

            if (this.getTimer().isFinished()) {
                game.getEnemies().spawnX = this.getX();
                game.getEnemies().spawnY = this.getY();
                game.getEnemies().Birds.add(game.getEnemies().makePigeon(closest));
            }
        }
    }


    }



