package builder.world;
import builder.GameState;
import builder.JavaBeanGameState;
import builder.entities.npc.NpcManager;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.resources.Cabbage;
import builder.entities.tiles.Dirt;
import builder.entities.tiles.Grass;
import builder.entities.tiles.Tile;
import builder.entities.tiles.Water;
import builder.inventory.TinyInventory;
import builder.player.ChickenFarmer;
import engine.EngineState;
import engine.renderer.Dimensions;
import engine.renderer.Renderable;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;
import scenarios.mocks.MockEngineState;

import java.util.List;

import static org.junit.Assert.*;

public class BeanWorldTest {
    private BeanWorld world;
    private Dimensions dimensions;
    private EngineState engineState;
    private GameState gameState;

    @Before
    public void setUp() {
        world = WorldBuilder.empty();
        dimensions = new TileGrid(10, 800);
        engineState = new MockEngineState(dimensions);

        gameState = new JavaBeanGameState(
                world,
                new ChickenFarmer(200, 200),
                new TinyInventory(5, 10, 10),
                new NpcManager(),
                new EnemyManager(dimensions)
        );
    }

    @Test
    public void testEmptyWorldHasNoTiles() {
        List<Tile> tiles = world.allTiles();
        assertEquals(0, tiles.size());
    }

    @Test
    public void testPlaceTileInWorld() {
        Tile tile = new Dirt(0, 0);
        world.place(tile);

        List<Tile> tiles = world.allTiles();
        assertEquals(1, tiles.size());
        assertTrue(tiles.contains(tile));
    }

    @Test
    public void testPlaceMultipleTiles() {
        Tile tile1 = new Dirt(0, 0);
        Tile tile2 = new Grass(100, 100);
        Tile tile3 = new Water(200, 200);

        world.place(tile1);
        world.place(tile2);
        world.place(tile3);

        List<Tile> tiles = world.allTiles();
        assertEquals(3, tiles.size());

    }

    @Test
    public void testTilesAtPositionFindsCorrectTile() {
        Tile tile = new Dirt(0, 0);
        world.place(tile);

        // Any position within the first tile should find it
        List<Tile> found = world.tilesAtPosition(5, 5, dimensions);

        assertEquals(1, found.size());
        assertEquals(tile, found.getFirst());
    }

    @Test
    public void testTilesAtPositionWithMultipleTilesAtSamePosition() {
        Tile tile1 = new Dirt(0, 0);
        Tile tile2 = new Grass(0, 0); // Same position

        world.place(tile1);
        world.place(tile2);

        List<Tile> found = world.tilesAtPosition(10, 10, dimensions);

        assertEquals(2, found.size());
        assertTrue(found.contains(tile1));
        assertTrue(found.contains(tile2));
    }

    @Test
    public void testTilesAtPositionReturnsEmptyForEmptyCell() {
        List<Tile> found = world.tilesAtPosition(100, 100, dimensions);
        assertEquals(0, found.size());
    }

    @Test
    public void testTilesAtPositionDifferentPlacesOnGrid() {
        int tileSize = dimensions.tileSize();
        Tile tile1 = new Dirt(0, 0);
        Tile tile2 = new Grass(tileSize, 0);

        world.place(tile1);
        world.place(tile2);

        // Check first tile
        List<Tile> firstTile = world.tilesAtPosition(10, 10, dimensions);
        assertEquals(1, firstTile.size());
        assertEquals(tile1, firstTile.getFirst());

        // Check second tile
        List<Tile> secondTile = world.tilesAtPosition(tileSize + 10, 10, dimensions);
        assertEquals(1, secondTile.size());
        assertEquals(tile2, secondTile.getFirst());
    }

    @Test
    public void testAllTilesReturnsNewList() {
        Tile tile = new Dirt(0, 0);
        world.place(tile);

        List<Tile> tiles1 = world.allTiles();
        List<Tile> tiles2 = world.allTiles();

        // Should be different list instances
        assertNotSame(tiles1, tiles2);
        // But same content
        assertEquals(tiles1, tiles2);
    }

    @Test
    public void testTileSelectorWithMatchingPredicate() {
        Tile dirt1 = new Dirt(0, 0);
        Tile dirt2 = new Dirt(80, 80);
        Tile grass = new Grass(160, 160);

        world.place(dirt1);
        world.place(dirt2);
        world.place(grass);

        List<Tile> dirtTiles = world.tileSelector(tile -> tile instanceof Dirt);

        assertEquals(2, dirtTiles.size());
        assertTrue(dirtTiles.contains(dirt1));
        assertTrue(dirtTiles.contains(dirt2));
    }

    @Test
    public void testTileSelectorWithNoMatches() {
        Tile grass = new Grass(0, 0);
        world.place(grass);

        List<Tile> waterTiles = world.tileSelector(tile -> tile instanceof Water);

        assertEquals(0, waterTiles.size());
    }

    @Test
    public void testTickUpdatesAllTiles() {
        Dirt dirt = new Dirt(0, 0);
        Cabbage cabbage = new Cabbage(0, 0);
        dirt.placeOn(cabbage);
        world.place(dirt);

        world.tick(engineState, gameState);

        assertNotNull(world.allTiles());
    }

    @Test
    public void testRenderReturnsAllTilesAndStackedEntities() {
        Dirt dirt = new Dirt(0, 0);
        Grass grass = new Grass(80, 80);

        world.place(dirt);
        world.place(grass);

        List<Renderable> renderables = world.render();

        // Size should be at least 2, both dirt and grass have at
        // least one renderable
        assertTrue(renderables.size() >= 2);
    }

    @Test
    public void testRenderWithStackedEntities() {
        Dirt dirt = new Dirt(0, 0);
        Cabbage cabbage = new Cabbage(0, 0);
        dirt.placeOn(cabbage);
        world.place(dirt);

        List<Renderable> renderables = world.render();

        // Should include tile AND cabbage renderables, therefore
        // at least 2
        assertTrue(renderables.size() >= 2);
    }

    @Test
    public void testRenderOrderTileBeforeStackedEntities() {
        Dirt dirt = new Dirt(0, 0);
        Cabbage cabbage = new Cabbage(0, 0);
        dirt.placeOn(cabbage);
        world.place(dirt);

        List<Renderable> renderables = world.render();

        // Tile should appear before its stacked entities
        int dirtIndex = renderables.indexOf(dirt);
        int cabbageIndex = renderables.indexOf(cabbage);

        assertTrue(dirtIndex >= 0);
        assertTrue(cabbageIndex > dirtIndex);
    }

    @Test
    public void testPlacingTilesDoesNotAffectExistingList() {
        Tile tile1 = new Dirt(0, 0);
        world.place(tile1);

        List<Tile> originalList = world.allTiles();

        Tile tile2 = new Grass(80, 80);
        world.place(tile2);

        // Original list should not be affected
        assertEquals(1, originalList.size());
        assertEquals(2, world.allTiles().size());
    }
}