package com.github.moincraft.cloudnet.module.platform.node;

import com.github.moincraft.cloudnet.module.platform.RPCSample;
import eu.cloudnetservice.driver.network.rpc.factory.RPCFactory;
import eu.cloudnetservice.driver.network.rpc.handler.RPCHandlerRegistry;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * An implementation of the {@link RPCSample} interface that is used on the node side.
 * On the node, RPCs are actually handled, meaning that the implementation of the RPCs is done here.
 */
@Singleton
public class NodeRPCSample implements RPCSample {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeRPCSample.class);

    /**
     * The sample string that is used in the RPCs.
     * Can be set using the {@link #setSampleString(String)} method and retrieved using the {@link #getSampleString()} method.
     */
    private String sampleString;

    @Inject
    public NodeRPCSample(
            @Nonnull RPCFactory rpcFactory,
            @Nonnull RPCHandlerRegistry rpcHandlerRegistry
    ) {
        // Register the RPC handler
        var rpcHandler = rpcFactory.newRPCHandlerBuilder(RPCSample.class).targetInstance(this).build();
        rpcHandlerRegistry.registerHandler(rpcHandler);
    }

    @Override
    public String getSampleString() {
        return this.sampleString;
    }

    @Override
    public void setSampleString(String sampleString) {
        LOGGER.info("Setting sample string to {}", sampleString);
        this.sampleString = sampleString;
    }
}
