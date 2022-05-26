package dev.buildtool.satako;

import java.util.UUID;

/**
 * Used for cross-mod ally identification
 */
public interface Ownable {
    /**
     * @param other target
     * @return whether the target is an ally
     */
    default boolean isAlly(Ownable other) {
        return other.getOwnerUUID() != null && other.getOwnerUUID().equals(getOwnerUUID());
    }

    UUID getOwnerUUID();
}
