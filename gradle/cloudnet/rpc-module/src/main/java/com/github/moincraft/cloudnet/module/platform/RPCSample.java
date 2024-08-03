package com.github.moincraft.cloudnet.module.platform;

import javax.annotation.Nullable;

/**
 * This interface is used to demonstrate the RPC feature of CloudNet.
 * <p>
 * As a developer, you define the methods you want to offer to RPC clients.
 */
public interface RPCSample {
    /**
     * Returns the sample string set by {@link #setSampleString(String)}.
     * @return The sample string.
     */
    @Nullable
    String getSampleString();

    /**
     * Sets and stores a sample string for later retrieval.
     * @param sampleString The sample string to store.
     */
    void setSampleString(@Nullable String sampleString);
}
