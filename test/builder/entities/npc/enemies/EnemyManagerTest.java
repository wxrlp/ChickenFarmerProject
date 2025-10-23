package builder.entities.npc.enemies;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
import builder.entities.npc.spawners.MagpieSpawner;
import builder.inventory.Inventory;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import builder.player.Player;
import builder.world.BeanWorld;
import builder.world.WorldBuilder;
import engine.EngineState;
import engine.renderer.Dimensions;
import engine.renderer.Renderable;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;

import java.util.List;

import static org.junit.Assert.*;

public class EnemyManagerTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private EnemyManager enemyManager;
    private Player player;
    private EngineState engineState;
    private GameState gameState;

    @Before
    public void setUp() {
        enemyManager = new EnemyManager(dimensions);
        player = new ChickenFarmer(400, 400);
        engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        Inventory inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();

        gameState = new JavaBeanGameState(world, player, inventory, npcs, enemyManager);
    }

    @Test
    public void testEnemyManagerInitiallyEmpty() {
        assertTrue(enemyManager.getAll().isEmpty());
    }

    @Test
    public void testMakeMagpieAddsToList() {
        Magpie magpie = enemyManager.makeMagpie(player);

        assertNotNull(magpie);
        assertEquals(1, enemyManager.getAll().size());
        assertTrue(enemyManager.getAll().contains(magpie));
    }

    @Test
    public void testMakePigeonAddsToList() {
        Pigeon pigeon = enemyManager.makePigeon(player);

        assertNotNull(pigeon);
        assertEquals(1, enemyManager.getAll().size());
        assertTrue(enemyManager.getAll().contains(pigeon));
    }

    @Test
    public void testMakeEagleDoesNotAddToList() {
        Eagle eagle = enemyManager.makeEagle(player);

        assertNotNull(eagle);
        assertEquals(0, enemyManager.getAll().size());
        assertFalse(enemyManager.getAll().contains(eagle));
    }

    @Test
    public void testSpawnCoordinatesAreSet() {
        enemyManager.setSpawnX(100);
        enemyManager.setSpawnY(200);

        Magpie magpie = enemyManager.makeMagpie(player);

        assertEquals(100, magpie.getX());
        assertEquals(200, magpie.getY());
    }

    @Test
    public void testAddSpawner() {
        MagpieSpawner spawner = new MagpieSpawner(100, 100, 500);
        enemyManager.add(spawner);

        assertEquals("Enemy manager should have 1 spawner",
                1, enemyManager.getSpawners().size());
    }

    @Test
    public void testGetMagpiesReturnsOnlyMagpies() {
        enemyManager.makeMagpie(player);
        enemyManager.makePigeon(player);
        enemyManager.makeMagpie(player);

        List<Magpie> magpies = enemyManager.getMagpies();

        assertEquals(2, magpies.size());
    }

    @Test
    public void testGetMagpiesReturnsEmptyWhenNoMagpies() {
        enemyManager.makePigeon(player);

        List<Magpie> magpies = enemyManager.getMagpies();

        assertTrue(magpies.isEmpty());
    }

    @Test
    public void testCleanupRemovesMarkedEnemies() {
        Magpie magpie1 = enemyManager.makeMagpie(player);
        Magpie magpie2 = enemyManager.makeMagpie(player);

        magpie1.markForRemoval();

        enemyManager.cleanup();

        assertEquals(1, enemyManager.getAll().size());
        assertFalse(enemyManager.getAll().contains(magpie1));
        assertTrue(enemyManager.getAll().contains(magpie2));
    }

    @Test
    public void testTickCallsTickOnAllEnemies() {
        Magpie magpie = enemyManager.makeMagpie(player);
        int initialX = magpie.getX();
        int initialY = magpie.getY();

        enemyManager.tick(engineState, gameState);

        // Enemy should have moved on tick call
        boolean moved =
                magpie.getX() != initialX
                        || magpie.getY() != initialY;
        assertTrue(moved || magpie.getSpeed() == 0);
    }

    @Test
    public void testRenderReturnsAllEnemies() {
        enemyManager.makeMagpie(player);
        enemyManager.makePigeon(player);

        List<Renderable> renderables = enemyManager.render();

        assertEquals(2, renderables.size());
    }

    @Test
    public void testRenderReturnsEmptyWhenNoEnemies() {
        List<Renderable> renderables = enemyManager.render();

        assertTrue(renderables.isEmpty());
    }

    @Test
    public void testMultipleCleanups() {
        Magpie magpie1 = enemyManager.makeMagpie(player);
        Magpie magpie2 = enemyManager.makeMagpie(player);
        Magpie magpie3 = enemyManager.makeMagpie(player);

        magpie1.markForRemoval();
        enemyManager.cleanup();

        assertEquals(2, enemyManager.getAll().size());

        magpie2.markForRemoval();
        magpie3.markForRemoval();
        enemyManager.cleanup();

        assertEquals(0, enemyManager.getAll().size());
    }

}