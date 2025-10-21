package builder.entities.npc.spawners;

import builder.GameState;
import builder.entities.npc.NpcManager;
import builder.entities.npc.Scarecrow;

import builder.player.Player;
import engine.EngineState;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

/**
 * A scarecrow spawner allows the player to spawn scarecrows at their current location
 * by pressing the 'c' key, provided they have enough coins.
 */
public class ScarecrowSpawner implements Spawner {

    private int x;
    private int y;
    private final RepeatingTimer timer = new RepeatingTimer(300);

    /**
     * Creates a new Scarecrow spawner at the given coordinates
     *
     * @param x The x coordinate of the spawner
     * @param y The y coordinate of the spawner
     */
    public ScarecrowSpawner(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Returns the timer for this spawner */
    @Override
    public TickTimer getTimer() {
        return this.timer;
    }

    /**
     * Tick method for the spawner
     *
     * @param state The current engine state
     * @param game  The current game state
     */
    @Override
    public void tick(EngineState state, GameState game) {
        Player player = game.getPlayer();
        NpcManager npcs = game.getNpcs();
        // look at use code to spawn
        if (game.getInventory().getCoins() >= Scarecrow.COIN_COST
                && state.getKeys().isDown('c')) {
            game.getInventory().addCoins(Scarecrow.COIN_COST);
            npcs.addNpc(new Scarecrow(player.getX(), player.getY()));
        }
    }

    /** Returns the x coordinate of the spawner */
    @Override
    public int getX() {
        return this.x;
    }

    /** Sets the x coordinate of the spawner */
    @Override
    public void setX(int x) {
        this.x = x;
    }

    /** Returns the y coordinate of the spawner */
    @Override
    public int getY() {
        return this.y;
    }

    /** Sets the y coordinate of the spawner */
    @Override
    public void setY(int y) {
        this.y = y;
    }
}
