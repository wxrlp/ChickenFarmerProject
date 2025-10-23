package builder.entities.npc.spawners;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
import builder.entities.npc.enemies.Enemy;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.npc.enemies.Pigeon;
import builder.entities.resources.Cabbage;
import builder.entities.tiles.Dirt;
import builder.inventory.Inventory;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import builder.player.Player;
import builder.world.BeanWorld;
import builder.world.WorldBuilder;
import engine.EngineState;
import engine.renderer.Dimensions;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;
import static org.junit.Assert.*;

public class PigeonSpawnerTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private static final int SPAWN_X = 100;
    private static final int SPAWN_Y = 100;

    private PigeonSpawner spawner;
    private GameState gameState;
    private EngineState engineState;
    private EnemyManager enemyManager;
    private BeanWorld world;

    @Before
    public void setUp() {
        spawner = new PigeonSpawner(SPAWN_X, SPAWN_Y);
        engineState = new MockEngineState(dimensions);

        world = WorldBuilder.empty();
        Player player = new ChickenFarmer(400, 400);
        Inventory inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();
        enemyManager = new EnemyManager(dimensions);

        gameState = new JavaBeanGameState(world,
                player, inventory, npcs, enemyManager);
    }

    @Test
    public void testPigeonSpawnerInitialPosition() {
        assertEquals(SPAWN_X, spawner.getX());
        assertEquals(SPAWN_Y, spawner.getY());
    }

    @Test
    public void testPigeonDoesntSpawnWithoutCabbages() {
        int initialPigeons = enemyManager.getAll().size();

        for (int i = 0;
             i < SpawnerBaseValues.PIGEON_SPAWN_INTERVAL; i++) {
            spawner.tick(engineState, gameState);
        }

        assertEquals(initialPigeons, enemyManager.getAll().size());
    }



    @Test
    public void testPigeonSpawnedAtSpawnerLocation() {
        // Place a cabbage in the world
        Dirt dirtTile = new Dirt(400, 400);
        Cabbage cabbage = new Cabbage(400, 400);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        for (int i = 0;
             i < SpawnerBaseValues.PIGEON_SPAWN_INTERVAL; i++) {
            spawner.tick(engineState, gameState);
        }

        Pigeon spawnedPigeon = null;
        for (Enemy enemy : enemyManager.getAll()) {
            if (enemy instanceof Pigeon) {
                spawnedPigeon = (Pigeon) enemy;
                break;
            }
        }

        assertNotNull(spawnedPigeon);
        assertEquals(SPAWN_X, spawnedPigeon.getX());
        assertEquals(SPAWN_Y, spawnedPigeon.getY());
    }



    @Test
    public void testPigeonSpawnerDoesNotSpawnBeforeInterval() {
        // Place a cabbage in the world
        Dirt dirtTile = new Dirt(400, 400);
        Cabbage cabbage = new Cabbage(400, 400);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        int initialPigeons = enemyManager.getAll().size();

        // Tick less than the spawn interval
        for (int i = 0;
             i < SpawnerBaseValues.PIGEON_SPAWN_INTERVAL - 10; i++) {
            spawner.tick(engineState, gameState);
        }

        // No pigeon should be spawned yet
        assertEquals(initialPigeons, enemyManager.getAll().size());
    }




}