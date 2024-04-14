package com.github.moincraft.bukkit.plugin;

import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Singleton
public class SampleCommand implements TabExecutor {

    private final JavaPlugin plugin;

    /**
     * The service info holder allows us to get the current service information of the CloudNet service
     * where this plugin is running on
     */
    private final ServiceInfoHolder serviceInfoHolder;

    @Inject
    public SampleCommand(JavaPlugin plugin,
                         ServiceInfoHolder serviceInfoHolder) {
        this.plugin = plugin;
        this.serviceInfoHolder = serviceInfoHolder;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final var builder = new ComponentBuilder();
        final var serviceInfoSnapshot = this.serviceInfoHolder.lastServiceInfo();

        builder.append("Hello, ").color(ChatColor.YELLOW);
        builder.append(sender.getName()).color(ChatColor.AQUA);
        builder.append("!").color(ChatColor.YELLOW);
        builder.append("\n").reset();

        builder.append("This command was invoked via /").color(ChatColor.YELLOW);
        builder.append(label).color(ChatColor.RED);
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

        if (sender instanceof Player player) {
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
        builder.append(this.plugin.getName()).color(ChatColor.AQUA);
        builder.append("\n").reset();

        builder.append("The version of this plugin is ").color(ChatColor.YELLOW);
        builder.append(this.plugin.getDescription().getVersion()).color(ChatColor.AQUA);
        builder.append("\n").reset();

        builder.append("The service id of this plugin is ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.name()).color(ChatColor.RED);
        builder.append("\n").reset();

        builder.append("The server is running on the address ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.address().toString()).color(ChatColor.RED);
        builder.append("\n").reset();

        builder.append("The server is in the life cycle state ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.lifeCycle().name()).color(ChatColor.RED);
        builder.append("\n").reset();

        builder.append("The server holds the properties ").color(ChatColor.YELLOW);
        builder.append(serviceInfoSnapshot.propertyHolder().toString()).color(ChatColor.DARK_RED);

        sender.spigot().sendMessage(builder.build());
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Return the name of all players, if this is the first argument
        if (args.length != 1) {
            return null;
        }
        return sender.getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> name.startsWith(args[0])).toList();
    }
}
