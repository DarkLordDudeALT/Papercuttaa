package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.chanceofprecipitation.statuseffects;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.StatusEffectCapability;
import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers.AttributeHelper;

// TODO Have freeze effect execute mobs under health threshold.
// TODO Prevent frozen mobs from jumping.
// TODO Prevent frozen mobs from attacking.
// TODO Prevent frozen players from using things.

// TODO Have the freeze effect save the rotation on extra data.

// TODO Add a sound effect upon being unfrozen.
// TODO Add a particle effect upon being unfrozen.

public class FreezeEffect extends StatusEffectCapability {
    private float initialEntityYaw;
    private float initialEntityPitch;

    public FreezeEffect(String extraData) {
        super(extraData);
    }

    @Override
    public Capability useConstructor(String extraData) {
        return new FreezeEffect(extraData);
    }

    @Override
    public String getCapabilityName() {
        return "COP:SE_freeze";
    }



    @Override
    public void runCapability(Entity entity) {
        super.runCapability(entity);

        // Prevents entities from rotating.
        Location entityLocation = entity.getLocation();

        if (entityLocation.getYaw() != initialEntityYaw || entityLocation.getPitch() != initialEntityPitch) {
            entityLocation.setYaw(initialEntityYaw);
            entityLocation.setPitch(initialEntityPitch);
            entity.teleport(entityLocation);
        }
    }

    @Override
    public void onAssignment(Entity entity) {
        super.onAssignment(entity);

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            AttributeInstance movementSpeed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            AttributeInstance flyingSpeed = livingEntity.getAttribute(Attribute.GENERIC_FLYING_SPEED);

            AttributeHelper.addModifierSafely(movementSpeed, new AttributeModifier("COP_FR-M2", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            AttributeHelper.addModifierSafely(flyingSpeed, new AttributeModifier("COP_FR-M2", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }

        Location entityLocation = entity.getLocation();
        initialEntityYaw = entityLocation.getYaw();
        initialEntityPitch = entityLocation.getPitch();
    }

    @Override
    public void onRevoke(Entity entity) {
        super.onRevoke(entity);

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            AttributeInstance movementSpeed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            AttributeInstance flyingSpeed = livingEntity.getAttribute(Attribute.GENERIC_FLYING_SPEED);

            AttributeHelper.removeModifiers(movementSpeed, "COP_FR-M2", true);
            AttributeHelper.removeModifiers(flyingSpeed, "COP_FR-M2", true);
        }
    }
}
