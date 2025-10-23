package builder.world;
import builder.entities.tiles.Dirt;
import builder.entities.tiles.Grass;
import builder.entities.tiles.Tile;
import builder.entities.tiles.Water;
import engine.renderer.Dimensions;
import engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class WorldBuilderTest {
    private Dimensions dimensions;
    private int numberOfTiles;

    @Before
    public void setUp() {
        dimensions = new TileGrid(10, 800);
        numberOfTiles = dimensions.windowSize() / dimensions.tileSize();
    }

    @Test
    public void testEmptyCreatesEmptyWorld() {
        BeanWorld world = WorldBuilder.empty();

        assertNotNull(world);
        assertEquals(0, world.allTiles().size());
    }

    @Test
    public void testFromTilesCreatesWorldWithTiles() {
        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Dirt(0, 0));
        tiles.add(new Grass(120, 120));
        tiles.add(new Water(220, 220));

        BeanWorld world = WorldBuilder.fromTiles(tiles);

        assertEquals(3, world.allTiles().size());
    }

    @Test
    public void testFromStringValidSimpleWorld() throws WorldLoadException {
        // Create a valid 10x10 world string (all water)
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numberOfTiles; i++) {
            for (int j = 0; j < numberOfTiles; j++) {
                builder.append('w');
            }
            if (i < numberOfTiles - 1) {
                builder.append('\n');
            }
        }

        List<Tile> tiles = WorldBuilder.fromString(dimensions, builder.toString());

        assertEquals(numberOfTiles * numberOfTiles, tiles.size());
        assertTrue(tiles.getFirst() instanceof Water);
    }

    @Test
    public void testFromStringMixedTiles() throws WorldLoadException {
        // Create a 10x10 world with different tile types
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numberOfTiles; i++) {
            for (int j = 0; j < numberOfTiles; j++) {
                // Alternate between water, dirt, and grass
                if (j % 3 == 0) builder.append('w');
                else if (j % 3 == 1) builder.append('d');
                else builder.append('g');
            }
            if (i < numberOfTiles - 1) {
                builder.append('\n');
            }
        }

        List<Tile> tiles = WorldBuilder.fromString(dimensions, builder.toString());

        assertEquals(numberOfTiles * numberOfTiles, tiles.size());
    }

    @Test(expected = WorldLoadException.class)
    public void testFromStringTooFewLines() throws WorldLoadException {
        // Only provide half the required lines
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numberOfTiles / 2; i++) {
            for (int j = 0; j < numberOfTiles; j++) {
                builder.append('w');
            }
            if (i < (numberOfTiles / 2) - 1) {
                builder.append('\n');
            }
        }

        WorldBuilder.fromString(dimensions, builder.toString());
    }



    @Test
    public void testFromTilesEmptyList() {
        BeanWorld world = WorldBuilder.fromTiles(List.of());

        assertEquals(0, world.allTiles().size());
    }

    @Test
    public void testFromTilesSingleTile() {
        Tile tile = new Dirt(0, 0);
        BeanWorld world = WorldBuilder.fromTiles(List.of(tile));

        assertEquals(1, world.allTiles().size());
        assertTrue(world.allTiles().contains(tile));
    }
}