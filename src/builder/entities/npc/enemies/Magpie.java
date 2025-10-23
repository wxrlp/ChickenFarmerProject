package builder.entities.npc.enemies;

import builder.GameState;
import builder.entities.npc.Expirable;
import builder.helpers.TargetPlayerHelper;
import builder.player.Player;
import builder.ui.SpriteGallery;

import engine.EngineState;
import engine.art.sprites.SpriteGroup;
import engine.game.HasPosition;
import engine.timing.FixedTimer;
import engine.timing.RepeatingTimer;

/**
 * Represents a Magpie enemy that tracks a target, attacks to steal
 * coins, and returns to its spawn
 * point before expiring. When the magpie reaches the player, it will
 * steal 1 coin from the player and return to spawn.
 */
public class Magpie extends Enemy implements Expirable {

    private static final SpriteGroup art = SpriteGallery.magpie;
    private FixedTimer lifespan = new FixedTimer(10000);
    private HasPosition trackedTarget;
    private boolean attacking;
    private int coins = 0;


    private RepeatingTimer directionalUpdateTimer =
            new RepeatingTimer(30);

    private final int spawnX;
    private final int spawnY;

    /**
     * Constructs a Magpie enemy at the specified coordinates,
     * tracking the given target.
     *
     * @param x             The x-coordinate of the magpie's spawn
     *                      position.
     * @param y             The y-coordinate of the magpie's spawn
     *                      position.
     * @param trackedTarget The target that the magpie will track
     *                      and attack.
     */
    public Magpie(int x, int y, HasPosition trackedTarget) {
        super(x, y);
        this.spawnX = x;
        this.spawnY = y;
        this.trackedTarget = trackedTarget;
        this.setSprite(art.getSprite("down"));
        this.attacking = true;

        double deltaX = trackedTarget.getX() - this.getX();
        double deltaY = trackedTarget.getY() - this.getY();
        this.setDirection(
                (int) Math.toDegrees(Math.atan2(deltaY, deltaX)));
    }

    /** Sets the attacking status of the Magpie.
     *
     * @param attackStatus True if the Magpie is attacking, false otherwise.
     */
    public void setAttacking(boolean attackStatus) {
        this.attacking = attackStatus;
    }

    /**
     * Sets the tracked target of the Magpie.
     *
     * @param trackedTarget The HasPosition to set as the Magpie's tracked target.
     */
    public void setTrackedTargetTrackedTarget(HasPosition trackedTarget) {
        this.trackedTarget = trackedTarget;
    }

    /**
     * Gets the tracked target of the Magpie.
     *
     * @return The HasPosition representing the Magpie's tracked target.
     */
    public HasPosition getTrackedTargetTrackedTarget() {
        return this.trackedTarget;
    }

    /**
     * Gets the number of coins the Magpie has stolen.
     *
     * @return The number of coins the Magpie has.
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Sets the number of coins the Magpie has stolen.
     *
     * @param coins The number of coins to set for the Magpie.
     */
    public void setCoins(int coins) {
        this.coins = coins;
    }




    @Override
    public FixedTimer getLifespan() {
        return lifespan;
    }

    /**
     * Sets the lifespan of the Magpie.
     *
     * @param timer The FixedTimer to set as the Magpie's lifespan.
     */
    @Override
    public void setLifespan(FixedTimer timer) {
        this.lifespan = timer;
    }

    /**
     * Updates the Magpie's state on each tick of the game engine.
     *
     * @param engine The current state of the game engine.
     * @param game   The current state of the game.
     */
    @Override
    public void tick(EngineState engine, GameState game) {
        super.tick(engine, game);
        this.lifespan.tick();
        final int tileSize = engine.getDimensions().tileSize();
        if (this.lifespan.isFinished()) {
            this.markForRemoval();
        }

        if (attacking) {
            TargetPlayerHelper.setDirectionAndSpriteTowards(
                    trackedTarget.getX(), trackedTarget.getY(),
                    attacking, this, art);
        } else {
            TargetPlayerHelper.setDirectionAndSpriteTowards(
                    this.spawnX, this.spawnY, attacking, this, art);
        }

        this.move();
        this.directionalUpdateTimer.tick();
        Player player = game.getPlayer();
        final boolean hasHitPlayer =
                this.distanceFrom(player.getX(), player.getY())
                        < tileSize;
        // Take a coin from player and run back to spawn
        if (hasHitPlayer && game.getInventory().getCoins() > 0
                && this.attacking) {
            game.getInventory().addCoins(-1);
            this.coins += 1;
            this.attacking = false;
            this.setSpeed(2); // Increases speed when escaping!
        }

        // Despawns when arriving back at spawn
        if (!attacking) {
            if (this.distanceFrom(spawnX, spawnY) < tileSize) {
                this.markForRemoval();
            }
        }

        // Give coins to player upon removal if still attacking
        if (this.isMarkedForRemoval() && attacking) {
            game.getInventory().addCoins(this.coins);
        }
    }


}
