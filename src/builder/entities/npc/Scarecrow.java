package builder.entities.npc;

import builder.GameState;
import builder.entities.npc.enemies.Enemy;
import builder.entities.npc.enemies.EnemyManager;
import builder.entities.npc.enemies.Magpie;
import builder.entities.npc.enemies.Pigeon;
import builder.ui.SpriteGallery;

import engine.EngineState;
import engine.art.sprites.SpriteGroup;

import java.util.ArrayList;

/**
 * A Scarecrow that can scare away birds within a certain radius.
 */
public class Scarecrow extends Npc {

    public static final int COIN_COST = 2;
    private static final SpriteGroup art = SpriteGallery.scarecrow;
    private static final int SCARE_RADIUS_IN_TILES = 4;

    /**
     * Create a new scarecrow at the given coordinates
     *
     * @param x horizontal position
     * @param y vertical position
     */
    public Scarecrow(int x, int y) {
        super(x, y);
        this.setSprite(art.getSprite("default"));
        this.setSpeed(0);
    }


    /**
     * Interact with the scarecrow to scare away nearby birds
     */
    @Override
    public void interact(EngineState state, GameState game) {
        super.interact(state, game);
        EnemyManager enemies = game.getEnemies();
        final ArrayList<Magpie> magpies = new ArrayList<>();
        final ArrayList<Pigeon> pigeons = new ArrayList<>();
        for (Enemy bird : enemies.getBirds()) {
            if (bird instanceof Magpie) {
                magpies.add((Magpie) bird);
            }
            if (bird instanceof Pigeon) {
                pigeons.add((Pigeon) bird);

            }
        }

        final int scareRadius = state.getDimensions().tileSize()
                * SCARE_RADIUS_IN_TILES;

        for (Magpie magpie : magpies) {
            if (this.distanceFrom(magpie) < scareRadius) {
                magpie.setAttacking(false);
                // trigger the scare animation
            }
        }

        for (Pigeon pigeon : pigeons) {
            if (this.distanceFrom(pigeon) < scareRadius) {
                pigeon.setAttacking(false);
                // trigger the scare animation
            }
        }
    }
}
