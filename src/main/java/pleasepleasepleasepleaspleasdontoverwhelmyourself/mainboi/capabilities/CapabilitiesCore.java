package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.CommandHelper;

import java.util.*;
import java.util.logging.Logger;

// TODO Add /capabilities debug setInterval (collector|assimilator) to set the interval time of the collector and the assimilator.
//  Have the debug logger follow suit.
// TODO Have debugInterval, collectorRunInterval, assimilatorRunInterval and be loaded to and from a file on plugin shutdown and startup.

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

    public static void onEnable() {
        MainBoi mainBoi = MainBoi.getInstance();
        CapabilitiesCore capabilitiesCore = new CapabilitiesCore();

        mainBoi.getServer().getPluginManager().registerEvents(capabilitiesCore, mainBoi);

        PluginCommand capabilitiesCommand = Objects.requireNonNull(mainBoi.getCommand("capabilities"));
        capabilitiesCommand.setExecutor(capabilitiesCore);
        capabilitiesCommand.setTabCompleter(capabilitiesCore);

        new BukkitRunnable() { @Override public void run() {
            runAssimilator();
        }}.runTaskTimer(MainBoi.getInstance(), 50, assimilatorRunInterval);

        new BukkitRunnable() { @Override public void run() {
            runCollector();
        }}.runTaskTimer(MainBoi.getInstance(), 51, collectorRunInterval);
    }

    // The amount of time to wait between each run of the collector.
    private static final long collectorRunInterval = 300;

    /**
     * Runs through the Entity Queue, getting rid of entities with no capabilities, and fixes discrepancies with it and entity tags.
     */
    private static void runCollector() {
        Set<Entity> removalQueue = new HashSet<>();

        for (Entity entity : ENTITY_CAPABILITY_QUEUE.keySet()) {
            Set<Capability> entityCapabilities = getCapabilitiesFromTags(entity);

            if (entityCapabilities.isEmpty()) {
                removalQueue.add(entity);

            } else {
                Set<Capability> activeEntityCapabilities = getCapabilities(entity);

                // Removes capabilities that are not contained in the tags.
                for (Capability activeCapability : activeEntityCapabilities) {
                    String activeCapabilityName = activeCapability.getCapabilityName();
                    boolean hasCapability = false;

                    for (Capability capability : entityCapabilities)
                        if (activeCapabilityName.equals(capability.getCapabilityName())) {
                            hasCapability = true;
                            break;
                        }

                    if (!hasCapability)
                        revokeCapability(entity, activeCapability);
                }

                activeEntityCapabilities = getCapabilities(entity);

                // Loads unloaded capabilities, fixes discrepancies with extra data.
                for (Capability capability : entityCapabilities) {
                    boolean hasCapability = false;

                    for (Capability activeCapability : activeEntityCapabilities) {
                        String activeCapabilityName = capability.getCapabilityName();
                        String capabilityName = capability.getCapabilityName();

                        if (capabilityName.equals(activeCapabilityName)) {
                            hasCapability = true;

                            // Overrides the capability queue's extra data onto the tags.
                            if (!capability.getExtraData().equals(activeCapability.getExtraData()))
                                for (String tag : entity.getScoreboardTags())
                                    if (tag.contains(activeCapabilityName)) {
                                        entity.removeScoreboardTag(tag);
                                        entity.addScoreboardTag(joinNameAndExtra(activeCapabilityName, activeCapability.getExtraData()));

                                        break;
                                    }

                            break;
                        }
                    }

                    if (!hasCapability)
                        ENTITY_CAPABILITY_QUEUE.get(entity).add(capability);
                }
            }
        }

        for (Entity entity : removalQueue)
            ENTITY_CAPABILITY_QUEUE.remove(entity);
    }

    // The amount of time to wait between each run of the assimilator.
    private static final long assimilatorRunInterval = 600;

    /**
     * Runs through all loaded entities, looking for those with capabilities that are not in the queue, so that it can add them.
     */
    private static void runAssimilator() {
        for (World world : Bukkit.getWorlds())
            for (Entity entity : world.getEntities())
                if (!ENTITY_CAPABILITY_QUEUE.containsKey(entity)) {
                    Set<Capability> entityCapabilities = getCapabilitiesFromTags(entity);

                    if (!entityCapabilities.isEmpty())
                        ENTITY_CAPABILITY_QUEUE.put(entity, entityCapabilities);
                }
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
     * Joins a capability name and its extra data.
     *
     * @param capabilityName The name of the capability.
     * @param extraData The extra data of the capability.
     *
     * @return The joined form.
     */
    public static String joinNameAndExtra(String capabilityName, String extraData) {
        return extraData.equals("") ? capabilityName : capabilityName + "-" + extraData;
    }



    /**
     * Assigns a capability to an entity.
     *
     * @param entity The entity to assign with the capability.
     * @param capability The capability to assign.
     *
     * @return If the capability was successfully assigned.
     */
    public static boolean assignCapability(Entity entity, Capability capability) throws UnsupportedOperationException {
        String capabilityName = capability.getCapabilityName();

        if (CAPABILITIES_REGISTRY.containsKey(capabilityName)) {
            boolean hasCapability = false;

            for (Capability possibleMatch : getCapabilities(entity))
                if (possibleMatch.getCapabilityName().equals(capabilityName)) {
                    hasCapability = true;
                    break;
                }

            if (!hasCapability) {
                if (!ENTITY_CAPABILITY_QUEUE.containsKey(entity))
                    ENTITY_CAPABILITY_QUEUE.put(entity, getCapabilitiesFromTags(entity));

                entity.addScoreboardTag(joinNameAndExtra(capability.getCapabilityName(), capability.getExtraData()));

                //if (entity instanceof Player && entity.isOp())
                //    entity.sendMessage("You have been assigned the capability: " + ChatColor.YELLOW + joinNameAndExtra(capability.getCapabilityName(), capability.getExtraData()) + ChatColor.WHITE + ".");

                ENTITY_CAPABILITY_QUEUE.get(entity).add(capability);
                capability.onAssignment(entity);

                return true;
            }

        } else
            throw new UnsupportedOperationException(capability.getCapabilityName() + " is not a registered capability. Capabilities must be registered before they can be assigned.");

        return false;
    }

    /**
     * Revokes a capability from an entity.
     *
     * @param entity The entity to revoke the capability from.
     * @param capability The capability to revoke.
     *
     * @return If the capability was successfully revoked.
     */
    public static boolean revokeCapability(Entity entity, Capability capability) throws UnsupportedOperationException {
        String capabilityName = capability.getCapabilityName();

        if (CAPABILITIES_REGISTRY.containsKey(capabilityName)) {
            Set<Capability> entityCapabilities = getCapabilities(entity);

            for (Capability possibleMatch : entityCapabilities)
                if (possibleMatch.getCapabilityName().equals(capabilityName)) {
                    capability.onRevoke(entity);

                    for (String tag : entity.getScoreboardTags())
                        if (tag.contains(capabilityName)) {
                            entity.removeScoreboardTag(tag);
                            break;
                        }

                    Set<Capability> trueEntityCapabilities = getCapabilitiesFromTags(entity);

                    if (trueEntityCapabilities.isEmpty()) {
                        ENTITY_CAPABILITY_QUEUE.remove(entity);

                    } else
                        ENTITY_CAPABILITY_QUEUE.get(entity).remove(capability);

                    //if (entity instanceof Player && entity.isOp())
                    //    entity.sendMessage("The capability, " + ChatColor.YELLOW + joinNameAndExtra(capability.getCapabilityName(), capability.getExtraData()) + ChatColor.WHITE + ", has been revoked from you.");

                    return true;
                }

        } else
            throw new UnsupportedOperationException(capability.getCapabilityName() + " is not a registered capability. Capabilities must be registered before they can be revoked.");

        return false;
    }

    /**
     * Gets the capabilities an entity has with its tags.
     *
     * @param entity The entity to get capabilities from.
     *
     * @return The capabilities an entity has.
     */
    public static Set<Capability> getCapabilitiesFromTags(Entity entity) {
        Set<String> entityTags = entity.getScoreboardTags();
        Set<Capability> entityCapabilities = new HashSet<>();

        // Looks for registered capabilities.
        for (String entityTag : entityTags) {
            Capability capability = getCapabilityFromTag(entityTag);

            if (capability != null)
                entityCapabilities.add(capability);
        }

        return entityCapabilities;
    }

    /**
     * Gets an instance of capability from a tag, adding in any extra data the tag has.
     *
     * @param entityTag The tag to get the capability from.
     *
     * @return The capability a tag has.
     */
    public static Capability getCapabilityFromTag(String entityTag) {
        String capabilityName;
        String extraData;

        // Extracts the capability name and any extra data from the tag.
        if (entityTag.contains("-")) {
            String[] splitTag = entityTag.split("-", 2);

            capabilityName = splitTag[0];
            extraData = splitTag[1];

        } else {
            capabilityName = entityTag;
            extraData = "";
        }

        Capability capability;

        // Creates a copy of the capability with the new data.
        if (CAPABILITIES_REGISTRY.containsKey(capabilityName)) {
            capability = CAPABILITIES_REGISTRY.get(capabilityName).useConstructor(extraData);

        } else
            capability = null;

        return capability;
    }

    /**
     * Gets the capabilities an entity has.
     * This methods grabs directly from the queue, instead of from the entity tags, and is thus faster.
     * Method may not be 100% accurate to tags.
     *
     * @param entity The entity to get capabilities from.
     * @return The capabilities an entity has.
     */
    public static Set<Capability> getCapabilities(Entity entity) {
        return ENTITY_CAPABILITY_QUEUE.getOrDefault(entity, new HashSet<>());
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

        // Overrides extra data onto tags before unload.
        if (ENTITY_CAPABILITY_QUEUE.containsKey(player)) {
            Set<Capability> playerCapabilities = getCapabilitiesFromTags(player);
            Set<Capability> activePlayerCapabilities = getCapabilities(player);

            for (Capability capability : playerCapabilities)
                for (Capability activeCapability : activePlayerCapabilities) {
                    String activeCapabilityName = capability.getCapabilityName();

                    if (capability.getCapabilityName().equals(activeCapabilityName)) {
                        String activeExtraData = activeCapability.getExtraData();

                        if (!capability.getExtraData().equals(activeExtraData))
                            for (String tag : player.getScoreboardTags())
                                if (tag.contains(activeCapabilityName)) {
                                    player.removeScoreboardTag(tag);
                                    player.addScoreboardTag(joinNameAndExtra(activeCapabilityName, activeExtraData));

                                    break;
                                }

                        break;
                    }
                }
        }

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

        if (!(entity instanceof Player)) {
            // Overrides extra data onto tags before unload.
            if (ENTITY_CAPABILITY_QUEUE.containsKey(entity)) {
                Set<Capability> entityCapabilities = getCapabilitiesFromTags(entity);
                Set<Capability> activeEntityCapabilities = getCapabilities(entity);

                for (Capability capability : entityCapabilities)
                    for (Capability activeCapability : activeEntityCapabilities) {
                        String activeCapabilityName = capability.getCapabilityName();

                        if (capability.getCapabilityName().equals(activeCapabilityName)) {
                            String activeExtraData = activeCapability.getExtraData();

                            if (!capability.getExtraData().equals(activeExtraData))
                                for (String tag : entity.getScoreboardTags())
                                    if (tag.contains(activeCapabilityName)) {
                                        entity.removeScoreboardTag(tag);
                                        entity.addScoreboardTag(joinNameAndExtra(activeCapabilityName, activeExtraData));

                                        break;
                                    }

                            break;
                        }
                    }
            }

            ENTITY_CAPABILITY_QUEUE.remove(entity);
        }
    }

    /**
     * Removes volatile capabilities on player death.
     */
    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        if (!playerDeathEvent.isCancelled()) {
            Player player = playerDeathEvent.getEntity();
            Set<Capability> playerCapabilities = CapabilitiesCore.getCapabilities(player);

            for (Capability capability : playerCapabilities)
                if (capability.isVolatile())
                    CapabilitiesCore.revokeCapability(player, capability);
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
                                Capability entityCapability = targetCapabilities.iterator().next();
                                sender.sendMessage(target.getName() + " has the capability: " + ChatColor.YELLOW + joinNameAndExtra(entityCapability.getCapabilityName(), entityCapability.getExtraData()) + ChatColor.WHITE + ".");

                            } else {
                                List<String> messageList = new ArrayList<>();
                                messageList.add(target.getName() + " has the following capabilities: ");

                                for (Capability capability : targetCapabilities)
                                    messageList.add(" - " + ChatColor.GOLD + joinNameAndExtra(capability.getCapabilityName(), capability.getExtraData()));

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
                                    debugRunnable.runTaskTimer(MainBoi.getInstance(), 100, debugLoggerInterval);

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
                                            debugRunnable.runTaskTimer(MainBoi.getInstance(), 100, debugLoggerInterval);
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
                        Capability capability = getCapabilityFromTag(args[2]);

                        if (capability != null) {
                            List<Entity> targets = CommandHelper.getCommandTargets(sender, args[1]);

                            if (targets.isEmpty()) {
                                sender.sendMessage(ChatColor.RED + "Entity '" + args[1] + "' cannot be found.");

                            } else if (targets.size() == 1) {
                                Entity target = targets.get(0);
                                boolean success = assignCapability(target, capability);

                                if (success) {
                                    sender.sendMessage("Assigned '" + ChatColor.YELLOW + joinNameAndExtra(capability.getCapabilityName(), capability.getExtraData()) + ChatColor.WHITE + "' to " + target.getName() + ".");

                                } else
                                    sender.sendMessage(ChatColor.RED + "The entity already has this capability");

                            } else {
                                boolean success = false;
                                int effectedEntityCount = 0;

                                for (Entity target : targets)
                                    if (assignCapability(target, capability)) {
                                        success = true;
                                        effectedEntityCount++;
                                    }

                                if (success) {
                                    sender.sendMessage("Assigned '" + ChatColor.YELLOW + joinNameAndExtra(capability.getCapabilityName(), capability.getExtraData()) + ChatColor.WHITE + "' to " + effectedEntityCount + " entities.");

                                } else
                                    sender.sendMessage(ChatColor.RED + "All of the entities already have this capability.");
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

                                if (targetCapabilities.isEmpty()) {
                                    sender.sendMessage(ChatColor.RED + "The entity has no capabilities.");

                                } else {
                                    for (Capability capability : targetCapabilities)
                                        revokeCapability(target, capability);

                                    sender.sendMessage("Revoked all capabilities from " + target.getName() + ".");
                                }

                            } else {
                                boolean noCapabilities = true;
                                int effectedEntityCount = 0;

                                for (Entity target : targets) {
                                    Set<Capability> targetCapabilities = getCapabilities(target);

                                    if (!targetCapabilities.isEmpty()) {
                                        noCapabilities = false;
                                        effectedEntityCount++;

                                        for (Capability capability : targetCapabilities)
                                            revokeCapability(target, capability);
                                    }
                                }

                                if (noCapabilities) {
                                    sender.sendMessage(ChatColor.RED + "All of the entities have no capabilities.");

                                } else
                                    sender.sendMessage("Revoked all capabilities from " + effectedEntityCount + " entities.");
                            }

                        } else {
                            Capability capability = getCapabilityFromTag(args[2]);

                            if (capability != null) {
                                List<Entity> targets = CommandHelper.getCommandTargets(sender, args[1]);

                                if (targets.isEmpty()) {
                                    sender.sendMessage(ChatColor.RED + "Entity '" + args[1] + "' cannot be found.");

                                } else if (targets.size() == 1) {
                                    Entity target = targets.get(0);
                                    boolean success = revokeCapability(target, capability);

                                    if (success) {
                                        sender.sendMessage("Revoked '" + ChatColor.YELLOW + capability.getCapabilityName() + ChatColor.WHITE + "' from " + target.getName() + ".");

                                    } else
                                        sender.sendMessage(ChatColor.RED + "The entity does not have this capability.");

                                } else {
                                    boolean success = false;
                                    int effectedEntityCount = 0;

                                    for (Entity target : targets)
                                        if (revokeCapability(target, capability)) {
                                            success = true;
                                            effectedEntityCount++;
                                        }

                                    if (success) {
                                        sender.sendMessage("Revoked '" + ChatColor.YELLOW + capability.getCapabilityName() + ChatColor.WHITE + "' from " + effectedEntityCount + " entities.");

                                    } else
                                        sender.sendMessage(ChatColor.RED + "None of the entities have this capability.");
                                }

                            } else
                                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a known capability.");
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
                    entityQueueDump.add("  - " + ChatColor.YELLOW + joinNameAndExtra(capability.getCapabilityName(), capability.getExtraData()));
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
                    if (args.length == 2) {
                        arguments = null;

                    } else if (args.length == 3)
                        arguments.addAll(CAPABILITIES_REGISTRY.keySet());

                    break;

                case "revoke":
                    if (args.length == 2) {
                        arguments = null;

                    } else if (args.length == 3) {
                        List<Entity> targets = CommandHelper.getCommandTargets(sender, args[1]);
                        Set<Capability> targetsCapabilities = new HashSet<>();

                        for (Entity target : targets)
                            targetsCapabilities.addAll(getCapabilities(target));

                        for (Capability capability : targetsCapabilities)
                            arguments.add(capability.getCapabilityName());

                        if (!targetsCapabilities.isEmpty())
                            arguments.add("__all");
                    }

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
