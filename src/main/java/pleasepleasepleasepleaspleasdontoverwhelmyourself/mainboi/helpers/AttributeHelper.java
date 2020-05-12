package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;

// TODO Allow create a variant of addModifierSafely() that can apply multiple modifiers at once. Do the the same for addHealthModifierAndScale().

/**
 * Code to help manage entity attributes.
 */
public final class AttributeHelper {
    /**
     * Adds an attribute modifier to an attribute only if there are no modifiers with the same name.
     *
     * @param attribute The attribute to add the modifier to.
     * @param attributeModifier The modifier to add.
     *
     * @return Whether or not the attribute was applied.
     */
    public static boolean addModifierSafely(AttributeInstance attribute, AttributeModifier attributeModifier) {
        if (attribute != null) {
            String attributeModifierName = attributeModifier.getName();
            boolean alreadyHasModifier = false;

            for (AttributeModifier possibleDuplicate : attribute.getModifiers())
                if (possibleDuplicate.getName().equals(attributeModifierName)) {
                    alreadyHasModifier = true;
                    break;
                }

            if (!alreadyHasModifier) {
                attribute.addModifier(attributeModifier);
                return true;
            }
        }

        return false;
    }

    /**
     * Removes all attribute modifiers that have the given name, or just one if removedOnce is true.
     *
     * @param attribute The attribute to remove the modifiers from.
     * @param modifierName The name of the modifiers to remove.
     * @param removeOnce Flag for whether or not to remove only one modifier.
     *
     * @return Whether or not the modifiers were removed.
     */
    public static boolean removeModifiers(AttributeInstance attribute, String modifierName, boolean removeOnce) {
        if (attribute != null) {
            boolean removedAModifier = false;

            for (AttributeModifier possibleDuplicate : attribute.getModifiers())
                if (possibleDuplicate.getName().equals(modifierName)) {
                    attribute.removeModifier(possibleDuplicate);
                    removedAModifier = true;

                    if (removeOnce)
                        break;
                }

            return removedAModifier;
        }

        return false;
    }

    /**
     * Removes all attribute modifiers that have the given name.
     *
     * @param attribute The attribute to remove the modifiers from.
     * @param modifierName The name of the modifiers to remove.
     *
     * @return Whether or not the modifiers were removed.
     */
    public static boolean removeModifiers(AttributeInstance attribute, String modifierName) {
        return removeModifiers(attribute, modifierName, false);
    }



    /**
     * Adds a modifier to a living entity's maxHealth attribute, if they have it, and scales their health accordingly.
     * Only runs if there is no attribute modifiers by the same name.
     *
     * @param livingEntity The living entity to add the modifier to.
     * @param maxHealthModifier The modifier to add to the living entity's max health.
     *
     * @return Whether or not the modifier and health increase were successful
     */
    public static boolean addHealthModifierAndScale(LivingEntity livingEntity, AttributeModifier maxHealthModifier) {
        AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (maxHealth != null) {
            String attributeModifierName = maxHealthModifier.getName();
            boolean alreadyHasModifier = false;

            for (AttributeModifier possibleDuplicate : maxHealth.getModifiers())
                if (possibleDuplicate.getName().equals(attributeModifierName)) {
                    alreadyHasModifier = true;
                    break;
                }

            if (!alreadyHasModifier) {
                double healthRatio = livingEntity.getHealth() / maxHealth.getValue();

                maxHealth.addModifier(maxHealthModifier);
                livingEntity.setHealth(healthRatio * maxHealth.getValue());

                return true;
            }
        }

        return false;
    }

    /**
     * Removes all max health modifiers of a given name that an entity has, or just one if removedOnce is true.
     * Scales the entity's afterward.
     *
     * @param livingEntity The living entity to remove attribute modifiers from.
     * @param modifierName The name of the modifiers to remove.
     * @param removeOnce Flag for whether or not to remove only one modifier.
     *
     * @return Whether or not the modifiers were removed.
     */
    public static boolean removeHealthModifiersAndScale(LivingEntity livingEntity, String modifierName, boolean removeOnce) {
        AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (maxHealth != null) {
            boolean removedAModifier = false;

            for (AttributeModifier possibleDuplicate : maxHealth.getModifiers())
                if (possibleDuplicate.getName().equals(modifierName)) {
                    maxHealth.removeModifier(possibleDuplicate);
                    removedAModifier = true;

                    if (removeOnce)
                        break;
                }

            if (removedAModifier) {
                double healthRatio = livingEntity.getHealth() / maxHealth.getValue();
                livingEntity.setHealth(healthRatio * maxHealth.getValue());

                return true;
            }
        }

        return false;
    }

    /**
     * Removes all max health modifiers of a given name that an entity has.
     * Scales the entity's afterward.
     *
     * @param livingEntity The living entity to remove attribute modifiers from.
     * @param modifierName The name of the modifiers to remove.
     *
     * @return Whether or not the modifiers were removed.
     */
    public static boolean removeHealthModifiersAndScale(LivingEntity livingEntity, String modifierName) {
        return removeHealthModifiersAndScale(livingEntity, modifierName, false);
    }
}
