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
import engine.timing.FixedTimer;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;

import static org.junit.Assert.*;

public class EagleTest {
    private static final Dimensions dimensions =
            new TileGrid(10, 800);
    private Eagle eagle;
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
        eagle = new Eagle(SPAWN_X, SPAWN_Y, player);
        engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();
        EnemyManager enemies = new EnemyManager(dimensions);

        gameState =
                new JavaBeanGameState(
                        world, player, inventory, npcs,
                        enemies);
    }

    @Test
    public void testEagleInitialPosition() {
        assertEquals(SPAWN_X, eagle.getX());
        assertEquals(SPAWN_Y, eagle.getY());
    }

    @Test
    public void testEagleTrackedTarget() {
        assertEquals(player, eagle.getTrackedTarget());
    }

    @Test
    public void testEagleHasCorrectInitialSpeed() {
        assertEquals(2, eagle.getSpeed(), 0.01);
    }

    @Test
    public void testLifespanIsSet() {
        assertNotNull(eagle.getLifespan());
    }

    @Test
    public void testEagleCanUpdateLifespan() {
        FixedTimer newTimer = new FixedTimer(1000);
        eagle.setLifespan(newTimer);
        assertEquals(newTimer, eagle.getLifespan());
    }

    @Test
    public void testEagleTracksTarget() {
        assertNotNull(eagle.getTrackedTarget());
        assertEquals(player, eagle.getTrackedTarget());
    }


    @Test
    public void testEagleRemovesWhenLifespanFinished() {
        // Tick lifespan until finished
        for (int i = 0; i < 5001; i++) {
            eagle.getLifespan().tick();
        }

        eagle.tick(engineState, gameState);

        assertTrue(eagle.isMarkedForRemoval());
    }

    @Test
    public void testEagleStealsFood() {
        int initialFood = inventory.getFood();
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);

        eagle.tick(engineState, gameState);

        assertEquals(initialFood - 3, inventory.getFood());
    }

    @Test
    public void EagleSpeedsUpAfterFood() {
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);

        eagle.tick(engineState, gameState);
        assertEquals(4.0, eagle.getSpeed(), 0.01);
    }

    @Test
    public void testEagleReturnsFood() {
        int initialFood = inventory.getFood();

        // Eagle takes food
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);
        eagle.tick(engineState, gameState);
        assertEquals(initialFood - 3, inventory.getFood());

        // Eagle moves far away from player and removed
        eagle.setX(PLAYER_X + 100);
        eagle.setY(PLAYER_Y + 100);
        eagle.markForRemoval();
        eagle.tick(engineState, gameState);

        assertEquals(initialFood, inventory.getFood());
    }

    @Test
    public void EagleDoesntReturnFoodAtSpawn() {
        int initialFood = inventory.getFood();

        // Eagle takes food
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);
        eagle.tick(engineState, gameState);
        assertEquals(initialFood - 3, inventory.getFood());

        // Eagle returns to spawn and is removed
        eagle.setX(SPAWN_X);
        eagle.setY(SPAWN_Y);
        eagle.tick(engineState, gameState);

        assertEquals(initialFood - 3, inventory.getFood());
    }

    @Test
    public void CheckEagleMarkedForRemovalAtSpawn() {
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);
        eagle.tick(engineState, gameState);

        // Move eagle back to spawn
        eagle.setX(SPAWN_X);
        eagle.setY(SPAWN_Y);
        eagle.tick(engineState, gameState);
        assertTrue(eagle.isMarkedForRemoval());
    }

    @Test
    public void testEagleOnlyStealsOnce() {
        int initialFood = inventory.getFood();

        // Eagle takes food
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);
        eagle.tick(engineState, gameState);
        assertEquals(initialFood - 3, inventory.getFood());

        // Move eagle back to player position
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);
        eagle.tick(engineState, gameState);

        // Food should not decrease further
        assertEquals(initialFood - 3, inventory.getFood());
    }

}
