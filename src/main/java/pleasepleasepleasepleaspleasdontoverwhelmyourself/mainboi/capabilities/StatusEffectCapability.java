package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Represents a status effect, much like potion effects.
 *
 * duration - The duration of the effect in ticks.
 * amplifier - The amplifier of the status effect.
 * ambient - If true, status effects produce less, or less noticeable, particles.
 * particles - If true, particles are displayed while a status effect is active.
 * notify - If true, notifies players of the effect when they receive it, and notifies them when it is gone.
 *
 * The extra data is stored in the order that is shown above.
 */
public abstract class StatusEffectCapability extends Capability {
    protected int duration;
    protected byte amplifier;
    protected boolean ambient;
    protected boolean particles;
    protected boolean notify;

    public StatusEffectCapability(String extraData) {
        super(extraData);

        String[] splitExtraData = extraData.split(",", 5);

        try {
            duration = Integer.parseInt(splitExtraData[0]);

        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            duration = 20;
        }

        try {
            amplifier = Byte.parseByte(splitExtraData[1]);

        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            amplifier = 0;
        }

        try {
            ambient = Boolean.parseBoolean(splitExtraData[2]);

        } catch (ArrayIndexOutOfBoundsException ignored) {
            ambient = false;
        }

        try {
            particles = Boolean.parseBoolean(splitExtraData[3]);

        } catch (ArrayIndexOutOfBoundsException ignored) {
            particles = false;
        }

        try {
            notify = Boolean.parseBoolean(splitExtraData[4]);

        } catch (ArrayIndexOutOfBoundsException ignored) {
            notify = false;
        }
    }

    @Override
    public String getExtraData() {
        return duration + "," + amplifier + "," + ambient + "," + particles + "," + notify;
    }

    @Override
    public boolean isVolatile() {
        return true;
    }



    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public byte getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(byte amplifier) {
        this.amplifier = amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    public boolean hasParticles() {
        return particles;
    }

    public void setParticles(boolean particles) {
        this.particles = particles;
    }

    public boolean willNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }



    @Override
    public void runCapability(Entity entity) {
        duration -= 1;

        if (duration <= 0)
            CapabilitiesCore.revokeCapability(entity, this);
    }



    @Override
    public void onAssignment(Entity entity) {
        if (notify && entity instanceof Player) {
            String amplifierString = amplifier != 0 ? " " + String.valueOf(amplifier) : "";

            entity.sendMessage("" + ChatColor.YELLOW + ChatColor.ITALIC + this.getCapabilityName() + amplifierString + ChatColor.RESET +
                    " has been applied to you for " + ChatColor.YELLOW + ChatColor.ITALIC + Math.round(duration / 20.0) + ChatColor.RESET + " seconds");
        }
    }

    @Override
    public void onRevoke(Entity entity) {
        if (notify && entity instanceof Player) {
            String amplifierString = amplifier != 0 ? " " + String.valueOf(amplifier) : "";

            entity.sendMessage("You no longer have the effect " + ChatColor.YELLOW + ChatColor.ITALIC + this.getCapabilityName() + amplifierString + ChatColor.RESET + ".");
        }
    }
}
