package builder.entities.npc.spawners;

import builder.GameState;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.resources.Cabbage;
import builder.entities.tiles.Tile;

import builder.helpers.HelperSpawner;
import engine.EngineState;
import engine.game.Entity;
import engine.timing.RepeatingTimer;

import java.util.List;

/**
 * Spawner that spawns pigeons when there are cabbages on the map.
 */
public class PigeonSpawner extends HelperSpawner implements Spawner {
    private static final int DEFAULT_SPAWN_INTERVAL = SpawnerBaseValues.PIGEON_SPAWN_INTERVAL;

    /** Creates a pigeon spawner at the given coordinates with the default spawn interval. */
    public PigeonSpawner(int x, int y) {
        super(x, y, DEFAULT_SPAWN_INTERVAL);
    }

    /** Creates a pigeon spawner at the given coordinates with a custom spawn interval. */
    public PigeonSpawner(int x, int y, int duration) {
        super(x, y, new RepeatingTimer(duration));
    }


    /**  * Spawns a pigeon targeting the closest cabbage if any cabbages exist on the map. */
    @Override
    public void tick(EngineState state, GameState game) {
        EnemyManager enemies = game.getEnemies();
        timer.tick();
        // Select all tiles that have cabbages on them
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

        // Find the closest cabbage tile
        if (!tiles.isEmpty()) {
            int distance = this.distanceFrom(tiles.getFirst());
            Tile closest = tiles.getFirst();
            for (Tile tile : tiles) {
                if (this.distanceFrom(tile) < distance) {
                    closest = tile;
                }
            }

            // Spawn a pigeon targeting the closest cabbage if the timer is finished
            if (this.getTimer().isFinished()) {
                enemies.spawnX = this.getX();
                enemies.spawnY = this.getY();
                enemies.Birds.add(game.getEnemies().makePigeon(closest));
            }
        }
    }


    }



