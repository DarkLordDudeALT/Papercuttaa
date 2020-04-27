package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.AttributeHelper;

import java.util.Set;

// TODO Make it so the fire dispersion only works if the entity is not on the fire.
// TODO Add red title with «Blazing» to elites without a custom name.
// TODO Have projectiles fired by fire elites be set aflame.

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
            Block feetBlock = entity.getWorld().getBlockAt(entity.getLocation());

            if (feetBlock.getType().equals(Material.AIR) || feetBlock.getType().equals(Material.CAVE_AIR)) {
                feetBlock.setType(Material.FIRE);

                // Limits the trial length.
                new BukkitRunnable() { @Override public void run() {
                    if (feetBlock.getType().equals(Material.FIRE))
                        feetBlock.setType(Material.AIR);

                }}.runTaskLater(MainBoi.getInstance(), 60);
            }
        }

        // Makes it so fire elites are always on fire.
        entity.setFireTicks(20);
    }

    @Override
    public void onAssignment(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            AttributeInstance attackDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

            AttributeHelper.addModifierSafely(maxHealth, new AttributeModifier("COP_FE-M2", 3.7, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            AttributeHelper.addModifierSafely(attackDamage, new AttributeModifier("COP_FE-M2", 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }
    }

    @Override
    public void onRevoke(Entity entity) {
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
}
