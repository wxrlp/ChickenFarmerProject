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
    private static final Dimensions dimensions = new TileGrid(10, 800);
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

        gameState = new JavaBeanGameState(world, player, inventory, npcs, enemies);
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
    public void tick() {
    }

    @Test
    public void getTrackedTarget() {
    }

    @Test
    public void setTrackedTarget() {
    }
}