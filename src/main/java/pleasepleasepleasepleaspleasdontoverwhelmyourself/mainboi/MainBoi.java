package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
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

// TODO Have sans appear as a player in the tab menu.

public final class MainBoi extends JavaPlugin implements Listener {
    private static MainBoi instance;

    @Override
    public void onEnable() {
        instance = this;

        // Registering event listeners.
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);

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



    // 草。
    private static final String fakePlayerName = "Gnome";

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player sans = Bukkit.getPlayer(fakePlayerName);
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer overworld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        EntityPlayer fakePlayer;

        if (sans != null) {
            fakePlayer = new EntityPlayer(server, overworld, new GameProfile(sans.getUniqueId(), "sans"), new PlayerInteractManager(overworld));

        } else
            fakePlayer = new EntityPlayer(server, overworld, new GameProfile(Bukkit.getOfflinePlayer(fakePlayerName).getUniqueId(), fakePlayerName), new PlayerInteractManager(overworld));

        PlayerConnection playerConnection = ((CraftPlayer) playerJoinEvent.getPlayer()).getHandle().playerConnection;
        playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, fakePlayer));
    }
}
