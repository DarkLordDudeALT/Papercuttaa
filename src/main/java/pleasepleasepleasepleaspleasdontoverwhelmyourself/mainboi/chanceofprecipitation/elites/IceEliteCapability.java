package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.AttributeHelper;

import java.util.Set;

// TODO Add the ice bomb on death of an ice elite.

public class IceEliteCapability extends Capability implements Listener {
    public IceEliteCapability(String extraData) {
        super(extraData);
    }

    @Override
    public Capability useConstructor(String extraData) {
        return new IceEliteCapability(extraData);
    }

    @Override
    public String getCapabilityName() {
        return "COP_iceElite";
    }

    @Override
    public boolean isVolatile() {
        return true;
    }



    @Override
    public void onAssignment(Entity entity) {
        if (!(entity instanceof Player)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                AttributeInstance attackDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

                AttributeHelper.addModifierSafely(maxHealth, new AttributeModifier("COP_IE-M2", 3.7, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                AttributeHelper.addModifierSafely(attackDamage, new AttributeModifier("COP_IE-M2", 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }

            // Gives fire elites a title if they don't have a custom name already.
            if (entity.getCustomName() == null) {
                entity.setCustomName(ChatColor.AQUA + "Glacial " + entity.getName());
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

                AttributeHelper.removeModifiers(maxHealth, "COP_IE-M2", true);
                AttributeHelper.removeModifiers(attackDamage, "COP_IE-M2", true);
            }

            // Removes an ice elite's title, if they have one.
            String entityCustomName = entity.getCustomName();

            if (entityCustomName != null && entityCustomName.contains(ChatColor.AQUA + "Glacial")) {
                entity.setCustomName(null);
                entity.setCustomNameVisible(false);
            }
        }
    }


    /**
     * Makes all melee and ranged attacks from ice elites apply slowness VI for 1.5 seconds.
     */
    @EventHandler
    public static void onEntityHitEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Entity victim = entityDamageByEntityEvent.getEntity();

        if (!entityDamageByEntityEvent.isCancelled() && victim instanceof LivingEntity) {
            Entity attacker = entityDamageByEntityEvent.getDamager();
            LivingEntity livingVictim = (LivingEntity) entityDamageByEntityEvent.getEntity();
            Set<Capability> attackerCapabilities = CapabilitiesCore.getCapabilities(attacker);

            for (Capability capability : attackerCapabilities)
                if (capability instanceof IceEliteCapability) {
                    livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 3, false, true, true));
                    break;
                }

            if (attacker.getScoreboardTags().contains("COP_IE-P"))
                livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 3, false, true, true));
        }
    }

    /**
     * Marks all arrows shot by ice elites.
     */
    @EventHandler
    public static void onEntityShootBow(EntityShootBowEvent entityShootBowEvent) {
        if (!entityShootBowEvent.isCancelled()) {
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(entityShootBowEvent.getEntity());

            for (Capability capability : entityCapabilities)
                if (capability instanceof IceEliteCapability) {
                    Entity projectile = entityShootBowEvent.getProjectile();

                    projectile.addScoreboardTag("COP_IE-P");

                    break;
                }
        }
    }
}
