package com.github.moincraft.cloudnet.module.platform.node;

import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * A module that uses the {@link NodeRPCSample} to handle RPC calls.
 */
@Singleton
public class RPCSampleModule extends DriverModule {
    private NodeRPCSample rpc;

    /***
     * Initializes the module with the {@link NodeRPCSample} instance.
     * @param rpc the {@link NodeRPCSample} instance
     */
    @ModuleTask(lifecycle = ModuleLifeCycle.LOADED)
    public void initialize(@Nonnull NodeRPCSample rpc) {
        this.rpc = rpc;
    }

    /***
     * Is called when the module is stopped and releases acquired resources.
     */
    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void stop() {
        this.rpc = null;
    }
}
