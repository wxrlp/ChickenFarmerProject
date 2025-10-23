package builder.entities.npc.spawners;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
import builder.entities.npc.enemies.Eagle;
import builder.entities.npc.enemies.EnemyManager;
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

public class EagleSpawnerTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private static final int SPAWN_X = 100;
    private static final int SPAWN_Y = 200;

    private EagleSpawner spawner;
    private GameState gameState;
    private EngineState engineState;
    private EnemyManager enemyManager;

    @Before
    public void setUp() {
        spawner = new EagleSpawner(SPAWN_X, SPAWN_Y);
        engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        Player player = new ChickenFarmer(400, 400);
        Inventory inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();
        enemyManager = new EnemyManager(dimensions);

        gameState = new JavaBeanGameState(world, player, inventory, npcs, enemyManager);
    }

    @Test
    public void testEagleSpawnerInitialPosition() {
        assertEquals(SPAWN_X, spawner.getX());
        assertEquals(SPAWN_Y, spawner.getY());
    }

    @Test
    public void testEagleSpawnerHasTimer() {
        assertNotNull(spawner.getTimer());
    }

    @Test
    public void testEagleSpawnerDoesNotSpawnBeforeTimerFinished() {
        int initialEagles = enemyManager.getAll().size();

        // Tick less than spawn interval
        for (int i = 0; i < 10; i++) {
            spawner.tick(engineState, gameState);
        }

        assertEquals(initialEagles,
                enemyManager.getAll().size());
    }

    @Test
    public void testEagleSpawnerSetAndGetCoordinates() {
        spawner.setX(300);
        spawner.setY(400);

        assertEquals(300, spawner.getX());
        assertEquals(400, spawner.getY());
    }

    @Test
    public void testEagleSpawnerSpawnsEagleAfterInterval() {
        int initialEnemyCount = enemyManager.getAll().size();

        // Tick until spawn interval completes (default: 800 ticks)
        for (int i = 0;
             i <= SpawnerBaseValues.EAGLE_SPAWN_INTERVAL; i++) {
            spawner.tick(engineState, gameState);
        }

        // An eagle should be spawned
        assertEquals(initialEnemyCount + 1,
                enemyManager.getAll().size());
    }

    @Test
    public void testEagleSpawnerDoesNotSpawnBeforeInterval() {
        int initialCount = enemyManager.getAll().size();

        // Tick less than the spawn interval
        for (int i = 0; i < SpawnerBaseValues.EAGLE_SPAWN_INTERVAL - 10; i++) {
            spawner.tick(engineState, gameState);
        }

        // No eagle should be spawned yet
        assertEquals(initialCount, enemyManager.getAll().size());
    }

    @Test
    public void testEagleSpawnerRepeatsSpawning() {
        int initialCount = enemyManager.getAll().size();

        // First spawn
        for (int i = 0;
             i <= SpawnerBaseValues.EAGLE_SPAWN_INTERVAL; i++) {
            spawner.tick(engineState, gameState);
        }
        assertEquals(initialCount + 1,
                enemyManager.getAll().size());

        // Second spawn (RepeatingTimer should restart)
        for (int i = 0;
             i <= SpawnerBaseValues.EAGLE_SPAWN_INTERVAL; i++) {
            spawner.tick(engineState, gameState);
        }
        assertEquals(initialCount + 2,
                enemyManager.getAll().size());
    }

    @Test
    public void testEagleSpawnerCreatesEagleAtCorrectLocation() {
        // Tick until spawn
        for (int i = 0;
             i <= SpawnerBaseValues.EAGLE_SPAWN_INTERVAL; i++) {
            spawner.tick(engineState, gameState);
        }

        // Check that eagle was spawned at spawner location
        assertEquals(SPAWN_X,
                enemyManager.getAll().getFirst().getX());
        assertEquals(SPAWN_Y,
                enemyManager.getAll().getFirst().getY());
    }

}