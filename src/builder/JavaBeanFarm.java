package builder;

import builder.entities.npc.NpcManager;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.npc.spawners.EagleSpawner;
import builder.entities.npc.spawners.MagpieSpawner;
import builder.entities.npc.spawners.PigeonSpawner;
import builder.entities.npc.spawners.Spawner;
import builder.entities.tiles.Dirt;
import builder.entities.tiles.Tile;
import builder.inventory.*;
import builder.inventory.items.*;
import builder.inventory.ui.InventoryOverlay;
import builder.inventory.ui.ResourceOverlay;
import builder.player.PlayerManager;
import builder.ui.Overlay;
import builder.world.BeanWorld;
import builder.world.CabbageDetails;
import builder.world.OverlayBuilder;
import builder.world.PlayerDetails;
import builder.world.SpawnerDetails;
import builder.world.WorldBuilder;
import builder.world.WorldLoadException;

import engine.EngineState;
import engine.game.Game;
import engine.renderer.Dimensions;
import engine.renderer.Renderable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * JavaBeans, a farming game.
 *
 * <p>In this game, the player collects coins by mining ores. The
 * player, a chicken farmer, may use
 * those coins to plant cabbages on tilled dirt.
 *
 * <p>This class managers the world instance, player manager, and
 * the inventory instance. The
 * inventory and resource overlays should also be managed by this
 * class.
 *
 * <p>In stage 0, this class will store the Brutus character to
 * wander around.
 *
 * @stage1part This class manages the player manager.
 * @stage2part This class manages the world instance.
 */
public class JavaBeanFarm implements Game {

    private final PlayerManager playerManager;
    private static final int INVENTORY_SIZE = 5;
    private final NpcManager npcs;
    private final EnemyManager enemies;
    private static final int CABBAGE_INVENTORY_SIZE = 5;
    private static final int CABBAGE_INVENTORY_COINS = 100;
    private static final int CABBAGE_INVENTORY_FOOD = 100;
    private final BeanWorld world;

    private final Inventory inventory;
    private final List<Overlay> overlays = new ArrayList<>();


    /** * Reads all content from a reader and returns it as a single
     * string.
     * @param reader The reader to read from.
     * @return The content read from the reader as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String readAllReader(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        StringJoiner sb = new StringJoiner(System.lineSeparator());
        String line;
        while ((line = br.readLine()) != null) {
            sb.add(line);
        }
        return sb.toString();
    }


    /**
     * Constructs a new JavaBean Farm game using the given
     * dimensions, mapPath and detailPath
     *
     * @param dimensions   The dimensions we want for this game.
     * @param mapReader    A reader the contains a description of
     *                     the world map.
     * @param detailReader A reader the contains the overlay
     *                     details for the game, e.g. spawner
     *                     locations.
     * @throws IOException        If the game is unable to find or
     *                            open the default world map file.
     * @throws WorldLoadException If the default world map file
     *                            cannot be parsed successfully.
     */
    public JavaBeanFarm(Dimensions dimensions, Reader mapReader,
                        Reader detailReader, BeanWorld world)
            throws IOException, WorldLoadException {
        this.world = world;
        final String detailsContent = readAllReader(detailReader);
        final String worldContent = readAllReader(mapReader);
        final PlayerDetails playerDetails =
                OverlayBuilder.getPlayerDetailsFromFile(
                        detailsContent);
        this.playerManager = new PlayerManager(
                playerDetails.getX(),
                playerDetails.getY());
        this.npcs = new NpcManager();
        this.enemies = new EnemyManager(dimensions);
        loadSpawners(
                OverlayBuilder.getMagpieSpawnDetailsFromString(
                        detailsContent),
                MagpieSpawner::new);
        loadSpawners(
                OverlayBuilder.getEagleSpawnDetailsFromString(
                        detailsContent),
                EagleSpawner::new);
        loadSpawners(
                OverlayBuilder.getPigeonSpawnDetailsFromString(
                        detailsContent),
                PigeonSpawner::new);

        initialiseCabbage(detailsContent, dimensions);
        this.inventory = createInventory(playerDetails);
        inventoryInitialiser(this.inventory);
        initializeOverlays(dimensions);

    }


    /**
     * Constructs a new JavaBean Farm game using the given
     * dimensions, mapPath and detailPath
     * @param dimensions The dimensions we want for this game.
     * @param mapFile The path to a file that contains a
     *                description of the world map.
     * @param detailsFile The path to a file that contains the
     *                    overlay details for the game, e.g.
     *                    spawner locations.
     * @param world The world instance to use in the game.
     * @throws IOException     If the game is unable to find or
     *                       open the default world map file.
     * @throws WorldLoadException If the default world map file
     *                            cannot be parsed successfully.
     */
    public JavaBeanFarm(Dimensions dimensions, String mapFile,
                        String detailsFile, BeanWorld world)
            throws IOException, WorldLoadException {
        this(
                dimensions, new FileReader(mapFile),
                new FileReader(detailsFile), world);
    }

    /** * Initialises cabbages in the world based on the details
     * content.
     * @param detailsContent The details content to parse for
     *                       cabbage spawn points.
     * @param dimensions The dimensions of the world.
     * @throws IOException If an I/O error occurs.
     */
    private void initialiseCabbage(String detailsContent,
                                   Dimensions dimensions)
            throws IOException {
        List<CabbageDetails> cabbageSpawnPoints =
                OverlayBuilder.getCabbageSpawnDetailsFromString(
                        detailsContent);
        for (CabbageDetails cabbageDetails : cabbageSpawnPoints) {
            plantCabbageAt(
                    cabbageDetails.getX(),
                    cabbageDetails.getY(),
                    dimensions);
        }
    }

    /** * Plants a cabbage at the given pixel coordinates if
     * possible.
     * @param x The x coordinate in pixels.
     * @param y The y coordinate in pixels.
     * @param dimensions The dimensions of the world.
     */
    private void plantCabbageAt(int x, int y,
                                Dimensions dimensions) {
        List<Tile> tiles =
                this.world.tilesAtPosition(
                        x, y,
                        dimensions);
        for (Tile tile : tiles) {
            if (tile instanceof Dirt) {
                TinyInventory tempInventory =
                        new TinyInventory(
                                CABBAGE_INVENTORY_SIZE,
                                CABBAGE_INVENTORY_COINS,
                                CABBAGE_INVENTORY_FOOD);
                ((Dirt) tile).till();
                ((Dirt) tile).plant(tempInventory);
            }
        }
    }

    /** * Creates an inventory for the player based on the
     * player details.
     * @param playerDetails The player details to use.
     * @return The created inventory.
     */
    private Inventory createInventory(PlayerDetails playerDetails) {
        return new TinyInventory(
                INVENTORY_SIZE,
                playerDetails.getStartingCoins(),
                playerDetails.getStartingFood());
    }

    /**
     * A factory interface for creating spawners.
     */
    @FunctionalInterface
    private interface SpawnerFactory {
        Spawner create(int x, int y, int duration);
    }

    /**
     * Loads spawners from the given details using the provided
     * factory.
     */
    private void loadSpawners(
            List<SpawnerDetails> spawnerDetails,
            SpawnerFactory factory) {
        for (SpawnerDetails details : spawnerDetails) {
            this.enemies.add(factory.create(
                    details.getX(),
                    details.getY(),
                    details.getDuration()));
        }
    }

    /** * Initializes the overlays for the game.
     * @param dimensions The dimensions of the game.
     */
    private void initializeOverlays(Dimensions dimensions) {
        this.overlays.add(
                new InventoryOverlay(dimensions, INVENTORY_SIZE));
        this.overlays.add(new ResourceOverlay(dimensions));
    }

    /** * Initializes the inventory with default items.
     * @param inventory The inventory to initialize.
     */
    private void inventoryInitialiser(Inventory inventory) {
        Item[] defaultItems = {
                new Bucket(),
                new Hoe(),
                new Jackhammer(),
                new HiveHammer(),
                new Pole()
        };

        for (int i = 0; i < defaultItems.length; i++) {
            inventory.setItem(i, defaultItems[i]);
        }
    }

    /**
     * Ticks the internal game state forward by one frame. a
     *
     * @param state The state of the engine, including the mouse,
     *              keyboard information and
     *              dimension. Useful for processing keyboard
     *              presses or mouse movement.
     * @stage1part The player manager should be progressed via {@link
     * PlayerManager#tick(EngineState, GameState)}.
     * @stage2part The world should be progressed via
     * {@link BeanWorld#tick(EngineState,
     * GameState)}.
     */
    public void tick(EngineState state) {
        GameState game =
                new JavaBeanGameState(
                        world, playerManager.getPlayer(), inventory,
                        this.npcs, this.enemies);
        this.playerManager.tick(state, game);
        this.npcs.tick(state, game);
        this.enemies.tick(state, game);

        this.world.tick(state, game);

        for (Overlay overlay : overlays) {
            overlay.tick(state, game);
        }

        this.npcs.interact(state, game);
        this.enemies.interact(state, game);

        this.npcs.cleanup();
        this.enemies.cleanup();
    }

    /**
     * A collection of items to render, every component of the game
     * to be rendered should be
     * returned.
     *
     * @return The list of renderables required to draw the whole
     * game.
     * @stage2part Any renderables of the world (i.e.
     * {@link BeanWorld#render()}) must be rendered
     * behind everything else, i.e., first in the returned list.
     * @stage1part Any renderables of the player (i.e.
     * {@link PlayerManager#render()}) must be
     * rendered after the world but before overlays.
     * <p>Overlays, i.e., {@link ResourceOverlay} and
     * {@link InventoryOverlay} must be rendered
     * last in any order.
     */
    @Override
    public List<Renderable> render() {

        List<Renderable> renderables = new ArrayList<>();

        renderables.addAll(this.world.render());

        renderables.addAll(this.npcs.render());
        renderables.addAll(this.enemies.render());

        renderables.addAll(this.playerManager.render());

        for (Overlay overlay : overlays) {
            renderables.addAll(overlay.render());
        }

        return renderables;
    }
}
