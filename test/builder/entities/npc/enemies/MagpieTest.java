package builder.entities.npc.enemies;

import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
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

public class MagpieTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private Magpie magpie;
    private Player player;
    private Inventory inventory;
    private EngineState engineState;
    private GameState gameState;
    private static final int SPAWN_X = 100;
    private static final int SPAWN_Y = 100;
    private static final int PLAYER_X = 400;
    private static final int PLAYER_Y = 400;


    @Before
    public void setUp() {
        player = new ChickenFarmer(PLAYER_X, PLAYER_Y);
        magpie = new Magpie(SPAWN_X, SPAWN_Y, player);
        engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();
        EnemyManager enemies = new EnemyManager(dimensions);

        gameState = new JavaBeanGameState(world, player, inventory, npcs, enemies);
    }

    @Test
    public void testMagpieInitialPosition() {
        assertEquals(SPAWN_X, magpie.getX());
        assertEquals(SPAWN_Y, magpie.getY());
    }

    @Test
    public void testMagpieTrackedTarget() {
        assertEquals(player, magpie.getTrackedTarget());
    }

    @Test
    public void testMagpieSpeedsUpAfterStealing() {
        magpie.setX(PLAYER_X);
        magpie.setY(PLAYER_Y);
        magpie.tick(engineState, gameState);
        assertEquals(2, magpie.getSpeed(), 0.01);
    }

    @Test
    public void testMagpieRetreatsAfterStealing() {
        magpie.setX(PLAYER_X);
        magpie.setY(PLAYER_Y);
        magpie.tick(engineState, gameState);
        assertFalse(magpie.getAttacking());
    }

    @Test
    public void testMagpieStealsOnlyIfCoinsAvailable() {
        // Set inventory to 0 coins
        inventory.addCoins(-inventory.getCoins());
        assertEquals(0, inventory.getCoins());

        magpie.setX(PLAYER_X);
        magpie.setY(PLAYER_Y);
        magpie.tick(engineState, gameState);

        assertEquals(0, inventory.getCoins());
        assertTrue(magpie.getAttacking());
    }

    @Test
    public void testMagpieDisappearsWhenBackAtSpawn() {
        magpie.setX(PLAYER_X);
        magpie.setY(PLAYER_Y);
        magpie.tick(engineState, gameState);
        assertFalse(magpie.getAttacking());

        // Magpie should be marked for removal when back at spawn
        magpie.setX(SPAWN_X);
        magpie.setY(SPAWN_Y);
        magpie.tick(engineState, gameState);
        assertTrue(magpie.isMarkedForRemoval());
    }

    @Test
    public void testMagpieReturnsCoins() {
        // Check if coin is stolen initially
        int initialCoins = inventory.getCoins();
        magpie.setX(PLAYER_X);
        magpie.setY(PLAYER_Y);
        magpie.tick(engineState, gameState);
        assertEquals("Coin should be stolen",
                initialCoins - 1,
                inventory.getCoins());

        // Moves magpie far away
        magpie.setX(PLAYER_X + 100);
        magpie.setY(PLAYER_Y + 100);

        // Matches conditions to return coins (must be attacking)
        magpie.setAttacking(true);
        magpie.markForRemoval();
        magpie.tick(engineState, gameState);
        assertEquals("Coin should be returned", initialCoins,
                inventory.getCoins());
    }

    @Test
    public void testMagpieSuccessfullyRetreats() {
        int initialCoins = inventory.getCoins();
        magpie.setX(PLAYER_X);
        magpie.setY(PLAYER_Y);
        magpie.tick(engineState, gameState);
        assertEquals("Coin should be stolen",
                initialCoins - 1,
                inventory.getCoins());

        // Return to spawn and successfully despawn
        magpie.setX(SPAWN_X);
        magpie.setY(SPAWN_Y);
        magpie.tick(engineState, gameState);

        assertEquals(initialCoins - 1, inventory.getCoins());
    }


}