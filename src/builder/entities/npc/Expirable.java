package builder.entities.npc;

import engine.timing.FixedTimer;

/**
 * Indicates the entity or other object that is implementing is set
 * to expire over a specific set of
 * time.
 */
public interface Expirable {
    void setLifespan(FixedTimer lifespan);

    FixedTimer getLifespan();
}
