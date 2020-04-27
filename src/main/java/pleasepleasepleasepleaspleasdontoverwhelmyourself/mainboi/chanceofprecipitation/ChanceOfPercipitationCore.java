package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation;

import org.bukkit.Bukkit;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites.FireEliteCapability;

public final class ChanceOfPercipitationCore {
    public static void onEnable() {
        FireEliteCapability fireEliteCapability = new FireEliteCapability();

        try {
            CapabilitiesCore.registerCapability(fireEliteCapability);
            Bukkit.getPluginManager().registerEvents(fireEliteCapability, MainBoi.getInstance());

        } catch (CapabilitiesCore.DuplicateRegistryNameException ignored) {}
    }
}
