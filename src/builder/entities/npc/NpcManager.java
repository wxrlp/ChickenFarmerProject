package builder.entities.npc;

import builder.GameState;
import builder.Tickable;
import builder.entities.Interactable;
import builder.ui.RenderableGroup;

import engine.EngineState;
import engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages all NPCs in the game, including updating, rendering, and
 * interactions.
 */
public class NpcManager implements Interactable, Tickable,
        RenderableGroup {
    private final ArrayList<Npc> npcs = new ArrayList<>();

    /**
     * Constructs an instance of {@link NpcManager}.
     */
    public NpcManager() {
    }

    /**
     * Cleans up any NPCs that are marked for removal.
     */
    public void cleanup() {
        for (int i = this.getNpcs().size() - 1; i >= 0; i -= 1) {
            if (this.npcs.get(i).isMarkedForRemoval()) {
                this.npcs.remove(i);
            }
        }
    }

    /** Gets an unmodifiable list of NPCs managed by this
     * @return an unmodifiable list of NPCs managed by this manager.
     */
    public List<Npc> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

    /** Adds an NPC to be managed by this manager.
     * @param npc npc to add to the manager for it to well
     *            manage/track.
     */
    public void addNpc(Npc npc) {
        this.npcs.add(npc);
    }

    /**
     * Updates all NPCs managed by this manager.
     *
     * @param state The current state of the engine.
     * @param game  The current state of the game.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        this.cleanup();
        for (Npc npc : npcs) {
            npc.tick(state, game);
        }
    }

    /**
     * Handles interactions for all NPCs managed by this manager.
     *
     * @param state The current state of the engine.
     * @param game  The current state of the game.
     */
    @Override
    public void interact(EngineState state, GameState game) {
        for (Interactable interactable : this.getInteractables()) {
            interactable.interact(state, game);
        }
    }

    /**
     * Returns an ArrayList of interactables
     *
     * @return an ArrayList of interactables
     */
    private ArrayList<Interactable> getInteractables() {
        final ArrayList<Interactable> interactables =
                new ArrayList<>();
        for (Npc npc : npcs) {
            if (npc != null) {
                interactables.add(npc);
            }
        }
        return interactables;
    }

    /**
     * A collection of NPCs to render.
     *
     * @return The list of NPC renderables.
     */
    @Override
    public List<Renderable> render() {
        return new ArrayList<>(this.npcs);
    }
}
