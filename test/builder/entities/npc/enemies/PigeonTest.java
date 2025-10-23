package builder.entities.npc.enemies;

import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
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
import engine.timing.FixedTimer;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;

import static org.junit.Assert.*;

public class PigeonTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private Pigeon pigeon;
    private EngineState engineState;
    private GameState gameState;
    private BeanWorld world;
    private static final int SPAWN_X = 100;
    private static final int SPAWN_Y = 100;
    private static final int CABBAGE_X = 400;
    private static final int CABBAGE_Y = 400;

    @Before
    public void setUp() {
        world = WorldBuilder.empty();
        pigeon = new Pigeon(SPAWN_X, SPAWN_Y);
        engineState = new MockEngineState(dimensions);

        Player player = new ChickenFarmer(200, 200);
        Inventory inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();
        EnemyManager enemies = new EnemyManager(dimensions);

        gameState = new JavaBeanGameState(world, player, inventory, npcs, enemies);
    }

    @Test
    public void testPigeonInitialPosition() {
        assertEquals(SPAWN_X, pigeon.getX());
        assertEquals(SPAWN_Y, pigeon.getY());
    }

    @Test
    public void testPigeonInitiallyAttacking() {
        assertTrue(pigeon.isAttacking());
    }

    @Test
    public void testPigeonLifespanIsSet() {
        assertNotNull(pigeon.getLifespan());

    }

    @Test
    public void testPigeonUpdatesLifespan() {
        FixedTimer newTimer = new FixedTimer(1000);
        pigeon.setLifespan(newTimer);
        assertEquals(newTimer, pigeon.getLifespan());
    }

    @Test
    public void testPigeonRemovesWhenLifespanEnds() {
        // A test lifespan duration of 10
        pigeon.setLifespan(new FixedTimer(10));
        for (int i = 0; i < 11; i++) {
            pigeon.tick(engineState, gameState);
        }
        assertTrue(pigeon.isMarkedForRemoval());
    }

    @Test
    public void testPigeonTargetsCabbage() {
        Dirt dirtTile = new Dirt(CABBAGE_X, CABBAGE_Y);
        Cabbage cabbage = new Cabbage(CABBAGE_X, CABBAGE_Y);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        pigeon = new Pigeon(SPAWN_X, SPAWN_Y, cabbage);

        // Check if pigeon is targeting the cabbage
        assertNotNull(pigeon.getTrackedTarget());
        assertEquals(cabbage, pigeon.getTrackedTarget());
    }

    @Test
    public void testPigeonEatsCabbage() {
        Dirt dirtTile = new Dirt(CABBAGE_X, CABBAGE_Y);
        Cabbage cabbage = new Cabbage(CABBAGE_X, CABBAGE_Y);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        pigeon = new Pigeon(SPAWN_X, SPAWN_Y, cabbage);

        // Move pigeon to cabbage position
        pigeon.setX(CABBAGE_X);
        pigeon.setY(CABBAGE_Y);

        // Tick to simulate eating
        pigeon.tick(engineState, gameState);

        assertTrue(cabbage.isMarkedForRemoval());
    }

    @Test
    public void testPigeonRetreatsAfterEating() {
        Dirt dirtTile = new Dirt(CABBAGE_X, CABBAGE_Y);
        Cabbage cabbage = new Cabbage(CABBAGE_X, CABBAGE_Y);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        pigeon = new Pigeon(SPAWN_X, SPAWN_Y, cabbage);

        // Move pigeon to cabbage position
        pigeon.setX(CABBAGE_X);
        pigeon.setY(CABBAGE_Y);

        // Tick to simulate eating
        pigeon.tick(engineState, gameState);

        assertFalse(pigeon.isAttacking());
    }

    @Test
    public void PigeonRetreatsIfNoCabbages() {
        assertTrue(pigeon.isAttacking());
        pigeon.tick(engineState, gameState);
        assertFalse(pigeon.isAttacking());
    }

    @Test
    public void testPigeonDespawnsWhenBackAtSpawn() {
        // Condition "not attacking" must be met
        pigeon.setAttacking(false);
        // return to spawn
        pigeon.setX(SPAWN_X);
        pigeon.setY(SPAWN_Y);
        pigeon.tick(engineState, gameState);
        assertTrue(pigeon.isMarkedForRemoval());
    }

    @Test
    public void testPigeonMovesToCenterWhenNoTarget() {
        pigeon = new Pigeon(SPAWN_X, SPAWN_Y, null);
        int initialX = pigeon.getX();
        int initialY = pigeon.getY();
        // Game ticks, pigeon should move away from initial position
        pigeon.tick(engineState, gameState);
        int newX = pigeon.getX();
        int newY = pigeon.getY();

        boolean pigeonMoved = (initialX != newX) || (initialY != newY);
        assertTrue(pigeonMoved);
    }

    @Test
    public void testPigeonFindsClosestCabbage() {
        Dirt dirtTile1 = new Dirt(200, 200);
        Cabbage cabbage1 = new Cabbage(200, 200);
        dirtTile1.placeOn(cabbage1);
        world.place(dirtTile1);

        Dirt dirtTile2 = new Dirt(600, 600);
        Cabbage cabbage2 = new Cabbage(600, 600);
        dirtTile2.placeOn(cabbage2);
        world.place(dirtTile2);

        // Tick to allow pigeon to find the closest cabbage
        pigeon.tick(engineState, gameState);

        assertNotNull(pigeon.getTrackedTarget());
        assertEquals(cabbage1.getX(),
                pigeon.getTrackedTarget().getX());

    }


    @Test
    public void testPigeonDirectionChangesWhenTargetMoves() {
        // Create cabbage at one location
        Dirt dirtTile = new Dirt(SPAWN_X + 100, SPAWN_Y);
        Cabbage cabbage = new Cabbage(SPAWN_X + 100, SPAWN_Y);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        pigeon.tick(engineState, gameState);
        int firstDirection = pigeon.getDirection();

        // Adds a closer cabbage in different direction
        Dirt dirtTile2 = new Dirt(SPAWN_X - 50, SPAWN_Y + 50);
        Cabbage cabbage2 = new Cabbage(SPAWN_X - 50, SPAWN_Y + 50);
        dirtTile2.placeOn(cabbage2);
        world.place(dirtTile2);

        // Tick again - pigeon should retarget closer cabbage
        pigeon.tick(engineState, gameState);
        int secondDirection = pigeon.getDirection();

        assertNotEquals(firstDirection, secondDirection);
    }

    @Test
    public void testReturnToSpawnMarksForRemovalWhenCloseToSpawn() {
        pigeon.setAttacking(false);

        // Position pigeon very close to spawn (within half tile size)
        int halfTile = dimensions.tileSize() / 2;
        pigeon.setX(SPAWN_X + halfTile / 2);
        pigeon.setY(SPAWN_Y + halfTile / 2);

        pigeon.tick(engineState, gameState);

        // Pigeon should be marked for removal when close to spawn
        assertTrue(pigeon.isMarkedForRemoval());
    }

    @Test
    public void testReturnToSpawnDoesNotRemoveWhenFarFromSpawn() {
        pigeon.setAttacking(false);

        // Position pigeon far from spawn (more than tile size away)
        int tileSize = dimensions.tileSize();
        pigeon.setX(SPAWN_X + tileSize * 2);
        pigeon.setY(SPAWN_Y + tileSize * 2);

        pigeon.tick(engineState, gameState);

        // Pigeon should not be marked for removal when far from spawn
        assertFalse(pigeon.isMarkedForRemoval());
    }

    @Test
    public void testReturnToSpawnUpdatesSprite() {
        // Position pigeon below spawn (spawn is above)
        pigeon.setX(SPAWN_X);
        pigeon.setY(SPAWN_Y + 100);
        pigeon.setAttacking(false);

        pigeon.tick(engineState, gameState);

        // Sprite should be "up" when returning to spawn that's above
        assertEquals("pigeon:up",
                pigeon.getSprite().getLabel());
    }

    @Test
    public void testTickCallsReturnToSpawnWhenNotAttacking() {
        // Position pigeon away from spawn
        pigeon.setX(SPAWN_X + 100);
        pigeon.setY(SPAWN_Y + 100);
        pigeon.setAttacking(false);

        int beforeDirection = pigeon.getDirection();
        pigeon.tick(engineState, gameState);
        int afterDirection = pigeon.getDirection();

        // Direction should be updated to point toward spawn
        assertNotEquals(beforeDirection, afterDirection);
    }

    @Test
    public void testTickSearchesForCabbagesAndRetargets() {
        // Start with no target
        assertNull(pigeon.getTrackedTarget());

        // Add a cabbage to the world
        Dirt dirtTile = new Dirt(CABBAGE_X, CABBAGE_Y);
        Cabbage cabbage = new Cabbage(CABBAGE_X, CABBAGE_Y);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        // Tick should find the cabbage and set it as target
        pigeon.tick(engineState, gameState);

        assertNotNull(pigeon.getTrackedTarget());
    }

    @Test
    public void testCompleteTickCycle() {
        // Add cabbage
        Dirt dirtTile = new Dirt(CABBAGE_X, CABBAGE_Y);
        Cabbage cabbage = new Cabbage(CABBAGE_X, CABBAGE_Y);
        dirtTile.placeOn(cabbage);
        world.place(dirtTile);

        // Phase 1: Attacking
        pigeon.tick(engineState, gameState);
        assertTrue(pigeon.isAttacking());
        assertNotNull(pigeon.getTrackedTarget());

        // Phase 2: Reach cabbage and eat it
        pigeon.setX(CABBAGE_X);
        pigeon.setY(CABBAGE_Y);
        pigeon.tick(engineState, gameState);

        assertFalse(pigeon.isAttacking());
        assertTrue(cabbage.isMarkedForRemoval());

        // Phase 3: Return to spawn
        pigeon.setX(SPAWN_X);
        pigeon.setY(SPAWN_Y);
        pigeon.tick(engineState, gameState);

        assertTrue(pigeon.isMarkedForRemoval());
    }




}