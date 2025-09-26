package builder.entities.npc;

import builder.GameState;
import builder.Tickable;
import builder.entities.Interactable;
import builder.ui.RenderableGroup;

import engine.EngineState;
import engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

public class NpcManager implements Interactable, Tickable, RenderableGroup {
    public final ArrayList<Npc> npcs = new ArrayList<>();

    public NpcManager() {}

    public void cleanup() {
        for (int i = this.npcs.size() - 1; i >= 0; i -= 1) {
            if (this.npcs.get(i).isMarkedForRemoval()) {
                this.npcs.remove(i);
            }
        }
    }

    /**
     * @param npc npc to add to the manager for it to well manage/track.
     */
    public void addNpc(Npc npc) {
        this.npcs.add(npc);
    }

    @Override
    public void tick(EngineState state, GameState game) {
        this.cleanup();
        for (Npc npc : npcs) {
            npc.tick(state, game);
        }
    }

    @Override
    public void interact(EngineState state, GameState game) {
        for (Interactable interactable : this.getInteractables()) {
            interactable.interact(state, game);
        }
    }

    /**
     * Returns an ArrayList<Interactable> of interactable
     *
     * @return an ArrayList<Interactable> of interactable
     */
    private ArrayList<Interactable> getInteractables() {
        final ArrayList<Interactable> interactables = new ArrayList<>();
        for (Npc npc : npcs) {
            if (npc instanceof Interactable) {
                interactables.add(npc);
            }
        }
        return interactables;
    }

    @Override
    public List<Renderable> render() {
        return new ArrayList<>(this.npcs);
    }
}
