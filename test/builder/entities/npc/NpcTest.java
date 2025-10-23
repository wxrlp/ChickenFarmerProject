package builder.entities.npc;

import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.enemies.EnemyManager;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import builder.world.WorldBuilder;
import engine.EngineState;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;

import static org.junit.Assert.*;

public class NpcTest {
    private Npc npc;
    private EngineState engineState;
    private GameState gameState;

    @Before
    public void setUp() {
        npc = new Npc(100, 100);
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
    public void testNpcInitialPosition() {
        assertEquals(100, npc.getX());
        assertEquals(100, npc.getY());
    }

    @Test
    public void testNpcDefaultSpeed() {
        assertEquals(1, npc.getSpeed(), 0.01);
    }

    @Test
    public void testNpcSetSpeed() {
        npc.setSpeed(3.5);
        assertEquals(3.5, npc.getSpeed(), 0.01);
    }

    @Test
    public void testNpcDefaultDirection() {
        assertEquals(0, npc.getDirection());
    }

    @Test
    public void testSetNpcDefaultDirection() {
        npc.setDirection(5);
        assertEquals(5, npc.getDirection());
    }

    @Test
    public void testNpcMoveDown() {
        npc.setDirection(90); // 90 degrees = down
        npc.setSpeed(10);
        npc.move();
        assertEquals(100, npc.getX());
        assertTrue(npc.getY() > 100);
    }

    @Test
    public void testNpcMoveLeft() {
        npc.setDirection(180); // 180 degrees = left
        npc.setSpeed(10);
        npc.move();
        assertTrue(npc.getX() < 100);
        assertEquals(100, npc.getY());
    }

    @Test
    public void testNpcMoveUp() {
        npc.setDirection(270); // 270 degrees = up
        npc.setSpeed(10);
        npc.move();
        assertEquals(100, npc.getX());
        assertTrue(npc.getY() < 100);
    }

    @Test
    public void testNpcDistanceFromSamePosition() {
        int distance = npc.distanceFrom(100, 100);
        assertEquals(0, distance);
    }

    @Test
    public void testNpcTickMovesNpc() {
        npc.setDirection(0);
        npc.setSpeed(5);
        int initialX = npc.getX();

        npc.tick(engineState);

        assertNotEquals(initialX, npc.getX());
    }

    @Test
    public void testNpcTickWithGameState() {
        npc.setDirection(0);
        npc.setSpeed(5);
        int initialX = npc.getX();

        npc.tick(engineState, gameState);

        assertNotEquals(initialX, npc.getX());
    }
}