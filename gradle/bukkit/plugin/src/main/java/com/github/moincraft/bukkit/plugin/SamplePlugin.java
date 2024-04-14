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
                ),
                @Command(
                        name = "createservice",
                        description = "Creates a new service in CloudNet",
                        usage = "/createservice <task>"
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
    @SuppressWarnings("unused")
    @Inject
    private void registerCommands(
            SampleCommand sampleCommand,
            CreateServiceCommand createServiceCommand
    ) {
        final var samplePluginCommand = this.plugin.getCommand("sample");
        if (samplePluginCommand != null) {
            samplePluginCommand.setExecutor(sampleCommand);
            samplePluginCommand.setTabCompleter(sampleCommand);
        }

        final var createServiceCommandPluginCommand = this.plugin.getCommand("createservice");
        if (createServiceCommandPluginCommand != null) {
            createServiceCommandPluginCommand.setExecutor(createServiceCommand);
            createServiceCommandPluginCommand.setTabCompleter(createServiceCommand);
        }
    }

    @Override
    public void onDisable() {
        this.plugin.getLogger().info("Sample plugin disabled");
    }
}
