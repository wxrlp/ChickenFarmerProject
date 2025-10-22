package builder.entities.npc.spawners;

import builder.GameState;
import builder.entities.npc.BeeHive;

import builder.entities.npc.NpcManager;
import builder.player.Player;
import engine.EngineState;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

/**
 * Spawner for BeeHive bees
 */
public
class BeeHiveSpawner implements Spawner {

    private final RepeatingTimer timer;
    private int x;
    private int y;

    /**
     * Creates a new BeeHive spawner at the given coordinates
     *
     * @param x        The x coordinate of the spawner
     * @param y        The y coordinate of the spawner
     * @param duration The duration between spawns
     */
    public
    BeeHiveSpawner(int x, int y, int duration) {
        this.x = x;
        this.y = y;
        this.timer = new RepeatingTimer(duration);
    }

    /**
     * Returns the timer for this spawner
     */
    @Override
    public
    TickTimer getTimer() {
        return this.timer;
    }

    /**
     * Tick method for the spawner
     *
     * @param state The current engine state
     * @param game  The current game state
     */
    @Override
    public
    void tick(EngineState state, GameState game) {
        Player player = game.getPlayer();
        NpcManager npcs = game.getNpcs();
        final boolean canAfford =
                game.getInventory().getFood() >= BeeHive.FOOD_COST
                        && game.getInventory().getCoins() >=
                        BeeHive.COIN_COST;

        if (canAfford && state.getKeys().isDown('h')) {
            game.getInventory().addFood(BeeHive.FOOD_COST);
            game.getInventory().addCoins(BeeHive.COIN_COST);
            npcs.addNpc(new BeeHive(player.getX(), player.getY()));
        }
        // look at use code example to spawn based on user input
        // and only on grass tiles
    }

    /**
     * returns the x coordinate of the spawner
     */
    @Override
    public
    int getX() {
        return this.x;
    }

    /**
     * sets the x coordinate of the spawner
     */
    @Override
    public
    void setX(int x) {
        this.x = x;
    }

    /**
     * returns the y coordinate of the spawner
     */
    @Override
    public
    int getY() {
        return this.y;
    }

    /**
     * sets the y coordinate of the spawner
     */
    @Override
    public
    void setY(int y) {
        this.y = y;
    }
}
