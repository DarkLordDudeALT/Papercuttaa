package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.elites;

import org.bukkit.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// TODO Add the freeze effect, and have the ice bomb apply it.
// TODO Have ice bomb apply knockback.
// TODO Make ice bomb's effect area a sphere.

/**
 * The ice elite from Risk of Rain.
 *
 * Ice elites slow entities they hit.
 * Ice elites slow entities they shoot.
 * Ice elites leave behind a ice bomb, which detonates for 150% of their damage after 2 seconds.
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

            if (attacker.getScoreboardTags().contains("COP_IE-P")) {
                livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 3, false, true, true));

            } else {
                Set<Capability> attackerCapabilities = CapabilitiesCore.getCapabilities(attacker);

                for (Capability capability : attackerCapabilities)
                    if (capability instanceof IceEliteCapability) {
                        livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 3, false, true, true));
                        break;
                    }
            }
        }
    }

    /**
     * Marks all arrows shot by ice elites.
     */
    @EventHandler
    public static void onEntityShootBow(EntityShootBowEvent entityShootBowEvent) {
        if (!entityShootBowEvent.isCancelled()) {
            Entity entity = entityShootBowEvent.getEntity();
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(entity);

            for (Capability capability : entityCapabilities)
                if (capability instanceof IceEliteCapability) {
                    Entity projectile = entityShootBowEvent.getProjectile();

                    projectile.addScoreboardTag("COP_IE-P");

                    if (!(entity instanceof Player) && projectile instanceof AbstractArrow) {
                        AbstractArrow arrow = (AbstractArrow) projectile;
                        arrow.setDamage(arrow.getDamage() * 2);
                    }

                    break;
                }
        }
    }

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent entityDeathEvent) {
        if (!entityDeathEvent.isCancelled()) {
            LivingEntity livingEntity = entityDeathEvent.getEntity();
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(livingEntity);

            for (Capability capability : entityCapabilities)
                if (capability instanceof IceEliteCapability) {
                    Location newLocation = livingEntity.getLocation();
                    newLocation.setY(newLocation.getY() + livingEntity.getHeight() / 2);

                    ArmorStand armorStand = (ArmorStand) livingEntity.getWorld().spawnEntity(newLocation, EntityType.ARMOR_STAND);

                    armorStand.setMarker(true);
                    armorStand.setVisible(false);
                    armorStand.setCanTick(false);


                    AttributeInstance attackDamage = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                    double damage;

                    if (attackDamage != null) {
                        damage = attackDamage.getValue();
                        damage *= 1.5;

                        if (livingEntity.hasPotionEffect(PotionEffectType.WEAKNESS))
                            damage *= 0.6;

                    } else
                        damage = 2;


                    IceBombCapability.addDamageSource(livingEntity);
                    CapabilitiesCore.assignCapability(armorStand, new IceBombCapability("0," + damage + "," + livingEntity.getUniqueId().toString()));
                }
        }
    }



    /**
     * The ice bomb that is left behind when ice elites die.
     */
    public static class IceBombCapability extends Capability {
        private static final HashMap<UUID, LivingEntity> damageSources = new HashMap<>();

        /**
         * Adds a damage source (entity) to a list that the ice bomb can use to tell the game who it belongs too.
         *
         * @param livingEntity The entity to add as a damage source.
         */
        static void addDamageSource(LivingEntity livingEntity) {
            if (livingEntity != null)
                damageSources.put(livingEntity.getUniqueId(), livingEntity);
        }



        int age;
        double damage;
        UUID source;

        public IceBombCapability(String extraData) {
            super(extraData);

            String[] splitExtraData = extraData.split(",", 3);

            try {
                age = Integer.parseInt(splitExtraData[0]);

            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                age = 0;
            }

            try {
                damage = Double.parseDouble(splitExtraData[1]);

            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                damage = 2;
            }

            try {
                source = UUID.fromString(splitExtraData[2]);

            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
                source = null;
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
            return source != null ? age + "," + damage + "," + source.toString() : age + "," + damage + ",";
        }



        @Override
        public void runCapability(Entity entity) {
            World world = entity.getWorld();
            Location entityLocation = entity.getLocation();

            // Them: Why are you spawning so many particles, with more than a couple ice bombs at a time it will lag badly, REEEEEEEE!!!11!
            // Me: Hahaha particle go brrrrr
            double angle = 0.314159265358979323846 * age;
            double verticalRadius = (age - 20) * 0.15;
            double horizontalRadius = Math.sqrt(9 - verticalRadius * verticalRadius);

            double particleX = horizontalRadius * Math.cos(angle) - horizontalRadius * Math.sin(angle);
            double particleZ = horizontalRadius * Math.sin(angle) + horizontalRadius * Math.cos(angle);

            world.spawnParticle(Particle.CLOUD, particleX + entityLocation.getX(), verticalRadius + entityLocation.getY(), particleZ + entityLocation.getZ(), 1, 0, 0, 0, 0);
            world.spawnParticle(Particle.CLOUD, entityLocation.getX() - particleX, verticalRadius + entityLocation.getY(), entityLocation.getZ() - particleZ, 1, 0, 0, 0, 0);

            if (age >= 40) {
                // Explosion effect.
                world.spawnParticle(Particle.CLOUD, entityLocation.getX(), entityLocation.getY(), entityLocation.getZ(), 40, 0, 0, 0, 0.35);

                // Applies damage.
                if (damage != 0) {
                    List<Entity> victims = entity.getNearbyEntities(3, 3, 3);
                    Entity sourceEntity = null;
                    boolean sourceFound = false;

                    // Gets source of ice bomb.
                    if (source != null) {
                        sourceEntity = damageSources.get(source);

                        if (sourceEntity != null) {
                            damageSources.remove(source);
                            sourceFound = true;

                        } else {
                            sourceEntity = Bukkit.getEntity(source);

                            if (sourceEntity != null)
                                sourceFound = true;
                        }
                    }

                    // Adds damage, calls events.
                    if (sourceFound) {
                        for (Entity victim : victims)
                            if (victim instanceof LivingEntity)
                                ((LivingEntity) victim).damage(damage, sourceEntity);

                    } else
                        for (Entity victim : victims)
                            if (victim instanceof LivingEntity)
                                ((LivingEntity) victim).damage(damage);
                }

                // Removes ice bomb entity.
                if (entity instanceof Player) {
                    ((Player) entity).setHealth(0);

                } else
                    entity.remove();

            } else
                age++;
        }
    }
}
