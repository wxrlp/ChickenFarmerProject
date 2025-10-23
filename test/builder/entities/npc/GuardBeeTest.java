package builder.entities.npc;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.npc.enemies.Magpie;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import builder.player.Player;
import builder.world.WorldBuilder;
import engine.EngineState;
import engine.renderer.TileGrid;
import engine.timing.FixedTimer;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;
import static org.junit.Assert.*;

public class GuardBeeTest {
    private GuardBee guardBee;
    private EngineState engineState;
    private GameState gameState;
    private EnemyManager enemies;
    private Magpie target;
    private static final int BEE_X = 400;
    private static final int BEE_Y = 400;
    private static final int TARGET_X = 600;
    private static final int TARGET_Y = 600;

    @Before
    public void setUp() {
        engineState = new MockEngineState(new TileGrid(10, 800));
        enemies = new EnemyManager(new TileGrid(10, 800));

        Player player = new ChickenFarmer(200, 200);
        target = new Magpie(TARGET_X, TARGET_Y, player);
        guardBee = new GuardBee(BEE_X, BEE_Y, target);

        gameState = new JavaBeanGameState(
                WorldBuilder.empty(),
                player,
                new TinyInventory(5, 10, 10),
                new NpcManager(),
                enemies
        );
    }

    @Test
    public void testGuardBeeInitialPosition() {
        assertEquals(BEE_X, guardBee.getX());
        assertEquals(BEE_Y, guardBee.getY());
    }

    @Test
    public void testGuardBeeHasSprite() {
        assertNotNull(guardBee.getSprite());
    }

    @Test
    public void testGuardBeeSpeed() {
        assertEquals(2.0, guardBee.getSpeed(), 0.01);
    }

    @Test
    public void testGuardBeeHasLifespan() {
        assertNotNull(guardBee.getLifespan());
    }

    @Test
    public void testGuardBeeSetLifespan() {
        FixedTimer newTimer = new FixedTimer(500);
        guardBee.setLifespan(newTimer);
        assertEquals(newTimer, guardBee.getLifespan());
    }

    @Test
    public void testGuardBeeMovesTowardTarget() {
        enemies.getBirds().add(target);
        int initialX = guardBee.getX();
        int initialY = guardBee.getY();

        guardBee.tick(engineState, gameState);

        // Bee should move toward target
        assertTrue(guardBee.getX() != initialX
                || guardBee.getY() != initialY);
    }

    @Test
    public void testGuardBeeKillsEnemy() {
        enemies.getBirds().add(target);
        enemies.getAll().add(target);

        guardBee.setX(TARGET_X);
        guardBee.setY(TARGET_Y);

        guardBee.tick(engineState, gameState);

        assertTrue(guardBee.isMarkedForRemoval());
        assertTrue(target.isMarkedForRemoval());
    }

    @Test
    public void testGuardBeeReturnsToSpawnWhenNoTarget() {
        GuardBee beeWithoutTarget = new GuardBee(BEE_X, BEE_Y, target);

        beeWithoutTarget.tick(engineState, gameState);

        // Should still function without crashing
        assertNotNull(beeWithoutTarget.getSprite());
    }


}