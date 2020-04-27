package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.helpers;

import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

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
}
