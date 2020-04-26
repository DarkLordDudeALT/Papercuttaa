package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
        List<Entity> targets = Bukkit.selectEntities(sender, selector);

        if (targets.isEmpty()) {
            Player target = Bukkit.getPlayerExact(selector);

            if (target != null)
                targets.add(target);
        }

        return targets;
    }
}
