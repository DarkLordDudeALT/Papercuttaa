package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.AttributeHelper;

import java.util.Set;

// TODO Make the fire trail place fires in a hashmap, so that it isn't creating 25 different runnables every time.

// TODO Make a list of replaceable blocks.
// TODO Make the fire trail replace only blocks in that list, rather than air.

public class FireEliteCapability extends Capability implements Listener {
    @Override
    public String getCapabilityName() {
        return "COP_fireElite";
    }

    @Override
    public boolean isVolatile() {
        return true;
    }



    @Override
    public void runCapability(Entity entity) {
        // A fire elite on fire would be kinda weird in minecraft. Imagine being killed by your own ability.
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20, 0, false, false, false));
        }

        // Spawns a trail of fire behind fire elites.
        if (entity.isOnGround()) {
            Location entityLocation = entity.getLocation();
            Block feetBlock = entity.getWorld().getBlockAt(entityLocation);

            if (feetBlock.getType().equals(Material.AIR) || feetBlock.getType().equals(Material.CAVE_AIR)) {
                feetBlock.setType(Material.FIRE);

                // Limits the trial length.
                new BukkitRunnable() { @Override public void run() {
                    if (feetBlock.getType().equals(Material.FIRE)) {
                        Location newEntityLocation = entity.getLocation();

                        if (entityLocation.getBlockX() != newEntityLocation.getBlockX() || entityLocation.getBlockY() != newEntityLocation.getBlockY()
                                || entityLocation.getBlockZ() != newEntityLocation.getBlockZ()) {
                            feetBlock.setType(Material.AIR);
                            this.cancel();
                        }

                    } else
                        this.cancel();

                }}.runTaskTimer(MainBoi.getInstance(), 60, 60);
            }
        }

        // Makes it so fire elites are always on fire.
        entity.setFireTicks(20);
    }

    @Override
    public void onAssignment(Entity entity) {
        if (!(entity instanceof Player)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                AttributeInstance attackDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

                AttributeHelper.addModifierSafely(maxHealth, new AttributeModifier("COP_FE-M2", 3.7, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                AttributeHelper.addModifierSafely(attackDamage, new AttributeModifier("COP_FE-M2", 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }

            // Gives fire elites a title if they don't have a custom name already.
            if (entity.getCustomName() == null) {
                entity.setCustomName(ChatColor.RED + "Blazing " + entity.getName());
                entity.setCustomNameVisible(true);
            }
        }
    }

    @Override
    public void onRevoke(Entity entity) {
        if (!(entity instanceof Player)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                AttributeInstance attackDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

                AttributeHelper.removeModifiers(maxHealth, "COP_FE-M2", true);
                AttributeHelper.removeModifiers(attackDamage, "COP_FE-M2", true);
            }

            Block feetBlock = entity.getWorld().getBlockAt(entity.getLocation());

            if (feetBlock.getType().equals(Material.FIRE))
                feetBlock.setType(Material.AIR);

            if (entity.getFireTicks() > 0)
                entity.setFireTicks(0);

            // Removes a fire elite's title, if they have one.
            String entityCustomName = entity.getCustomName();

            if (entityCustomName != null && entityCustomName.contains(ChatColor.RED + "Blazing")) {
                entity.setCustomName(null);
                entity.setCustomNameVisible(false);
            }
        }
    }



    /**
     * Makes all melee attacks from fire elites apply 160 ticks of fire. Equal to fire aspect II.
     */
    @EventHandler
    public static void onEntityHitEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (!entityDamageByEntityEvent.isCancelled()) {
            Entity attacker = entityDamageByEntityEvent.getDamager();
            Set<Capability> attackerCapabilities = CapabilitiesCore.getCapabilities(attacker);

            for (Capability capability : attackerCapabilities)
                if (capability instanceof FireEliteCapability) {
                    Entity victim = entityDamageByEntityEvent.getEntity();

                    victim.setFireTicks(victim.getFireTicks() + 160);

                    break;
                }
        }
    }

    /**
     * Makes all arrows shot by fire elites be set aflame.
     */
    @EventHandler
    public static void onEntityShootBow(EntityShootBowEvent entityShootBowEvent) {
        if (!entityShootBowEvent.isCancelled()) {
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(entityShootBowEvent.getEntity());

            for (Capability capability : entityCapabilities)
                if (capability instanceof FireEliteCapability) {
                    Entity projectile = entityShootBowEvent.getProjectile();

                    projectile.setFireTicks(2000);

                    break;
                }
        }
    }
}
