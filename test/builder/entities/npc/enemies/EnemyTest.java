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

public class EnemyTest {
    private static final Dimensions dimensions = new TileGrid(10, 800);
    private Enemy enemy;

    @Before
    public void setUp() {
        enemy = new Enemy(100, 200);
        EngineState engineState = new MockEngineState(dimensions);

        BeanWorld world = WorldBuilder.empty();
        Player player = new ChickenFarmer(400, 400);
        Inventory inventory = new TinyInventory(5, 10, 10);
        NpcManager npcs = new NpcManager();
        EnemyManager enemies = new EnemyManager(dimensions);

        GameState gameState =
                new JavaBeanGameState(
                        world, player, inventory, npcs,
                        enemies);
    }

    @Test
    public void testEnemyInitialPosition() {
        assertEquals(100, enemy.getX());
        assertEquals(200, enemy.getY());
    }

    @Test
    public void testEnemyNotMarkedForRemovalInitially() {
        assertFalse(enemy.isMarkedForRemoval());
    }

    @Test
    public void testEnemyCanBeMarkedForRemoval() {
        enemy.markForRemoval();
        assertTrue(enemy.isMarkedForRemoval());
    }

    @Test
    public void testDistanceFromSamePosition() {
        int distance = enemy.distanceFrom(100,
                200);
        assertEquals(0, distance);
    }
}