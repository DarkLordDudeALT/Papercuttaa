package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Code to help with writing custom commands.
 */
public final class CommandHelper {
    /**
     * Gets the targets of a command using the selector given.
     *
     * @param sender The sender of the command.
     * @param selector The selector to use.
     *
     * @return A list of entities fitting the selector.
     */
    public static List<Entity> getCommandTargets(CommandSender sender, String selector) {
        List<Entity> targets = new ArrayList<>();

        try {
            targets.addAll(Bukkit.selectEntities(sender, selector));

        } catch (IllegalArgumentException ignore) {}

        if (targets.isEmpty()) {
            Player target = Bukkit.getPlayerExact(selector);

            if (target != null)
                targets.add(target);
        }

        return targets;
    }
}
