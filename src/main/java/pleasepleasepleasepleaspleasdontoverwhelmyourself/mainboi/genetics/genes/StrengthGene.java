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
import org.bukkit.event.entity.EntityShootBowEvent;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics.Gene;

import java.util.Collection;
import java.util.Set;

/**
 * Gene allele frequency:
 *  rr: 0.80644187772, rR/Rr: 0.12829694323, RR: 0.02537117903
 * Gene frequency:
 *  100%
 *
 * r = normal speed, strength, and jump height.
 * R = faster speed, more strength and higher jump height.
 */
public class StrengthGene extends Gene implements Listener {
    private final byte variantCode;

    public StrengthGene(byte variantCode) {
        this.variantCode = variantCode;
    }

    @Override
    public byte getVariantCode() {
        return variantCode;
    }

    @Override
    public String getCapabilityName() {
        return "G_strength-" + variantCode;
    }



    @EventHandler
    public static void onEntityJump(EntityJumpEvent entityJumpEvent) {
        if (!entityJumpEvent.isCancelled()) {
            LivingEntity livingEntity = entityJumpEvent.getEntity();
            Set<Capability> livingEntityCapabilities = CapabilitiesCore.getCapabilities(livingEntity);

            for (Capability possibleGene : livingEntityCapabilities)
                if (possibleGene instanceof StrengthGene) {
                    byte variantCode = ((Gene) possibleGene).getVariantCode();

                    if (variantCode == 0b10)
                        livingEntity.setVelocity(livingEntity.getVelocity());

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
                    byte variantCode = ((Gene) possibleGene).getVariantCode();

                    if (variantCode == 0b10)
                        player.setVelocity(player.getVelocity());

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
                    byte variantCode = ((Gene) possibleGene).getVariantCode();

                    if (variantCode == 0b01) {
                        Entity projectile = entityShootBowEvent.getProjectile();
                        projectile.setVelocity(projectile.getVelocity().multiply(1.03));

                    } else if (variantCode == 0b10) {
                        Entity projectile = entityShootBowEvent.getProjectile();
                        projectile.setVelocity(projectile.getVelocity().multiply(1.07));
                    }

                    break;
                }
        }
    }



    @Override
    public void onAssignment(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity;

            if (variantCode == 0b01) {
                livingEntity = (LivingEntity) entity;
                increaseAttributeValues(livingEntity, 0.03);

            } else if (variantCode == 0b10) {
                livingEntity = (LivingEntity) entity;
                increaseAttributeValues(livingEntity, 0.07);
            }
        }
    }

    @Override
    public void onRevoke(Entity entity) {
        if (entity instanceof LivingEntity && (variantCode == 0b01 || variantCode == 0b10)) {
            LivingEntity livingEntity = (LivingEntity) entity;
            revertAttributeValues(livingEntity);
        }
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

        addAttributeModifiers(movementSpeed, multiplier);
        addAttributeModifiers(attackDamage, multiplier);
        addAttributeModifiers(armor, multiplier);
        addAttributeModifiers(knockbackResist, multiplier);
        addAttributeModifiers(maxHealth, multiplier);
        addAttributeModifiers(attackSpeed, multiplier);
        addAttributeModifiers(armorToughness, multiplier);
        addAttributeModifiers(flyingSpeed, multiplier);
    }

    /**
     * Adds the attribute modifiers of the strength gene to an attribute.
     *
     * @param attribute The attribute to add the modifiers to.
     * @param multiplier The amount to scale by and add back to the original value.
     */
    private static void addAttributeModifiers(AttributeInstance attribute, double multiplier) {
        if (attribute != null) {
            attribute.addModifier(new AttributeModifier("G_ST-A", 0.003, AttributeModifier.Operation.ADD_NUMBER));
            attribute.addModifier(new AttributeModifier("G_ST-M", multiplier, AttributeModifier.Operation.ADD_SCALAR));
        }
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

        revertAttributeModifiers(movementSpeed);
        revertAttributeModifiers(attackDamage);
        revertAttributeModifiers(armor);
        revertAttributeModifiers(knockbackResist);
        revertAttributeModifiers(maxHealth);
        revertAttributeModifiers(attackSpeed);
        revertAttributeModifiers(armorToughness);
        revertAttributeModifiers(flyingSpeed);
    }

    private static final AttributeModifier[] modifierBuffer = new AttributeModifier[2];

    /**
     * Gets rid of the attribute modifiers that the strength gene adds from attributes.
     *
     * @param attribute The attribute to revert the modifiers of.
     */
    private static void revertAttributeModifiers(AttributeInstance attribute) {
        if (attribute != null) {
            Collection<AttributeModifier> attributeModifiers = attribute.getModifiers();

            if (!attributeModifiers.isEmpty()) {
                for (AttributeModifier attributeModifier : attributeModifiers) {
                    String attributeName = attributeModifier.getName();

                    if (attributeName.equals("G_ST-A")) {
                        modifierBuffer[0] = attributeModifier;

                    } else if (attributeName.equals("G_ST-M"))
                        modifierBuffer[1] = attributeModifier;
                }

                attribute.removeModifier(modifierBuffer[0]);
                attribute.removeModifier(modifierBuffer[1]);
            }
        }
    }
}
