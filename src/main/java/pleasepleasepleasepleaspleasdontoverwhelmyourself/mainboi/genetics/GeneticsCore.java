package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics.genes.StrengthGene;

public final class GeneticsCore implements Listener {
    public static void onEnable() {
        MainBoi mainBoi = MainBoi.getInstance();

        PluginManager pluginManager = MainBoi.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents(new GeneticsCore(), mainBoi);

        // Registering genes
        try {
            StrengthGene strengthGene = new StrengthGene("0");

            CapabilitiesCore.registerCapability(strengthGene);
            pluginManager.registerEvents(strengthGene, mainBoi);

        } catch (CapabilitiesCore.DuplicateRegistryNameException ignore) {}
    }
}
