package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.genetics;

import pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.capabilities.Capability;

/**
 * The base class for genes.
 * Extend this class to get the necessary functions for genes.
 */
public abstract class Gene extends Capability {
    /**
     * The code used to identity which variant of the gene an instance is.
     *
     * @return The variant code.
     */
    public abstract byte getVariantCode();
}
