package com.github.moincraft.bukkit.plugin;

import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Command;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Dependency;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
@PlatformPlugin(
        platform = "bukkit",
        pluginFileNames = "plugin.yml",
        name = "SamplePlugin",
        version = "1.0.0",
        description = "A sample plugin for Bukkit",
        authors = "MoinCraft",
        dependencies = {
                @Dependency(name = "CloudNet-Bridge")
        },
        commands = {
                @Command(
                        name = "sample",
                        description = "A sample command",
                        usage = "/sample [player]"
                )
        }
)
public class SamplePlugin implements PlatformEntrypoint {

    private final JavaPlugin plugin;

    @Inject
    public SamplePlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        this.plugin.getLogger().info("Sample plugin loaded");
    }

    // Methods annotated with @Inject will be called after the plugin has been enabled
    @Inject
    private void registerCommands(
            SampleCommand sampleCommand
    ) {
        final var samplePluginCommand = this.plugin.getCommand("sample");
        if (samplePluginCommand != null) {
            samplePluginCommand.setExecutor(sampleCommand);
            samplePluginCommand.setTabCompleter(sampleCommand);
        }

    }

    @Override
    public void onDisable() {
        this.plugin.getLogger().info("Sample plugin disabled");
    }
}
