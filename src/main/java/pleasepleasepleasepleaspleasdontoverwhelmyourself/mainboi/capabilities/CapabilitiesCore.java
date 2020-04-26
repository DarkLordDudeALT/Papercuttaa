package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.CommandHelper;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;

import java.util.*;
import java.util.logging.Logger;

// TODO Add a collector that gets rid of unnecessary entities in the queue.
// TODO Have «/capabilities list (entity)» list the capabilities an entity has.
// TODO Have debugInterval be loaded to and from a file on plugin shutdown and startup.

// TODO Add a isVolatile() function that tells if the capability is lost on death.

// TODO Create a risk of rain elite capabilities.

/**
 * The code used to manage capabilities.
 *
 * Capabilities are persistent data structures for entities.
 *
 * A capability must extend Capability.java, the base class.
 * A capability then must be registered with registerCapability().
 * Then you are free to assign and revoke that capability to your heart's content.
 */
public final class CapabilitiesCore implements Listener, CommandExecutor, TabCompleter {
    // Stores entities with capabilities.
    private static final HashMap<Entity, Set<Capability>> ENTITY_CAPABILITY_QUEUE = new HashMap<>();
    // Stores registered capabilities, allowing easy String -> Capability conversion.
    private static final HashMap<String, Capability> CAPABILITIES_REGISTRY = new HashMap<>();

    /**
     * Registers capabilities to the capabilities system.
     * Capabilities must be registered to operate.
     */
    public static void registerCapability(Capability capability) throws DuplicateRegistryNameException {
        String capabilityName = capability.getCapabilityName();

        if (CAPABILITIES_REGISTRY.containsKey(capabilityName))
            throw new DuplicateRegistryNameException("Duplicate Capability Registry name: '" + capabilityName + "'.");

        CAPABILITIES_REGISTRY.put(capabilityName, capability);
    }

    /**
     * Gets a capability from the registry using its name.
     *
     * @param capabilityName The name of the capability.
     * @return The capability.
     */
    public static Capability getCapabilityFromRegistry(String capabilityName) {
        return CAPABILITIES_REGISTRY.get(capabilityName);
    }



    /**
     * Assigns a capability to an entity.
     *
     * @param entity     The entity to assign with the capability.
     * @param capability The capability to assign.
     */
    public static void assignCapability(Entity entity, Capability capability) throws UnsupportedOperationException {
        if (CAPABILITIES_REGISTRY.containsValue(capability)) {
            if (!getCapabilities(entity).contains(capability)) {
                String capabilityName = capability.getCapabilityName();

                entity.addScoreboardTag(capabilityName);
                ENTITY_CAPABILITY_QUEUE.put(entity, getCapabilitiesFromTags(entity));

                if (entity instanceof Player)
                    entity.sendMessage("You have been assigned the capability: " + ChatColor.YELLOW + capabilityName + ChatColor.WHITE + ".");

                capability.onAssignment(entity);
            }

        } else
            throw new UnsupportedOperationException(capability.getCapabilityName() + " is not a registered capability. Capabilities must be registered before they can be assigned.");
    }

    /**
     * Revokes a capability from an entity.
     *
     * @param entity     The entity to revoke the capability from.
     * @param capability The capability to revoke.
     */
    public static void revokeCapability(Entity entity, Capability capability) throws UnsupportedOperationException {
        if (CAPABILITIES_REGISTRY.containsValue(capability)) {
            if (getCapabilities(entity).contains(capability)) {
                String capabilityName = capability.getCapabilityName();
                
                capability.onRevoke(entity);

                entity.removeScoreboardTag(capabilityName);

                Set<Capability> entityCapabilities = getCapabilitiesFromTags(entity);

                if (entity instanceof Player)
                    entity.sendMessage("The capability, " + ChatColor.YELLOW + capabilityName + ChatColor.WHITE + ", has been revoked from you.");

                if (entityCapabilities.isEmpty()) {
                    ENTITY_CAPABILITY_QUEUE.remove(entity);

                } else
                    ENTITY_CAPABILITY_QUEUE.put(entity, entityCapabilities);
            }

        } else
            throw new UnsupportedOperationException(capability.getCapabilityName() + " is not a registered capability. Capabilities must be registered before they can be revoked.");
    }

    /**
     * Gets the capabilities an entity has with its tags.
     *
     * @param entity The entity to get capabilities from.
     * @return The capabilities an entity has.
     */
    public static Set<Capability> getCapabilitiesFromTags(Entity entity) {
        Set<String> entityTags = entity.getScoreboardTags();
        Set<Capability> entityCapabilities = new HashSet<>();

        // Looks for registered capabilities.
        for (String entityTag : entityTags)
            if (CAPABILITIES_REGISTRY.containsKey(entityTag))
                entityCapabilities.add(CAPABILITIES_REGISTRY.get(entityTag));

        return entityCapabilities;
    }

    /**
     * Gets the capabilities an entity has.
     * This methods grabs directly from the queue, instead of from entity tags, and is thus faster.
     * Method may not be 100% accurate.
     *
     * @param entity The entity to get capabilities from.
     * @return The capabilities an entity has.
     */
    public static Set<Capability> getCapabilities(Entity entity) {
        return ENTITY_CAPABILITY_QUEUE.getOrDefault(entity, new HashSet<>());
    }



    /**
     * Runs the capabilities for the entities in the queue.
     */
    public static void tickCapabilities() {
        for (Map.Entry<Entity, Set<Capability>> entityQueueEntry : ENTITY_CAPABILITY_QUEUE.entrySet()) {
            Entity entity = entityQueueEntry.getKey();
            Set<Capability> entityCapabilities = entityQueueEntry.getValue();

            for (Capability entityCapability : entityCapabilities)
                entityCapability.runCapability(entity);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // The following 4 event handlers control the loading and unloading of entities to and from the entity queue. //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        Set<Capability> playerCapabilities = getCapabilitiesFromTags(player);

        if (!playerCapabilities.isEmpty())
            ENTITY_CAPABILITY_QUEUE.put(player, playerCapabilities);
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();

        ENTITY_CAPABILITY_QUEUE.remove(player);
    }

    @EventHandler
    public static void onEntityLoad(EntityAddToWorldEvent entityAddToWorldEvent) {
        Entity entity = entityAddToWorldEvent.getEntity();

        if (!(entity instanceof Player)) {
            Set<Capability> entityCapabilities = getCapabilitiesFromTags(entity);

            if (!entityCapabilities.isEmpty())
                ENTITY_CAPABILITY_QUEUE.put(entity, entityCapabilities);
        }
    }

    @EventHandler
    public static void onEntityUnload(EntityRemoveFromWorldEvent entityRemoveFromWorldEvent) {
        Entity entity = entityRemoveFromWorldEvent.getEntity();

        if (!(entity instanceof Player))
            ENTITY_CAPABILITY_QUEUE.remove(entity);
    }

    /**
     * Loads entities onto the queue during reloads.
     */
    public static void onEnable() {
        for (World world : Bukkit.getWorlds())
            for (Entity entity : world.getEntities()) {
                Set<Capability> entityCapabilities = getCapabilitiesFromTags(entity);

                if (!entityCapabilities.isEmpty())
                    ENTITY_CAPABILITY_QUEUE.put(entity, entityCapabilities);
            }
    }



    // A runnable used to periodically log debug information.
    private static BukkitRunnable debugRunnable = null;
    // The interval that the Entity Queue is dumped into the logs when debugging is on.
    private static long debugLoggerInterval = 1200;

    /**
     * Assigns and revokes registered capabilities through commands.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1)
            switch (args[0].toLowerCase()) {
                // Lists all registered capabilities on the server.
                case "list":
                    if (args.length == 1) {
                        if (CAPABILITIES_REGISTRY.isEmpty()) {
                            sender.sendMessage("There are no registered capabilities.");

                        } else if (CAPABILITIES_REGISTRY.size() == 1) {
                            sender.sendMessage("The registered capability is: " + ChatColor.YELLOW + CAPABILITIES_REGISTRY.keySet().toArray()[0]);

                        } else {
                            List<String> messageList = new ArrayList<>();
                            messageList.add("The registered capabilities are: ");

                            for (String capabilityName : CAPABILITIES_REGISTRY.keySet())
                                messageList.add(" - " + ChatColor.YELLOW + capabilityName);

                            for (String message : messageList)
                                sender.sendMessage(message);
                        }

                    } else {
                        List<Entity> targets = CommandHelper.getCommandTargets(sender, args[1]);

                        if (targets.isEmpty()) {
                            sender.sendMessage(ChatColor.RED + "Entity '" + args[1] + "' cannot be found.");

                        } else if (targets.size() == 1) {
                            Entity target = targets.get(0);
                            Set<Capability> targetCapabilities = getCapabilities(target);

                            if (targetCapabilities.isEmpty()) {
                                sender.sendMessage(target.getName() + " has no capabilities.");

                            } else if (targetCapabilities.size() == 1) {
                                Iterator<Capability> capabilityIterator = targetCapabilities.iterator();
                                sender.sendMessage(target.getName() + " has the capability: " + ChatColor.YELLOW + capabilityIterator.next().getCapabilityName() + ChatColor.WHITE + ".");

                            } else {
                                List<String> messageList = new ArrayList<>();
                                messageList.add(target.getName() + " has the following capabilities: ");

                                for (Capability capability : targetCapabilities)
                                    messageList.add(" - " + ChatColor.GOLD + capability.getCapabilityName());

                                for (String message : messageList)
                                    sender.sendMessage(message);
                            }

                        } else {
                            Set<Capability> totalCapabilities = new HashSet<>();

                            for (Entity target : targets)
                                totalCapabilities.addAll(getCapabilities(target));

                            if (totalCapabilities.isEmpty()) {
                                sender.sendMessage(targets.size() + " entities have no capabilities.");

                            } else if (totalCapabilities.size() == 1) {
                                Iterator<Capability> capabilityIterator = totalCapabilities.iterator();
                                sender.sendMessage(targets.size() + " entities have, total, the capability: " + ChatColor.YELLOW + capabilityIterator.next().getCapabilityName() + ChatColor.WHITE + ".");

                            } else {
                                List<String> messageList = new ArrayList<>();
                                messageList.add(targets.size() + " entities have the following total capabilities: ");

                                for (Capability capability : totalCapabilities)
                                    messageList.add(" - " + ChatColor.GOLD + capability.getCapabilityName());

                                for (String message : messageList)
                                    sender.sendMessage(message);
                            }
                        }
                    }

                    return true;


                // Debug commands for testing and troubleshooting the capabilities system.
                case "debug":
                    if (args.length >= 2) {
                        switch (args[1].toLowerCase()) {
                            case "start":
                                if (debugRunnable == null) {
                                    debugRunnable = new BukkitRunnable() { @Override public void run() {
                                        logEntityQueueDump();
                                    }};
                                    debugRunnable.runTaskTimer(MainBoi.getInstance(), 60, debugLoggerInterval);

                                    sender.sendMessage("Debugging has been started, will log every " + debugLoggerInterval + " ticks.");

                                } else
                                    sender.sendMessage(ChatColor.RED + "The capabilities debugger is already started.");

                                break;

                            case "stop":
                                if (debugRunnable != null) {
                                    debugRunnable.cancel();
                                    debugRunnable = null;

                                    sender.sendMessage("Debugging has been stopped.");

                                } else
                                    sender.sendMessage(ChatColor.RED + "The capabilities debugger is already stopped.");

                                break;

                            case "dump":
                                logEntityQueueDump();

                                break;

                            case "setinterval":
                                if (args.length >= 3) {
                                    try {
                                        debugLoggerInterval = Long.parseLong(args[2]);

                                        if (debugRunnable != null) {
                                            debugRunnable.cancel();

                                            debugRunnable = new BukkitRunnable() { @Override public void run() {
                                                logEntityQueueDump();
                                            }};
                                            debugRunnable.runTaskTimer(MainBoi.getInstance(), 60, debugLoggerInterval);
                                        }

                                        sender.sendMessage("Debug interval successfully set to " + debugLoggerInterval + " ticks.");

                                    } catch (NumberFormatException ignored) {
                                        sender.sendMessage(ChatColor.RED + args[2] + " must be a number!");
                                    }

                                } else
                                    sender.sendMessage("Usage: /capabilities debug setInterval <intervalTime>");

                                break;

                            default:
                                sender.sendMessage("Usage: /capabilities debug (start|stop|dump|setInterval)");
                                break;
                        }

                    } else
                        sender.sendMessage("Usage: /capabilities debug (start|stop|dump|setInterval)");

                    return true;


                // Assigns capabilities to entities.
                case "assign":
                    if (args.length >= 3) {
                        Capability capability = CAPABILITIES_REGISTRY.get(args[2]);

                        if (capability != null) {
                            List<Entity> targets = CommandHelper.getCommandTargets(sender, args[1]);

                            if (targets.isEmpty()) {
                                sender.sendMessage(ChatColor.RED + "Entity '" + args[1] + "' cannot be found.");

                            } else if (targets.size() == 1) {
                                Entity target = targets.get(0);
                                assignCapability(target, capability);

                                sender.sendMessage("Assigned '" + ChatColor.YELLOW + capability.getCapabilityName() + ChatColor.WHITE + "' to " + target.getName() + ".");

                            } else {
                                for (Entity target : targets)
                                    assignCapability(target, capability);

                                sender.sendMessage("Assigned '" + ChatColor.YELLOW + capability.getCapabilityName() + ChatColor.WHITE + "' to " + targets.size() + " entities.");
                            }

                        } else
                            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a known capability");

                    } else
                        sender.sendMessage("Usage: /capabilities assign <targets> <capability>");

                    return true;


                // Revokes capabilities from entities.
                case "revoke":
                    if (args.length >= 3) {
                        if (args[2].equals("__all")) {
                            List<Entity> targets = CommandHelper.getCommandTargets(sender, args[1]);

                            if (targets.isEmpty()) {
                                sender.sendMessage(ChatColor.RED + "Entity '" + args[1] + "' cannot be found.");

                            } else if (targets.size() == 1) {
                                Entity target = targets.get(0);
                                Set<Capability> targetCapabilities = getCapabilities(target);

                                for (Capability capability : targetCapabilities)
                                    revokeCapability(target, capability);

                                sender.sendMessage("Revoked all capabilities from " + target.getName() + ".");

                            } else {
                                for (Entity target : targets) {
                                    Set<Capability> targetCapabilities = getCapabilities(target);

                                    for (Capability capability : targetCapabilities)
                                        revokeCapability(target, capability);
                                }

                                sender.sendMessage("Revoked all capabilities from " + targets.size() + " entities.");
                            }

                        } else {
                            Capability capability = CAPABILITIES_REGISTRY.get(args[2]);

                            if (capability != null) {
                                List<Entity> targets = CommandHelper.getCommandTargets(sender, args[1]);

                                if (targets.isEmpty()) {
                                    sender.sendMessage(ChatColor.RED + "Entity '" + args[1] + "' cannot be found.");

                                } else if (targets.size() == 1) {
                                    Entity target = targets.get(0);
                                    revokeCapability(target, capability);

                                    sender.sendMessage("Revoked '" + ChatColor.YELLOW + capability.getCapabilityName() + ChatColor.WHITE + "' from " + target.getName() + ".");

                                } else {
                                    for (Entity target : targets)
                                        revokeCapability(target, capability);

                                    sender.sendMessage("Revoked '" + ChatColor.YELLOW + capability.getCapabilityName() + ChatColor.WHITE + "' from " + targets.size() + " entities.");
                                }

                            } else
                                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a known capability");
                        }

                    } else
                        sender.sendMessage("Usage: /capabilities revoke <targets> (<capability>|__all)");

                    return true;
            }

        return false;
    }

    // A runnable that logs a dump of the Entity Queue to the server and its admins.
    private static void logEntityQueueDump() {
        List<String> entityQueueDump = new ArrayList<>();
        entityQueueDump.add("Entity Queue dump:");

        if (ENTITY_CAPABILITY_QUEUE.isEmpty()) {
            entityQueueDump.add(" Nothing!");

        } else
            for (Map.Entry<Entity, Set<Capability>> entityQueueEntry : ENTITY_CAPABILITY_QUEUE.entrySet()) {
                Entity entity = entityQueueEntry.getKey();
                Set<Capability> entityCapabilities = entityQueueEntry.getValue();

                entityQueueDump.add(" (" + ChatColor.YELLOW + entity.getType() + ChatColor.WHITE + ") " + entity.getName() + ":");

                for (Capability capability : entityCapabilities)
                    entityQueueDump.add("  - " + ChatColor.YELLOW + capability.getCapabilityName());
            }

        entityQueueDump.add("End of Entity Queue dump.");

        Logger bukkitLogger = Bukkit.getLogger();
        for (String message : entityQueueDump)
            bukkitLogger.info(message);

        for (OfflinePlayer operator : Bukkit.getOperators()) {
            Player admin = operator.getPlayer();

            if (admin != null)
                for (String message : entityQueueDump)
                    admin.sendMessage(message);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = new ArrayList<>();

        if (args.length == 1) {
            arguments.add("assign");
            arguments.add("revoke");
            arguments.add("list");
            arguments.add("debug");

        } else if (args.length >= 1)
            switch (args[0].toLowerCase()) {
                case "assign":
                case "revoke":
                    if (args.length == 2) {
                        arguments = null;

                    } else if (args.length == 3)
                        arguments.addAll(CAPABILITIES_REGISTRY.keySet());

                    break;

                case "debug":
                    if (args.length == 2) {
                        arguments.add("start");
                        arguments.add("stop");
                        arguments.add("dump");
                        arguments.add("setInterval");
                    }

                    break;

                case "list":
                    if (args.length == 2)
                        arguments = null;
            }

        return arguments;
    }



    /**
     * An exception used for when there are duplicate registry names.
     */
    public static class DuplicateRegistryNameException extends Exception {
        DuplicateRegistryNameException(String message) {
            super(message);
        }
    }
}
