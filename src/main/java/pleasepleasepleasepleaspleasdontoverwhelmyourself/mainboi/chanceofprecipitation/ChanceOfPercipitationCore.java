package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation;

import org.bukkit.Bukkit;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites.FireEliteCapability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites.IceEliteCapability;

// TODO Allow players to craft a Empty Soul Capsule. Upon slaying an elite with an empty soul capsule in the inventory,
//  it will become a (ELITE TITLE GOES HERE) Soul Capsule. Using this item with right-click will destroy it,
//  and give the player the power of the elites temporarily.  

public final class ChanceOfPercipitationCore {
    public static void onEnable() {
        MainBoi mainBoi = MainBoi.getInstance();

        FireEliteCapability fireEliteCapability = new FireEliteCapability("");
        IceEliteCapability iceEliteCapability = new IceEliteCapability("");
        IceEliteCapability.IceBombCapability iceBombCapability = new IceEliteCapability.IceBombCapability("0");

        try {
            CapabilitiesCore.registerCapability(fireEliteCapability);
            Bukkit.getPluginManager().registerEvents(fireEliteCapability, mainBoi);

        } catch (CapabilitiesCore.DuplicateRegistryNameException exception) {
            exception.printStackTrace();
        }

        try {
            CapabilitiesCore.registerCapability(iceEliteCapability);
            Bukkit.getPluginManager().registerEvents(iceEliteCapability, mainBoi);
            CapabilitiesCore.registerCapability(iceBombCapability);

        } catch (CapabilitiesCore.DuplicateRegistryNameException exception) {
            exception.printStackTrace();
        }
    }
}
