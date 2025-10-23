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
    public void testEagleSpriteExists() {
        assertNotNull(eagle.getSprite());
    }

    @Test
    public void testEagleFinishesLifespanAndDespawns() {
        // Set a shorter lifespan for testing
        eagle.setLifespan(new FixedTimer(10));

        // Tick 11 times (10 + 1 to finish)
        for (int i = 0; i < 11; i++) {
            eagle.tick(engineState, gameState);
        }

        assertTrue(eagle.isMarkedForRemoval());
    }


    @Test
    public void testEagleSetNewDirection() {
        eagle.setDirection(90);
        assertEquals(90, eagle.getDirection());
    }

    @Test
    public void testEagleStillAttackingIfNotNearPlayer() {
        eagle.setX(PLAYER_X + dimensions.tileSize() + 5);
        eagle.setY(PLAYER_Y + dimensions.tileSize() + 5);
        eagle.setAttacking(true);
        eagle.tick(engineState, gameState);
        assertTrue(eagle.isAttacking());
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
    public void EagleDoesNotReturnFoodAtSpawn() {
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
    @Test
    public void testEagleSpriteChangesWhenAttackingPlayerBelow() {
        // Position eagle above the player
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y - 100);  // Eagle is 100 pixels above player
        eagle.setAttacking(true);

        eagle.tick(engineState, gameState);

        String eagleSpriteLabel = eagle.getSprite().getLabel();
        // When attacking and player is below, sprite should be "down"
        assertEquals("eagle:down", eagleSpriteLabel);
    }

    @Test
    public void testEagleSpriteChangesWhenAttackingPlayerAbove() {
        // Position eagle below the player
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y + 100);  // Eagle is 100 pixels below
        // player
        eagle.setAttacking(true);

        eagle.tick(engineState, gameState);

        String eagleSpriteLabel = eagle.getSprite().getLabel();
        // When attacking and player is below, sprite should be "up"
        assertEquals("eagle:up", eagleSpriteLabel);
    }


    @Test
    public void testEagleSpriteChangesFromAttackToRetreat() {
        // Start: Eagle is attacking player (who is below)
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y - 100);
        eagle.setAttacking(true);

        eagle.tick(engineState, gameState);
        String attackSprite = eagle.getSprite().getLabel();

        // Trigger retreat by positioning eagle at player location
        eagle.setX(PLAYER_X);
        eagle.setY(PLAYER_Y);
        eagle.tick(engineState, gameState);

        // Now eagle should be retreating
        assertFalse(eagle.isAttacking());

        // Position eagle so spawn is above (opposite of initial attack direction)
        eagle.setX(SPAWN_X);
        eagle.setY(SPAWN_Y + 50);
        eagle.tick(engineState, gameState);

        String retreatSprite = eagle.getSprite().getLabel();

        // Sprite should have changed from attack to retreat behavior
        // Attack had "down", retreat should now have "up" (since spawn is above)
        assertEquals("eagle:down", attackSprite);
        assertEquals("eagle:up", retreatSprite);
    }

    @Test
    public void testCheckDefaultSpriteOnCreation() {
        assertEquals("eagle:default",
                eagle.getSprite().getLabel());
    }

    @Test
    public void testCheckDeltaX() {
        eagle.setX(200);
        eagle.setY(200);
        double deltaX = player.getX() - eagle.getX();
        assertEquals(200, deltaX, 0.01);
    }

    @Test
    public void testCheckDeltaY() {
        eagle.setX(200);
        eagle.setY(200);
        double deltaY = player.getY() - eagle.getY();
        assertEquals(200, deltaY, 0.01);
    }

    @Test
    public void testCheckInitialFoodIsZero() {
        assertEquals(0, eagle.getFood());
    }
}
