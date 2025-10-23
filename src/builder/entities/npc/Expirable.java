package builder.entities.npc;

import engine.timing.FixedTimer;

/**
 * Indicates the entity or other object that is implementing is set
 * to expire over a specific set of
 * time.
 */
public interface Expirable {
    /**
     * Sets the lifespan timer for this expirable entity.
     *
     * @param lifespan The FixedTimer representing the lifespan duration.
     */
    void setLifespan(FixedTimer lifespan);

    /**
     * Gets the lifespan timer for this expirable entity.
     *
     * @return The FixedTimer representing the lifespan duration.
     */
    FixedTimer getLifespan();
}
