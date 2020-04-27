package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics.genes.StrengthGene;

// TODO Remake system to store which variant else where, and to only use one capability for each gene, rather than three.

public final class GeneticsCore implements Listener {
    public static void onEnable() {
        PluginManager pluginManager = MainBoi.getInstance().getServer().getPluginManager();

        // Registering genes
        try {
            StrengthGene strengthGene = new StrengthGene((byte) 0b00);

            CapabilitiesCore.registerCapability(strengthGene);
            CapabilitiesCore.registerCapability(new StrengthGene((byte) 0b01));
            CapabilitiesCore.registerCapability(new StrengthGene((byte) 0b10));

            pluginManager.registerEvents(strengthGene, MainBoi.getInstance());

        } catch (CapabilitiesCore.DuplicateRegistryNameException ignore) {}
    }
}
