package builder.entities.npc.enemies;

import builder.GameState;
import builder.Tickable;
import builder.entities.Interactable;
import builder.entities.npc.spawners.Spawner;
import builder.player.Player;
import builder.ui.RenderableGroup;

import engine.EngineState;
import engine.game.HasPosition;
import engine.renderer.Dimensions;
import engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all enemies in the game, including updating, rendering,
 * and interactions.
 */
public class EnemyManager implements Tickable, Interactable,
        RenderableGroup {

    private final ArrayList<Spawner> spawners = new ArrayList<>();
    private final ArrayList<Enemy> birds = new ArrayList<>();


    private int spawnX;

    private int spawnY;

    public EnemyManager(Dimensions dimensions) {
    }

    /**
     * Cleans up any birds that are marked for removal.
     */
    public void cleanup() {
        for (int i = this.birds.size() - 1; i >= 0; i -= 1) {
            if (this.birds.get(i).isMarkedForRemoval()) {
                this.birds.remove(i);
            }
        }
    }

    /**
     * @return the list of spawners managed by this manager.
     */
    public ArrayList<Spawner> getSpawners() {
        return spawners;
    }

    /**
     * @return the list of birds managed by this manager.
     */
    public ArrayList<Enemy> getBirds() {
        return birds;
    }

    /**
     * @return the spawn Y coordinate.
     */
    public int getSpawnY() {
        return spawnY;
    }

    /**
     * Sets the spawn Y coordinate.
     *
     * @param spawnY The spawn Y coordinate to set.
     */
    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    /**
     * @return the spawn X coordinate.
     */
    public int getSpawnX() {
        return spawnX;
    }

    /**
     * Sets the spawn X coordinate.
     *
     * @param spawnX The spawn X coordinate to set.
     */
    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    /**
     * Adds a spawner to the list of spawners.
     *
     * @param spawner
     */
    public void add(Spawner spawner) {
        this.spawners.add(spawner);
    }

    /**
     * Adds a new Magpie targeting the given player to the list of
     * birds.
     *
     * @param player The player to target.
     * @return The created Magpie.
     */
    public Magpie makeMagpie(Player player) {
        final Magpie magpie =
                new Magpie(this.spawnX, this.spawnY, player);
        this.birds.add(magpie);
        return magpie;
    }

    /**
     * Adds a new Pigeon targeting the given HasPosition to the
     * list of birds.
     *
     * @param hasPosition The HasPosition to target.
     * @return The created Pigeon.
     */
    public Pigeon makePigeon(HasPosition hasPosition) {
        final Pigeon pigeon =
                new Pigeon(this.spawnX, this.spawnY, hasPosition);
        this.birds.add(pigeon);
        return pigeon;
    }

    /**
     * Returns a new Eagle targeting the given player.
     * Eagles are immune to scarecrows, hence they are not added to
     * the Birds list here.
     *
     * @param player The player to target.
     * @return The created Eagle.
     */
    public Eagle makeEagle(Player player) {
        return new Eagle(this.spawnX, this.spawnY, player);
    }

    public void spawnEagle(int x, int y, Player player) {
        this.spawnX = x;
        this.spawnY = y;
        Eagle eagle = makeEagle(player);
        this.birds.add(eagle);
    }


    /**
     * @param state The state of the engine, including the mouse,
     *              keyboard information and
     *              dimension. Useful for processing keyboard
     *              presses or mouse movement.
     * @param game  The state of the game, including the player and
     *              world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        this.cleanup();
        for (Spawner spawner : this.spawners) {
            spawner.tick(state, game);
        }
        for (Enemy bird : birds) {
            bird.tick(state, game);
        }
    }

    /**
     * Get all Magpies' positions from the enemy manager.
     *
     * @return all {@link Magpie}s positions from the enemy manager.
     */
    public ArrayList<Magpie> getMagpies() {
        final ArrayList<Magpie> magpies = new ArrayList<Magpie>();
        for (Enemy bird : birds) {
            if (bird instanceof Magpie temp) {
                magpies.add(temp);
            }
        }
        return magpies;
    }

    public ArrayList<Enemy> getAll() {
        return this.birds;
    }

    /**
     * @param state The state of the engine, including the mouse,
     *              keyboard information and
     *              dimension. Useful for processing keyboard
     *              presses or mouse movement.
     * @param game  The state of the game, including the player and
     *              world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void interact(EngineState state, GameState game) {
        return;
    }

    /**
     * A collection of birds to render.
     *
     * @return The list of bird renderables.
     */
    @Override
    public List<Renderable> render() {
        return new ArrayList<>(this.birds);
    }
}
