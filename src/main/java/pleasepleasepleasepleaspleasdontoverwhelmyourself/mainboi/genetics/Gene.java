package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics;

import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;

/**
 * The base class for genes.
 * Extend this class to get the necessary functions for genes.
 */
public abstract class Gene extends Capability {
    protected byte variant;

    public Gene(String extraData) {
        super(extraData);

        try {
            variant = Byte.parseByte(extraData);

        } catch (NumberFormatException ignored) {
            variant = 0;
        }
    }

    @Override
    public String getExtraData() {
        return String.valueOf(variant);
    }

    @Override
    public boolean isVolatile() {
        return true;
    }



    /**
     * Gets the variant, a number that the gene uses to figure out which effects should be applied.
     *
     * @return The variant.
     */
    public byte getVariant() {
        return variant;
    }
}
