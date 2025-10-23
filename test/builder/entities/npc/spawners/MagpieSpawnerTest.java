package builder.entities.npc.spawners;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
import builder.entities.npc.enemies.Enemy;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.npc.enemies.Magpie;
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

public class MagpieSpawnerTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private static final int SPAWN_X = 150;
    private static final int SPAWN_Y = 250;

    private MagpieSpawner spawner;
    private GameState gameState;
    private EngineState engineState;
    private EnemyManager enemyManager;

    @Before
    public void setUp() {
        spawner = new MagpieSpawner(SPAWN_X, SPAWN_Y);
        engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        Player player = new ChickenFarmer(400, 400);
        Inventory inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();
        enemyManager = new EnemyManager(dimensions);

        gameState = new JavaBeanGameState(world, player, inventory, npcs, enemyManager);
    }

    @Test
    public void testMagpieSpawnerInitialPosition() {
        assertEquals(SPAWN_X, spawner.getX());
        assertEquals(SPAWN_Y, spawner.getY());
    }



    @Test
    public void testNoMagpieSpawnBeforeTimerFinished() {
        int initialMagpies = enemyManager.getAll().size();
        for (int i = 0;
             i < SpawnerBaseValues.MAGPIE_SPAWN_INTERVAL - 1; i++) {
            spawner.tick(engineState, gameState);
        }

        assertEquals(initialMagpies,
                enemyManager.getAll().size());
    }

    @Test
    public void testMagpieSpawnedAtSpawnerLocation() {
        for (int i = 0;
             i < SpawnerBaseValues.MAGPIE_SPAWN_INTERVAL; i++) {
            spawner.tick(engineState, gameState);
        }

        Magpie spawnedMagpie = null;
        for (Enemy enemy : enemyManager.getAll()) {
            if (enemy instanceof Magpie) {
                spawnedMagpie = (Magpie) enemy;
                break;
            }
        }

        assertNotNull(spawnedMagpie);
        assertEquals(SPAWN_X, spawnedMagpie.getX());
        assertEquals(SPAWN_Y, spawnedMagpie.getY());
    }



}