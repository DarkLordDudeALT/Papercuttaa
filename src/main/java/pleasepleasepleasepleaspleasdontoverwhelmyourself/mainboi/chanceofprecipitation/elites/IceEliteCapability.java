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
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.statuseffects.FreezeEffect;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.AttributeHelper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        if (!entityDamageByEntityEvent.isCancelled()) {
            Entity victim = entityDamageByEntityEvent.getEntity();

            if (victim instanceof LivingEntity) {
                Entity attacker = entityDamageByEntityEvent.getDamager();
                LivingEntity livingVictim = (LivingEntity) entityDamageByEntityEvent.getEntity();

                if (attacker.getScoreboardTags().contains("COP_IE-P")) {
                    livingVictim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 3, false, true, true));

                    if (attacker instanceof Player)
                        attacker.removeScoreboardTag("COP_IE-P");

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
    }

    /**
     * Marks all arrows shot by ice elites.
     */
    @EventHandler
    public static void onEntityShootBow(EntityShootBowEvent entityShootBowEvent) {
        if (!entityShootBowEvent.isCancelled()) {
            LivingEntity livingEntity = entityShootBowEvent.getEntity();
            Set<Capability> entityCapabilities = CapabilitiesCore.getCapabilities(livingEntity);

            for (Capability capability : entityCapabilities)
                if (capability instanceof IceEliteCapability) {
                    Entity projectile = entityShootBowEvent.getProjectile();

                    projectile.addScoreboardTag("COP_IE-P");

                    if (!(livingEntity instanceof Player) && projectile instanceof AbstractArrow) {
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


                    CapabilitiesCore.assignCapability(armorStand, new IceBombCapability("0," + damage, livingEntity));
                }
        }
    }



    /**
     * The ice bomb that is left behind when ice elites die.
     *
     * It causes damage and freezes its targets for 2 seconds.
     */
    public static class IceBombCapability extends Capability {
        int age;
        double damage;
        UUID sourceUUID;
        Entity sourceEntity;

        public IceBombCapability(String extraData, Entity sourceEntity) {
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
                sourceUUID = UUID.fromString(splitExtraData[2]);

            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
                sourceUUID = null;
            }


            this.sourceEntity = sourceEntity;

            if (sourceUUID == null && sourceEntity != null)
                sourceUUID = sourceEntity.getUniqueId();
        }

        @Override
        public Capability useConstructor(String extraData) {
            return new IceBombCapability(extraData, null);
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
            return sourceUUID != null ? age + "," + damage + "," + sourceUUID.toString() : age + "," + damage;
        }



        @Override
        public void runCapability(Entity entity) {
            World world = entity.getWorld();
            Location entityLocation = entity.getLocation();

            // Them: Why are you spawning so many particles, with more than a couple ice bombs at a time it will lag badly, REEEEEEEE!!!11!
            // Me: Hahaha particle go brrrrr
            double angle = 0.31415926535897932385 * age;
            double verticalRadius = (age - 20) * 0.15;
            double horizontalRadius = Math.sqrt(9 - verticalRadius * verticalRadius);

            double particleX = horizontalRadius * Math.cos(angle) - horizontalRadius * Math.sin(angle);
            double particleZ = horizontalRadius * Math.sin(angle) + horizontalRadius * Math.cos(angle);

            world.spawnParticle(Particle.CLOUD, particleX + entityLocation.getX(), verticalRadius + entityLocation.getY(), particleZ + entityLocation.getZ(), 1, 0, 0, 0, 0);
            world.spawnParticle(Particle.CLOUD, entityLocation.getX() - particleX, verticalRadius + entityLocation.getY(), entityLocation.getZ() - particleZ, 1, 0, 0, 0, 0);

            if (age >= 40) {
                // Explosion effect.
                world.spawnParticle(Particle.CLOUD, entityLocation.getX(), entityLocation.getY(), entityLocation.getZ(), 40, 0, 0, 0, 0.35);

                // Damage stuff.
                if (damage != 0) {
                    List<Entity> victims = entity.getNearbyEntities(3, 3, 3);
                    Entity sourceEntity = null;
                    boolean sourceFound = false;

                    // Gets source of ice bomb.
                    if (this.sourceEntity == null) {
                        sourceEntity = Bukkit.getEntity(sourceUUID);

                        if (sourceEntity != null)
                            sourceFound = true;

                    } else
                        sourceFound = true;

                    // Adds damage, applies freeze.
                    for (Entity victim : victims)
                        if (victim instanceof LivingEntity) {
                            LivingEntity livingVictim = (LivingEntity) victim;

                            if (sourceFound) {
                                livingVictim.damage(damage, sourceEntity);

                            } else
                                livingVictim.damage(damage);

                            if (!livingVictim.isDead()) {
                                if (!(livingVictim instanceof Boss)) {
                                    boolean validEntity = true;

                                    if (livingVictim instanceof Player) {
                                        GameMode playerGameMode = ((Player) livingVictim).getGameMode();

                                        if (playerGameMode.equals(GameMode.CREATIVE) || playerGameMode.equals(GameMode.SPECTATOR))
                                            validEntity = false;
                                    }

                                    if (validEntity)
                                        CapabilitiesCore.assignCapability(livingVictim, new FreezeEffect("30,0,false,true,false"));
                                }

                            } else if (livingVictim instanceof Player)
                                livingVictim.addScoreboardTag("COP:SE_FE-PD");
                        }
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
