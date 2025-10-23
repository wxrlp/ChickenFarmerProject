package builder.entities.npc;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.npc.enemies.Magpie;
import builder.entities.npc.enemies.Pigeon;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import builder.player.Player;
import builder.world.WorldBuilder;
import engine.EngineState;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;
import static org.junit.Assert.*;

public class ScarecrowTest {
    private Scarecrow scarecrow;
    private EngineState engineState;
    private GameState gameState;
    private EnemyManager enemies;
    private static final int SCARECROW_X = 400;
    private static final int SCARECROW_Y = 400;

    @Before
    public void setUp() {
        scarecrow = new Scarecrow(SCARECROW_X, SCARECROW_Y);
        engineState = new MockEngineState(new TileGrid(10, 800));
        enemies = new EnemyManager(new TileGrid(10, 800));

        Player player = new ChickenFarmer(200, 200);
        gameState = new JavaBeanGameState(
                WorldBuilder.empty(),
                player,
                new TinyInventory(5, 10, 10),
                new NpcManager(),
                enemies
        );
    }

    @Test
    public void testScarecrowInitialPosition() {
        assertEquals(SCARECROW_X, scarecrow.getX());
        assertEquals(SCARECROW_Y, scarecrow.getY());
    }

    @Test
    public void testScarecrowHasSprite() {
        assertNotNull(scarecrow.getSprite());
    }

    @Test
    public void testScarecrowSpeedIsZero() {
        assertEquals(0.0, scarecrow.getSpeed(), 0.01);
    }

    @Test
    public void testScarecrowCoinCost() {
        assertEquals(2, Scarecrow.COIN_COST);
    }

    @Test
    public void testScarecrowScaresMagpieInRange() {
        // Add magpie within scare radius (4 tiles)
        int tileSize = engineState.getDimensions().tileSize();
        Magpie magpie = new Magpie(
                SCARECROW_X + tileSize * 2,
                SCARECROW_Y + tileSize * 2,
                gameState.getPlayer()
        );
        magpie.setAttacking(true);
        enemies.getBirds().add(magpie);

        scarecrow.interact(engineState, gameState);

        assertFalse(magpie.isAttacking());
    }

    @Test
    public void testScarecrowScaresPigeonInRange() {
        int tileSize = engineState.getDimensions().tileSize();
        Pigeon pigeon = new Pigeon(
                SCARECROW_X + tileSize * 2,
                SCARECROW_Y + tileSize * 2
        );
        pigeon.setAttacking(true);
        enemies.getBirds().add(pigeon);

        scarecrow.interact(engineState, gameState);

        assertFalse(pigeon.isAttacking());
    }

    @Test
    public void testScarecrowDoesNotScarePigeonOutOfRange() {
        int tileSize = engineState.getDimensions().tileSize();
        Pigeon pigeon = new Pigeon(
                SCARECROW_X + tileSize * 5,
                SCARECROW_Y + tileSize * 5
        );
        pigeon.setAttacking(true);
        enemies.getBirds().add(pigeon);

        scarecrow.interact(engineState, gameState);

        assertTrue(pigeon.isAttacking());
    }

    @Test
    public void testScarecrowScaresMultipleBirds() {
        int tileSize = engineState.getDimensions().tileSize();

        Magpie magpie = new Magpie(
                SCARECROW_X + tileSize,
                SCARECROW_Y + tileSize,
                gameState.getPlayer()
        );
        magpie.setAttacking(true);

        Pigeon pigeon = new Pigeon(
                SCARECROW_X + tileSize * 2,
                SCARECROW_Y + tileSize * 2
        );
        pigeon.setAttacking(true);

        enemies.getBirds().add(magpie);
        enemies.getBirds().add(pigeon);

        scarecrow.interact(engineState, gameState);

        assertFalse(magpie.isAttacking());
        assertFalse(pigeon.isAttacking());
    }

    @Test
    public void testScarecrowInteractWithNoEnemies() {
        // No exception thrown
        scarecrow.interact(engineState, gameState);
    }

    @Test
    public void testScarecrowDoesNotMove() {
        int initialX = scarecrow.getX();
        int initialY = scarecrow.getY();

        scarecrow.move();

        assertEquals(initialX, scarecrow.getX());
        assertEquals(initialY, scarecrow.getY());
    }







}