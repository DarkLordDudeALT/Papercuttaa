package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers;

import org.bukkit.Bukkit;

/**
 * Code used to help deal with NMS in a version-friendly way.
 */
public final class NMSHelper {
    private static String NMSVersion;

    public static void onEnable() {
        NMSVersion = Bukkit.getVersion().getClass().getPackage().getName().split("\\.")[3];
    }


    /**
     * Gets the version of NMS currently in use.
     *
     * @return The current NMS version.
     */
    public static String getNMSVersion() {
        return NMSVersion;
    }
}
