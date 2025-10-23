package builder.entities.npc;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.enemies.EnemyManager;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import builder.world.WorldBuilder;
import engine.EngineState;
import engine.renderer.Renderable;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;
import java.util.List;
import static org.junit.Assert.*;

public class NpcManagerTest {
    private NpcManager manager;
    private EngineState engineState;
    private GameState gameState;

    @Before
    public void setUp() {
        manager = new NpcManager();
        engineState = new MockEngineState(new TileGrid(10, 800));
        gameState = new JavaBeanGameState(
                WorldBuilder.empty(),
                new ChickenFarmer(200, 200),
                new TinyInventory(5, 10, 10),
                new NpcManager(),
                new EnemyManager(new TileGrid(10, 800))
        );
    }

    @Test
    public void testNpcManagerCreatedEmpty() {
        assertEquals(0, manager.getNpcs().size());
    }

    @Test
    public void testNpcManagerAddNpc() {
        Npc testNpc = new Npc(100, 100);
        manager.addNpc(testNpc);

        assertEquals(1, manager.getNpcs().size());
        assertTrue(manager.getNpcs().contains(testNpc));
    }

    @Test
    public void testNpcManagerAddMultipleNpcs() {
        Npc testNpc1 = new Npc(100, 100);
        Npc testNpc2 = new BeeHive(200, 200);
        Npc testNpc3 = new Scarecrow(300, 300);

        manager.addNpc(testNpc1);
        manager.addNpc(testNpc2);
        manager.addNpc(testNpc3);

        assertEquals(3, manager.getNpcs().size());
        assertTrue(manager.getNpcs().contains(testNpc1));
        assertTrue(manager.getNpcs().contains(testNpc2));
        assertTrue(manager.getNpcs().contains(testNpc3));
    }

    @Test
    public void testNpcManagerCleanupRemovesMarkedNpcs() {
        Npc npc1 = new Npc(100, 100);
        Npc npc2 = new Npc(200, 200);
        npc2.markForRemoval();

        manager.addNpc(npc1);
        manager.addNpc(npc2);

        manager.cleanup();

        assertEquals(1, manager.getNpcs().size());
        assertTrue(manager.getNpcs().contains(npc1));
        assertFalse(manager.getNpcs().contains(npc2));
    }

    @Test
    public void testNpcManagerTickCallsCleanup() {
        Npc npc = new Npc(100, 100);
        npc.markForRemoval();
        manager.addNpc(npc);

        manager.tick(engineState, gameState);

        assertEquals(0, manager.getNpcs().size());
    }

    @Test
    public void testNpcManagerTickUpdatesNpcs() {
        Npc npc = new Npc(100, 100);
        npc.setDirection(0);
        npc.setSpeed(5);
        manager.addNpc(npc);

        int initialX = npc.getX();
        manager.tick(engineState, gameState);

        assertNotEquals(initialX, npc.getX());
    }

    @Test
    public void testNpcManagerRenderReturnsAllNpcs() {
        Npc npc1 = new Npc(100, 100);
        Npc npc2 = new Npc(200, 200);
        manager.addNpc(npc1);
        manager.addNpc(npc2);

        List<Renderable> renderables = manager.render();

        assertEquals(2, renderables.size());
    }

    @Test
    public void testNpcManagerCleanupWithMultipleMarkedNpcs() {
        Npc npc1 = new Npc(100, 100);
        Npc npc2 = new Npc(200, 200);
        Npc npc3 = new Npc(300, 300);

        npc1.markForRemoval();
        npc3.markForRemoval();

        manager.addNpc(npc1);
        manager.addNpc(npc2);
        manager.addNpc(npc3);

        manager.cleanup();

        assertEquals(1, manager.getNpcs().size());
        assertTrue(manager.getNpcs().contains(npc2));
    }

    @Test
    public void testNpcManagerWithNoNpcs() {
        manager.tick(engineState, gameState);
        manager.interact(engineState, gameState);
        List<Renderable> renderables = manager.render();

        assertEquals(0, renderables.size());
    }

}