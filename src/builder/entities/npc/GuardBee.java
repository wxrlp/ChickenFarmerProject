package builder.entities.npc;

import builder.GameState;
import builder.entities.npc.enemies.Enemy;
import builder.ui.SpriteGallery;

import engine.EngineState;
import engine.art.sprites.SpriteGroup;
import engine.game.HasPosition;
import engine.timing.FixedTimer;

/**
 * A highly trained Guard Bee... don't think about that too much. This is our projectile class,
 * basically a bullet.
 */
public class GuardBee extends Npc implements Expirable {

    private final int spawnX;
    private final int spawnY;
    private static final int SPEED = 2;
    private static final SpriteGroup art = SpriteGallery.bee;
    private FixedTimer lifespan = new FixedTimer(300);
    private final HasPosition trackedTarget;
    private static final int UP_MIN = 230;
    private static final int UP_MAX = 310;
    private static final int DOWN_MIN = 40;
    private static final int DOWN_MAX = 140;
    private static final int RIGHT_MIN = 310;
    private static final int RIGHT_MAX = 40;  // Wraps around 0


    /**
     * @param xCoordinate horizontal spawning position
     * @param yCoordinate vertical spawning position
     * @param trackedTarget target with a position we want this to track
     */
    public GuardBee(int xCoordinate, int yCoordinate, HasPosition trackedTarget) {
        super(xCoordinate, yCoordinate);
        this.setSprite(art.getSprite("default"));
        this.trackedTarget = trackedTarget;

        this.spawnX = xCoordinate;
        this.spawnY = yCoordinate;

        double deltaX = trackedTarget.getX() - this.getX();
        double deltaY = trackedTarget.getY() - this.getY();
        this.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));
        this.setSpeed(GuardBee.SPEED);
    }

    /** Return the lifespan timer of this GuardBee */
    @Override
    public FixedTimer getLifespan() {
        return lifespan;
    }

    /** Set the lifespan timer of this GuardBee */
    @Override
    public void setLifespan(FixedTimer timer) {
        this.lifespan = timer;
    }

    private enum CardinalDirection {
        UP, DOWN, LEFT, RIGHT
    }


    /** Update the sprite of the GuardBee based on its current direction */
    public void updateArtBasedOnDirection() {
        int dir = this.getDirection();
        if (dir >= DOWN_MIN && dir < DOWN_MAX) {
            this.setSprite(art.getSprite("down"));
        } else if (dir >= UP_MIN && dir < UP_MAX) {
            this.setSprite(art.getSprite("up"));
        } else if (dir >= RIGHT_MIN || dir < RIGHT_MAX) {
            this.setSprite(art.getSprite("right"));
        } else {
            this.setSprite(art.getSprite("left"));
        }
    }

    /** Progress the state of the GuardBee, updating how it is rendered as required. */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state);
        this.move();

        if (this.trackedTarget == null) {

            double deltaX = this.spawnX - this.getX();
            double deltaY = this.spawnY - this.getY() ;
            this.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));
            return;
        }
        for (Enemy enemy : game.getEnemies().Birds) {
            if (this.distanceFrom(enemy)
                    < 300) { // if a magpie is close enough to a bee it will lock onto it // TODO
                double deltaX = this.trackedTarget.getX() - this.getX();
                double deltaY = this.trackedTarget.getY() - this.getY();
                this.setDirection((int) Math.toDegrees(Math.atan2(deltaY, deltaX)));
                break;
            }
        }

        if (game.getEnemies().getAll().isEmpty()){
            this.markForRemoval();
        }
        for (Enemy enemy : game.getEnemies().getAll()) {
            if (this.distanceFrom(enemy) < state.getDimensions().tileSize()) {
                enemy.markForRemoval();
                this.markForRemoval();
            }
        }

        this.updateArtBasedOnDirection();
        lifespan.tick();
        if (lifespan.isFinished() && (this.spawnX == this.getX() && this.spawnY == this.getY())) {
            this.markForRemoval();
        }
    }
}
