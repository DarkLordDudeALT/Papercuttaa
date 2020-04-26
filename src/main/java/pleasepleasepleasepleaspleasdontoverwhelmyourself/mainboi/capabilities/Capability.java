package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities;

import org.bukkit.entity.Entity;

/**
 * The base class for capabilities.
 * Extend this class to get the necessary functions for capabilities.
 */
public abstract class Capability {
    /**
     * The name of the capability.
     * Used to store capabilities in the form of tags on entities.
     *
     * @return The name of the capability.
     */
    public abstract String getCapabilityName();



    /**
     * Runs a capability, allowing it to apply its effects.
     *
     * @param entity The entity to apply the capability's effects to.
     */
    public void runCapability(Entity entity) {}

    /**
     * Runs a capability upon its assignment to an entity.
     *
     * @param entity The entity the capability was assigned to.
     */
    public void onAssignment(Entity entity) {}

    /**
     * Runs a capability upon it being revoked from an entity.
     *
     * @param entity The entity the capability was revoked from.
     */
    public void onRevoke(Entity entity) {}
}
