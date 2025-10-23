package builder.entities.npc;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.npc.enemies.Magpie;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import builder.world.WorldBuilder;
import engine.EngineState;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;
import static org.junit.Assert.*;

public class BeeHiveTest {
    private BeeHive beeHive;
    private EngineState engineState;
    private GameState gameState;
    private EnemyManager enemies;
    private NpcManager npcs;
    private static final int HIVE_X = 400;
    private static final int HIVE_Y = 400;

    @Before
    public void setUp() {
        beeHive = new BeeHive(HIVE_X, HIVE_Y);
        engineState = new MockEngineState(new TileGrid(10,
                800));
        npcs = new NpcManager();
        enemies = new EnemyManager(new TileGrid(10,
                800));

        gameState = new JavaBeanGameState(
                WorldBuilder.empty(),
                new ChickenFarmer(200, 200),
                new TinyInventory(5, 10, 10),
                npcs,
                enemies
        );
    }

    @Test
    public void testBeehiveConstants() {
        assertEquals(350, BeeHive.DETECTION_DISTANCE);
        assertEquals(2, BeeHive.COIN_COST);
        assertEquals(2, BeeHive.FOOD_COST);
        assertEquals(240, BeeHive.RELOAD_COOLDOWN_TICKS);

    }

    @Test
    public void testBeeHiveSpeedIsZero() {
        assertEquals(0.0, beeHive.getSpeed(), 0.01);
    }

    @Test
    public void testBeeHiveSpawnsBeeWhenEnemyEntersRange() {
        Magpie magpie = new Magpie(HIVE_X + 100, HIVE_Y + 100,
                gameState.getPlayer());
        enemies.getBirds().add(magpie);

        Npc spawnedBee = beeHive.checkAndSpawnBee(enemies.getBirds());

        assertNotNull(spawnedBee);
        assertTrue(spawnedBee instanceof GuardBee);
        assertEquals(HIVE_Y, spawnedBee.getY());
        assertEquals(HIVE_X, spawnedBee.getX());
    }

    @Test
    public void testBeeHiveDoesNotSpawnBeeWhenEnemyOutOfRange() {
        Magpie magpie = new Magpie(HIVE_X + 400, HIVE_Y + 400,
                gameState.getPlayer());
        enemies.getBirds().add(magpie);

        Npc spawnedBee = beeHive.checkAndSpawnBee(enemies.getBirds());

        assertNull(spawnedBee);
    }

    @Test
    public void testBeeHiveOnlySpawnsOneGuardBeeAtATime() {
        Magpie magpie = new Magpie(HIVE_X + 100, HIVE_Y + 100,
                gameState.getPlayer());
        enemies.getBirds().add(magpie);

        // First spawn should succeed
        Npc firstBee = beeHive.checkAndSpawnBee(enemies.getBirds());
        assertNotNull(firstBee);

        // Second spawn immediately should fail (not loaded)
        Npc secondBee = beeHive.checkAndSpawnBee(enemies.getBirds());
        assertNull(secondBee);
    }

    @Test
    public void testBeeHiveInteractAddsSpawnedBeeToManager() {
        Magpie magpie = new Magpie(HIVE_X + 100, HIVE_Y + 100,
                gameState.getPlayer());
        enemies.getBirds().add(magpie);

        int initialCount = npcs.getNpcs().size();
        beeHive.interact(engineState, gameState);

        assertEquals(initialCount + 1,
                npcs.getNpcs().size());
    }

    @Test
    public void testBeehiveWithEmptyEnemyList() {
        Npc spawnedBee = beeHive.checkAndSpawnBee(enemies.getBirds());
        assertNull(spawnedBee);
    }

    @Test
    public void testBeehiveInteractWithNoEnemies() {
        // No exception thrown
        beeHive.interact(engineState, gameState);
    }

}