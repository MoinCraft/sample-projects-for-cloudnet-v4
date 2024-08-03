package com.github.moincraft.cloudnet.module.platform.bukkit;

import com.github.moincraft.cloudnet.module.platform.PlatformRPCSample;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

/**
 * This class is the entrypoint for the Bukkit platform.
 * The constructor requests an instance of the {@link PlatformRPCSample} class for later RPC calls.
 * <p>
 * In the {@link #onLoad()} method, the {@link #printSampleString(PlatformRPCSample)} method is called, which
 * in turn sets a sample string and prints it to the console of the Bukkit server.
 */
@Singleton
@PlatformPlugin(
        platform = "bukkit",
        name = "RPCSample",
        pluginFileNames = "plugin.yml",
        version = "1.0")
public class RPCSamplePlugin implements PlatformEntrypoint {

    private final PlatformRPCSample rpcSample;
    private final JavaPlugin plugin;

    @Inject
    public RPCSamplePlugin(
            PlatformRPCSample rpcSample,
            JavaPlugin plugin
    ) {
        this.rpcSample = rpcSample;
        this.plugin = plugin;
    }

    public void onLoad() {
        this.printSampleString(this.rpcSample);
    }

    public void printSampleString(@Nonnull PlatformRPCSample rpcSample) {
        rpcSample.setSampleString("Hello CloudNet");
        this.plugin.getLogger().info(rpcSample.getSampleString());
    }
}
