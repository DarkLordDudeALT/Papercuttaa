package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.CapabilitiesCore;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.AttributeHelper;

import java.util.Set;

// TODO Have the ice bomb do damage.
// TODO Rework elite damage boost.

/**
 * The ice elite from Risk of Rain.
 *
 * Ice elites slow entities they hit.
 * Ice elites slow entities they shoot.
 */
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

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent entityDeathEvent) {
        if (!entityDeathEvent.isCancelled()) {
            Entity entity = entityDeathEvent.getEntity();
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(entity);

            for (Capability capability : entityCapabilities)
                if (capability instanceof IceEliteCapability) {
                    Location newLocation = entity.getLocation();
                    newLocation.setY(newLocation.getY() + entity.getHeight() / 2);

                    ArmorStand armorStand = (ArmorStand) entity.getWorld().spawnEntity(newLocation, EntityType.ARMOR_STAND);

                    armorStand.setMarker(true);
                    armorStand.setVisible(false);
                    armorStand.setCanTick(false);

                    CapabilitiesCore.assignCapability(armorStand, new IceBombCapability("0"));
                }
        }
    }



    public static class IceBombCapability extends Capability {
        int age;

        public IceBombCapability(String extraData) {
            super(extraData);

            try {
                age = Integer.parseInt(extraData);

            } catch (NumberFormatException ignored) {
                age = 0;
            }
        }

        @Override
        public Capability useConstructor(String extraData) {
            return new IceBombCapability(extraData);
        }

        @Override
        public String getCapabilityName() {
            return "COP_iceBomb";
        }

        @Override
        public boolean isVolatile() {
            return true;
        }

        @Override
        public String getExtraData() {
            return String.valueOf(age);
        }



        private static final double SMOL_PI = Math.PI * 0.1;

        @Override
        public void runCapability(Entity entity) {
            World world = entity.getWorld();
            Location entityLocation = entity.getLocation();

            // Them: Why are you spawning so many particles, with more than a couple ice bombs at a time it will lag badly!!!11!
            // Me: Hahaha particle go brrrrr
            double angle = SMOL_PI * age;
            double verticalRadius = (age - 20) * 0.15;
            double horizontalRadius = 3 - Math.abs(verticalRadius);

            double particleX = horizontalRadius * Math.cos(angle) - horizontalRadius * Math.sin(angle);
            double particleZ = horizontalRadius * Math.sin(angle) + horizontalRadius * Math.cos(angle);

            world.spawnParticle(Particle.CLOUD, particleX + entityLocation.getX(), verticalRadius + entityLocation.getY(), particleZ + entityLocation.getZ(), 1, 0, 0, 0, 0);
            world.spawnParticle(Particle.CLOUD, entityLocation.getX() - particleX, verticalRadius + entityLocation.getY(), entityLocation.getZ() - particleZ, 1, 0, 0, 0, 0);

            if (age >= 40) {
                world.spawnParticle(Particle.CLOUD, entityLocation.getX(), entityLocation.getY(), entityLocation.getZ(), 40, 0, 0, 0, 0.35);

                if (entity instanceof Player) {
                    ((Player) entity).setHealth(0);

                } else
                    entity.remove();

            } else
                age++;
        }
    }
}
