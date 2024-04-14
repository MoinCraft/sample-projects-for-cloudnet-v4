package com.github.moincraft.bungeecord.plugin;

import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Command;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Dependency;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.api.plugin.Plugin;

@Singleton
@PlatformPlugin(
        platform = "bungeecord",
        name = "SamplePlugin",
        version = "1.0.0",
        description = "A sample plugin for Bungeecord",
        authors = "MoinCraft",
        pluginFileNames = {"bungee.yml"},
        dependencies = {
                @Dependency(name = "CloudNet-Bridge")
        },
        commands = {
                @Command(
                        name = "sampleproxy",
                        description = "A sample command on Bungeecord",
                        usage = "/sampleproxy [player]"
                )
        }
)
public class SamplePlugin implements PlatformEntrypoint {

    private final Plugin plugin;

    @Inject
    public SamplePlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        this.plugin.getLogger().info("Sample plugin loaded");
    }

    @SuppressWarnings("unused")
    @Inject
    private void registerCommands(SampleCommand sampleCommand) {
        this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, sampleCommand);
    }

    @Override
    public void onDisable() {
        this.plugin.getLogger().info("Sample plugin disabled");
    }
}
