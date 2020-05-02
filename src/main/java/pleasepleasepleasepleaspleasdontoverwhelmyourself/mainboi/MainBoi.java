package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.ChanceOfPercipitationCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics.GeneticsCore;

// TODO Add the ability for players to set their spawn at campfires.

// TODO Add programmable golems.

// TODO Add Very Hard difficulty, which does normal difficulty things on top of some other cool stuff.
//  Creepers leave behind fire.
//  Fire elites use VH_airReplaceables instead of airReplaceables.
//  Fire elites trails last longer.
//  Spiders shoot webs.

public final class MainBoi extends JavaPlugin{
    private static MainBoi instance;

    @Override
    public void onEnable() {
        instance = this;

        // Registering event listeners.
        //PluginManager pluginManager = getServer().getPluginManager();

        CapabilitiesCore.onEnable();
        GeneticsCore.onEnable();
        ChanceOfPercipitationCore.onEnable();

        // Starts onTick function.
        new BukkitRunnable() {@Override public void run() {
                onTick();
        }}.runTaskTimer(this, 0, 1);
    }

    /**
     * Base runnable for the plugin.
     */
    private static void onTick() {
        CapabilitiesCore.tickCapabilities();
    }

    @Override
    public void onDisable() {

    }



    /**
     * Returns the plugin's instance, mainly used for BukkitRunnable.
     *
     * @return The plugin's instance
     */
    public static MainBoi getInstance() {
        return instance;
    }
}
