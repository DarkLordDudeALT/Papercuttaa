package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.programmablegolems;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

// TODO Add option to run all golem code in separate thread. All calls to change the game will need to synchronize.

public final class ProgrammableGolemHandler implements Listener {
    // Storage of programmable golems in plugin memory for handling.
    private static final HashMap<ArmorStand, ProgrammableGolemInstance> GOLEM_QUEUE = new HashMap<>();

    public static void onEnable() {

    }



    /**
     * Spawns a programmable golem at the location, and returns its instance.
     *
     * @param location The location to spawn the golem at.
     *
     * @return The instance of the golem.
     */
    public static ProgrammableGolemInstance spawnProgrammableGolem(Location location) {
        ArmorStand golem = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        golem.setCustomName("BotBoi #" + golem.getUniqueId());
        golem.setSmall(true);
        golem.setArms(true);
        golem.setBasePlate(false);
        golem.setDisabledSlots(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
        makeProgrammable(golem);

        return GOLEM_QUEUE.get(golem);
    }

    /**
     * Attempts to create a programmable golem instance for the given armor stand.
     *
     * @param armorStand The armor stand to turn into an active golem.
     *
     * @return Whether or not the armor stand was a golem to begin with.
     */
    public static boolean makeProgrammable(ArmorStand armorStand) {
        if (!GOLEM_QUEUE.containsKey(armorStand)) {
            if (armorStand.getScoreboardTags().contains("programmableGolem")) {
                GOLEM_QUEUE.put(armorStand, new ProgrammableGolemInstance(armorStand));

            } else {
                GOLEM_QUEUE.put(armorStand, new ProgrammableGolemInstance(armorStand));
                armorStand.addScoreboardTag("programmableGolem");
            }

            return true;
        }

        return false;
    }


    /**
     * Attempts to revert the given golem back into an armor stand.
     *
     * @param golem The golem to revert.
     *
     * @return Whether or not the armor stand was an active golem.
     */
    public static boolean makeNotProgrammable(ArmorStand golem) {
        if (GOLEM_QUEUE.containsKey(golem)) {
            GOLEM_QUEUE.remove(golem);
            golem.removeScoreboardTag("programmableGolem");

            return true;
        }

        return false;
    }



    //////////////////////////////////////////////////////////////////////////////////////////////
    // The following 2 event handlers control the loading and unloading of programmable golems. //
    //////////////////////////////////////////////////////////////////////////////////////////////
    @EventHandler
    public static void onEntityLoad(EntityAddToWorldEvent entityAddToWorldEvent) {
        Entity possibleGolem = entityAddToWorldEvent.getEntity();

        if (possibleGolem instanceof ArmorStand && possibleGolem.getScoreboardTags().contains("programmableGolem"))
            makeProgrammable((ArmorStand) possibleGolem);
    }

    @EventHandler
    public static void onEntityUnload(EntityRemoveFromWorldEvent entityRemoveFromWorldEvent) {
        Entity possibleGolem = entityRemoveFromWorldEvent.getEntity();

        if (possibleGolem instanceof ArmorStand)
            GOLEM_QUEUE.remove(possibleGolem);
    }



    /**
     * Called when a possible golem attempts to tick its instance.
     *
     * @param golem The possible golem.
     */
    public static void tickGolem(ArmorStand golem) {
        ProgrammableGolemInstance programmableGolem = GOLEM_QUEUE.get(golem);

        if (programmableGolem != null)
            programmableGolem.tick();
    }
}
