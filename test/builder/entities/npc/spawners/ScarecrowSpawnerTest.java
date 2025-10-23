package builder.entities.npc.spawners;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
import builder.entities.npc.Scarecrow;
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

public class ScarecrowSpawnerTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private static final int SPAWN_X = 150;
    private static final int SPAWN_Y = 250;

    private ScarecrowSpawner spawner;
    private GameState gameState;
    private MockEngineState engineState;
    private NpcManager npcManager;
    private Inventory inventory;
    private Player player;

    @Before
    public void setUp() {
        spawner = new ScarecrowSpawner(SPAWN_X, SPAWN_Y);
        engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        player = new ChickenFarmer(400, 400);
        inventory = new TinyInventory(5, 10, 10);
        npcManager = new NpcManager();
        EnemyManager enemies = new EnemyManager(dimensions);

        gameState = new JavaBeanGameState(world, player, inventory, npcManager, enemies);
    }

    @Test
    public void testScarecrowSpawnerInitialPosition() {
        assertEquals(SPAWN_X, spawner.getX());
        assertEquals(SPAWN_Y, spawner.getY());
    }

    @Test
    public void testScarecrowSpawnerHasTimer() {
        assertNotNull(spawner.getTimer());
        assertEquals(300,
                SpawnerBaseValues.SCARECROW_SPAWN_INTERVAL);
    }

    @Test
    public void testScarecrowSpawnerDoesNotSpawnWithoutKeyPress() {
        int initialNpcs = npcManager.getNpcs().size();

        // Tick without pressing 'c'
        for (int i = 0; i < 10; i++) {
            spawner.tick(engineState, gameState);
        }

        assertEquals(initialNpcs,
                npcManager.getNpcs().size());
    }

    @Test
    public void testScarecrowSpawnerNotEnoughCoins() {
        // 1 coin isn't enough
        inventory.addCoins(-inventory.getCoins() + 1);

        int initialNpcs = npcManager.getNpcs().size();

        engineState = engineState.press('c');
        spawner.tick(engineState, gameState);

        assertEquals(initialNpcs, npcManager.getNpcs().size());
    }

    @Test
    public void testSuccessfulScarecrowSpawn() {
        inventory.addCoins(Scarecrow.COIN_COST + 1);

        int initialNpcs = npcManager.getNpcs().size();

        engineState = engineState.press('c');
        spawner.tick(engineState, gameState);

        assertEquals(initialNpcs + 1,
                npcManager.getNpcs().size());
    }

    @Test
    public void testScarecrowSpawnerDeductsCoins() {
        int initialCoins = 10;
        inventory.addCoins(initialCoins - inventory.getCoins());

        engineState = engineState.press('c');
        spawner.tick(engineState, gameState);

        assertEquals(initialCoins + Scarecrow.COIN_COST,
                inventory.getCoins());
    }

    @Test
    public void testScarecrowSpawnsAtPlayerPosition() {
        inventory.addCoins(10);
        engineState = engineState.press('c');
        spawner.tick(engineState, gameState);

        // Get scarecrow entity from npc list
        Scarecrow scarecrow =
                (Scarecrow) npcManager.getNpcs().getFirst();

        // Crosscheck coordinates with player
        assertEquals(player.getX(), scarecrow.getX());
        assertEquals(player.getY(), scarecrow.getY());
    }

    @Test
    public void testScarecrowGetAndSetMethods() {
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
    public void testScarecrowPlacedMultipleTimes() {
        inventory.addCoins(50);

        int initialNpcs = npcManager.getNpcs().size();

        for (int i = 0; i < 3; i++) {
            engineState = engineState.press('c');
            spawner.tick(engineState, gameState);
        }

        assertEquals(initialNpcs + 3,
                npcManager.getNpcs().size());
    }

    @Test
    public void testScarecrowSpawnerChecksExactCoinCost() {
        // Set exact coins needed
        inventory.addCoins(-inventory.getCoins() + Scarecrow.COIN_COST);

        int initialNpcs = npcManager.getNpcs().size();

        engineState = engineState.press('c');
        spawner.tick(engineState, gameState);

        assertEquals(initialNpcs + 1,
                npcManager.getNpcs().size());
    }


}