package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics.genes;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.MainBoi;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics.Gene;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.AttributeHelper;

import java.util.Set;

// TODO Increase knockback dealt by the strength gene.

public class StrengthGene extends Gene implements Listener {
    public StrengthGene(String extraData) {
        super(extraData);
    }

    @Override
    public Capability useConstructor(String extraData) {
        return new StrengthGene(extraData);
    }

    @Override
    public String getCapabilityName() {
        return "G_strength";
    }



    @EventHandler
    public static void onEntityJump(EntityJumpEvent entityJumpEvent) {
        if (!entityJumpEvent.isCancelled()) {
            LivingEntity livingEntity = entityJumpEvent.getEntity();
            Set<Capability> livingEntityCapabilities = CapabilitiesCore.getCapabilities(livingEntity);

            for (Capability possibleGene : livingEntityCapabilities)
                if (possibleGene instanceof StrengthGene) {
                    byte variant = ((Gene) possibleGene).getVariant();

                    if (variant != 0)
                        new BukkitRunnable() { @Override public void run() {
                            livingEntity.setVelocity(livingEntity.getVelocity().multiply(new Vector(1 + variant * 0.03, 1 + variant * 0.05, 1 + variant * 0.03)));
                        }}.runTaskLater(MainBoi.getInstance(), 1);

                    break;
                }
        }
    }

    @EventHandler
    public static void onPlayerJump(PlayerJumpEvent playerJumpEvent) {
        if (!playerJumpEvent.isCancelled()) {
            Player player = playerJumpEvent.getPlayer();
            Set<Capability> playerCapabilities = CapabilitiesCore.getCapabilities(player);

            for (Capability possibleGene : playerCapabilities)
                if (possibleGene instanceof StrengthGene) {
                    byte variant = ((Gene) possibleGene).getVariant();

                    if (variant != 0)
                        new BukkitRunnable() { @Override public void run() {
                            player.setVelocity(player.getVelocity().multiply(new Vector(1 + variant * 0.03, 1 + variant * 0.05, 1 + variant * 0.03)));
                        }}.runTaskLater(MainBoi.getInstance(), 1);

                    break;
                }
        }
    }

    // Makes entities fire faster projectiles.
    @EventHandler
    public static void onBowFire(EntityShootBowEvent entityShootBowEvent) {
        if (!entityShootBowEvent.isCancelled()) {
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(entityShootBowEvent.getEntity());

            for (Capability possibleGene : entityCapabilities)
                if (possibleGene instanceof StrengthGene) {
                    byte variant = ((Gene) possibleGene).getVariant();

                    if (variant != 0) {
                        Entity projectile = entityShootBowEvent.getProjectile();

                        projectile.setVelocity(projectile.getVelocity().multiply(1.01 + variant * 0.015));
                    }

                    break;
                }
        }
    }

    @EventHandler
    public static void onEntityFall(EntityDamageEvent entityDamageEvent) {
        if (!entityDamageEvent.isCancelled() && entityDamageEvent.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(entityDamageEvent.getEntity());

            for (Capability possibleGene : entityCapabilities)
                if (possibleGene instanceof StrengthGene) {
                    byte variant = ((Gene) possibleGene).getVariant();

                    if (variant != 0) {
                        double newDamage = entityDamageEvent.getDamage() - variant * 0.4;

                        if (newDamage < 0) {
                            entityDamageEvent.setCancelled(true);

                        } else
                            entityDamageEvent.setDamage(newDamage);
                    }

                    break;
                }
        }
    }



    @Override
    public void onAssignment(Entity entity) {
        if (entity instanceof LivingEntity && variant != 0)
            increaseAttributeValues((LivingEntity) entity, variant * 0.03);
    }

    @Override
    public void onRevoke(Entity entity) {
        if (entity instanceof LivingEntity && variant != 0)
            revertAttributeValues((LivingEntity) entity);
    }

    /**
     * Increase the values of the attributes an entity has
     *
     * @param livingEntity The entity to increase the attributes of.
     * @param multiplier The amount to scale by and add back to the original value.
     */
    private static void increaseAttributeValues(LivingEntity livingEntity, double multiplier) {
        AttributeInstance movementSpeed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance attackDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        AttributeInstance armor = livingEntity.getAttribute(Attribute.GENERIC_ARMOR);
        AttributeInstance knockbackResist = livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance attackSpeed = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        AttributeInstance armorToughness = livingEntity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        AttributeInstance flyingSpeed = livingEntity.getAttribute(Attribute.GENERIC_FLYING_SPEED);

        AttributeHelper.addModifierSafely(movementSpeed, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(movementSpeed, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        AttributeHelper.addModifierSafely(attackDamage, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(attackDamage, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        AttributeHelper.addModifierSafely(armor, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(armor, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        AttributeHelper.addModifierSafely(knockbackResist, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(knockbackResist, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        AttributeHelper.addModifierSafely(maxHealth, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(maxHealth, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        AttributeHelper.addModifierSafely(attackSpeed, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(attackSpeed, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        AttributeHelper.addModifierSafely(armorToughness, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(armorToughness, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        AttributeHelper.addModifierSafely(flyingSpeed, new AttributeModifier("G_ST-A", 0.001, AttributeModifier.Operation.ADD_NUMBER));
        AttributeHelper.addModifierSafely(flyingSpeed, new AttributeModifier("G_ST-M1", multiplier, AttributeModifier.Operation.ADD_SCALAR));
    }

    /**
     * Gets rid of the attribute modifiers applied by the strength gene.
     *
     * @param livingEntity The entity to revert the attributes of.
     */
    private static void revertAttributeValues(LivingEntity livingEntity) {
        AttributeInstance movementSpeed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance attackDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        AttributeInstance armor = livingEntity.getAttribute(Attribute.GENERIC_ARMOR);
        AttributeInstance knockbackResist = livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance attackSpeed = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        AttributeInstance armorToughness = livingEntity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        AttributeInstance flyingSpeed = livingEntity.getAttribute(Attribute.GENERIC_FLYING_SPEED);

        AttributeHelper.removeModifiers(movementSpeed, "G_ST-A", true);
        AttributeHelper.removeModifiers(movementSpeed, "G_ST-M1", true);
        AttributeHelper.removeModifiers(attackDamage, "G_ST-A", true);
        AttributeHelper.removeModifiers(attackDamage, "G_ST-M1", true);
        AttributeHelper.removeModifiers(armor, "G_ST-A", true);
        AttributeHelper.removeModifiers(armor, "G_ST-M1", true);
        AttributeHelper.removeModifiers(knockbackResist, "G_ST-A", true);
        AttributeHelper.removeModifiers(knockbackResist, "G_ST-M1", true);
        AttributeHelper.removeModifiers(maxHealth, "G_ST-A", true);
        AttributeHelper.removeModifiers(maxHealth, "G_ST-M1", true);
        AttributeHelper.removeModifiers(attackSpeed, "G_ST-A", true);
        AttributeHelper.removeModifiers(attackSpeed, "G_ST-M1", true);
        AttributeHelper.removeModifiers(armorToughness, "G_ST-A", true);
        AttributeHelper.removeModifiers(armorToughness, "G_ST-M1", true);
        AttributeHelper.removeModifiers(flyingSpeed, "G_ST-A", true);
        AttributeHelper.removeModifiers(flyingSpeed, "G_ST-M1", true);
    }
}
