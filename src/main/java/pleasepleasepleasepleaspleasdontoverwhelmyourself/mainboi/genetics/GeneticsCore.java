package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics.genes.StrengthGene;

import java.util.Collection;

// TODO Remake system to store which variant else where, and to only use one capability for each gene, rather than three.

public final class GeneticsCore implements Listener {
    public static void onEnable() {
        StrengthGene strengthGene = new StrengthGene((byte) 0b00);

        // Registering genes
        try {
            CapabilitiesCore.registerCapability(strengthGene);
            CapabilitiesCore.registerCapability(new StrengthGene((byte) 0b01));
            CapabilitiesCore.registerCapability(new StrengthGene((byte) 0b10));

        } catch (CapabilitiesCore.DuplicateRegistryNameException ignore) {}

        // Registering event listeners.
        PluginManager pluginManager = MainBoi.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents(strengthGene, MainBoi.getInstance());
    }



    /**
     * Removes genes on death, resetting a player's genetic code.
     */
    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        if (!playerDeathEvent.isCancelled()) {
            Player player = playerDeathEvent.getEntity();
            Collection<Capability> playerCapabilities = CapabilitiesCore.getCapabilities(player);

            for (Capability capability : playerCapabilities)
                if (capability instanceof Gene)
                    CapabilitiesCore.revokeCapability(player, capability);
        }
    }
}
