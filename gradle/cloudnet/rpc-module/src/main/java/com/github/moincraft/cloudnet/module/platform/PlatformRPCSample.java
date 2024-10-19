package com.github.moincraft.cloudnet.module.platform;

import eu.cloudnetservice.driver.network.NetworkClient;
import eu.cloudnetservice.driver.network.rpc.factory.RPCFactory;
import eu.cloudnetservice.driver.network.rpc.RPCSender;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * This class is an implementation of the {@link RPCSample} interface that is used to send RPC calls to the node module.
 * All platforms can, when possible, share the same implementation of the {@link RPCSample} interface.
 */
@Singleton
public class PlatformRPCSample implements RPCSample {

    /**
     * The RPC sender that is used to send RPC calls to the node module.
     */
    private final RPCSender rpcSender;

    /**
     * Constructs a new instance of the {@link PlatformRPCSample} class and creates a new RPC sender.
     * @param rpcFactory The RPC factory that is used to create the RPC sender.
     * @param networkClient The network client that is used to send the RPC calls.
     */
    @Inject
    public PlatformRPCSample(
        @Nonnull RPCFactory rpcFactory,
        @Nonnull NetworkClient networkClient
    ) {
        this.rpcSender = rpcFactory.newRPCSenderBuilder(RPCSample.class).targetComponent(networkClient).build();
    }

    @Override
    public String getSampleString() {
        return this.rpcSender.invokeMethod("getSampleString").fireSync();
    }

    @Override
    public void setSampleString(String sampleString) {
        this.rpcSender.invokeMethod("setSampleString", sampleString).fireSync();
    }
}
