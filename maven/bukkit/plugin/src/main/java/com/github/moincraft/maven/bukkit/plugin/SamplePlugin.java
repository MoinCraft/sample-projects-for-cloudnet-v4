package com.github.moincraft.maven.bukkit.plugin;

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
        this.registerCommands();
    }

    private void registerCommands() {
        final var samplePluginCommand = this.plugin.getCommand("sample");
        if (samplePluginCommand != null) {
            final var sampleCommand = new SampleCommand(this.plugin);
            samplePluginCommand.setExecutor(sampleCommand);
            samplePluginCommand.setTabCompleter(sampleCommand);
        }
    }

    @Override
    public void onDisable() {
        this.plugin.getLogger().info("Sample plugin disabled");
    }
}
