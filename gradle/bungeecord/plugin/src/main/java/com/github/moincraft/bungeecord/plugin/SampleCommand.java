package com.github.moincraft.bungeecord.plugin;

import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SampleCommand extends Command implements TabExecutor {

    private final Plugin plugin;
    private final ServiceInfoHolder serviceInfoHolder;

    @Inject
    public SampleCommand(Plugin plugin,
                         ServiceInfoHolder serviceInfoHolder) {
        super("sampleproxy");
        this.plugin = plugin;
        this.serviceInfoHolder = serviceInfoHolder;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        final var builder = new ComponentBuilder();
        final var serviceInfoSnapshot = this.serviceInfoHolder.lastServiceInfo();

        builder.append("Hello, ").color(ChatColor.YELLOW);
        builder.append(sender.getName()).color(ChatColor.AQUA);
        builder.append("!").color(ChatColor.YELLOW);
        builder.append("\n").reset();

        builder.append("This command was invoked via on the proxy!").color(ChatColor.YELLOW);
        builder.append("\n").reset();

        if (args.length > 0) {
            builder.append("With the arguments ").color(ChatColor.YELLOW);
            builder.append("[").color(ChatColor.GRAY);
            builder.append("\"").color(ChatColor.GREEN);
            builder.append(String.join("\", \"", args)).color(ChatColor.GREEN);
            builder.append("\"").color(ChatColor.GREEN);
            builder.append("]").color(ChatColor.GRAY);
            builder.append("\n").reset();
        } else {
            builder.append("Without any arguments").color(ChatColor.GREEN);
            builder.append("\n").reset();
        }

        if (sender instanceof ProxiedPlayer player) {
            builder.append("The player calling this command is ").color(ChatColor.YELLOW);
            final var playerNameComponent = new TextComponent(player.getName());
            playerNameComponent.setColor(ChatColor.AQUA);
            playerNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
                            new Entity("minecraft:player", player.getUniqueId().toString(), new TextComponent(player.getName())
                            )
                    )
            );

            builder.append(playerNameComponent);
            builder.append("\n").reset();
        } else {
            builder.append("The command was called by the console").color(ChatColor.YELLOW);
            builder.append("\n").reset();
        }

        builder.append("The plugin of this command is ").color(ChatColor.YELLOW);
        builder.append(this.plugin.getDescription().getName()).color(ChatColor.AQUA);
        builder.append("\n").reset();

        builder.append("The version of this plugin is ").color(ChatColor.YELLOW);
        builder.append(this.plugin.getDescription().getVersion()).color(ChatColor.AQUA);
        builder.append("\n").reset();

        builder.append("The service id of this plugin is ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.name()).color(ChatColor.RED);
        builder.append("\n").reset();

        builder.append("The proxy is running on the address ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.address().toString()).color(ChatColor.RED);
        builder.append("\n").reset();

        builder.append("The proxy is in the life cycle state ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.lifeCycle().name()).color(ChatColor.RED);
        builder.append("\n").reset();

        builder.append("The proxy holds the properties ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.propertyHolder().toString()).color(ChatColor.DARK_RED);

        sender.sendMessage(builder.build());

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args.length != 1) {
            return List.of();
        }

        return this.plugin.getProxy().getPlayers().stream()
                .map(ProxiedPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
    }
}
