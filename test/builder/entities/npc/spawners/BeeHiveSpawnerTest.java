package builder.entities.npc.spawners;

import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.BeeHive;
import builder.entities.npc.NpcManager;
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

public class BeeHiveSpawnerTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private static final int SPAWN_X = 100;
    private static final int SPAWN_Y = 200;

    private BeeHiveSpawner spawner;
    private GameState gameState;
    private MockEngineState engineState;
    private NpcManager npcManager;
    private Inventory inventory;
    private Player player;

    @Before
    public void setUp() {
        spawner = new BeeHiveSpawner(SPAWN_X, SPAWN_Y, 240);
        engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        player = new ChickenFarmer(400, 400);
        inventory = new TinyInventory(5, 10, 10);
        npcManager = new NpcManager();
        EnemyManager enemies = new EnemyManager(dimensions);

        gameState = new JavaBeanGameState(world, player, inventory, npcManager, enemies);
    }

    @Test
    public void testBeeHiveSpawnerInitialPosition() {
        assertEquals(SPAWN_X, spawner.getX());
        assertEquals(SPAWN_Y, spawner.getY());
    }

    @Test
    public void testBeeHiveSpawnerHasTimer() {
        assertNotNull(spawner.getTimer());
        assertEquals(240,
                SpawnerBaseValues.BEEHIVE_SPAWN_INTERVAL);
    }

    @Test
    public void testBeeHiveDoesNotSpawnWithoutKeyPress() {
        int initialNpcs = npcManager.getNpcs().size();

        //Ticks without pressing 'h' key
        for (int i = 0; i < 10; i++) {
            spawner.tick(engineState, gameState);
        }

        assertEquals(initialNpcs, npcManager.getNpcs().size());
    }

    @Test
    public void testBeeHiveInsufficientResources() {
        // 1 food and 1 coin isn't enough to spawn beehive
        inventory.addCoins(-inventory.getCoins() + 1);
        inventory.addFood(-inventory.getFood() + 1);

        int initialNpcs = npcManager.getNpcs().size();

        // 'h' key pressed
        engineState = engineState.press('h');
        spawner.tick(engineState, gameState);

        assertEquals(initialNpcs, npcManager.getNpcs().size());
    }

    @Test
    public void testSuccessfulBeehivePlacement() {
        inventory.addCoins(2);
        inventory.addFood(2);

        int initialNpcs = npcManager.getNpcs().size();

        engineState = engineState.press('h');
        spawner.tick(engineState, gameState);

        assertEquals(initialNpcs + 1,
                npcManager.getNpcs().size());

    }

    @Test
    public void testCheckInventoryResourcesUsed() {
        int initialFood = 10;
        int initialCoins = 10;

        inventory.addCoins(initialCoins - inventory.getCoins());
        inventory.addFood(initialFood - inventory.getFood());

        int initialNpcs = npcManager.getNpcs().size();

        engineState = engineState.press('h');
        spawner.tick(engineState, gameState);

        assertEquals(initialNpcs + 1, npcManager.getNpcs().size());
        assertEquals(initialFood + BeeHive.FOOD_COST,
                inventory.getFood());
        assertEquals(initialCoins + BeeHive.COIN_COST,
                inventory.getCoins());

    }

    @Test
    public void testBeeHiveSpawnsAtPlayerLocation() {
        inventory.addCoins(10);
        inventory.addFood(10);

        engineState = engineState.press('h');
        spawner.tick(engineState, gameState);

        BeeHive hive = (BeeHive) npcManager.getNpcs().getFirst();
        assertEquals(player.getX(), hive.getX());
        assertEquals(player.getY(), hive.getY());
    }

    @Test
    public void testSpawnerGetAndSetMethods() {
        spawner.setX(300);
        spawner.setY(300);

        assertEquals(300, spawner.getX());
        assertEquals(300, spawner.getY());

        spawner.setX(400);
        spawner.setY(400);

        assertEquals(400, spawner.getX());
        assertEquals(400, spawner.getY());
    }

    @Test
    public void testMultipleSpawnsOfBeehives() {
        inventory.addCoins(50);
        inventory.addFood(50);

        for (int i = 0; i < 3; i++) {
            engineState = engineState.press('h');
            spawner.tick(engineState, gameState);
        }

        assertEquals(3, npcManager.getNpcs().size());
    }

    @Test
    public void testBeeHiveSpawnerChecksExactResourceCost() {
        // Set exact resources needed
        inventory.addCoins(-inventory.getCoins() + BeeHive.COIN_COST);
        inventory.addFood(-inventory.getFood() + BeeHive.FOOD_COST);

        int initialNpcs = npcManager.getNpcs().size();

        engineState = engineState.press('h');
        spawner.tick(engineState, gameState);

        assertEquals(initialNpcs + 1,
                npcManager.getNpcs().size());
    }


}