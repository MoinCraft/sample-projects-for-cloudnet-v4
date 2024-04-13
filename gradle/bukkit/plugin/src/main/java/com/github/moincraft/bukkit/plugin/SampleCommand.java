package com.github.moincraft.bukkit.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SampleCommand implements TabExecutor {

    private final JavaPlugin plugin;

    public SampleCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Print "Hello, <player>! This command was invoked via /<label> with argument <arg0>" if the command is executed by a player.
        // Print "Hello, <player>! This command was invoked via /<label>" if the command is executed by a player.
        // Print "Hello, console! This command was invoked via /<label>" if the command is executed by the console.
        if (args.length > 0) {
            sender.sendMessage("Hello, " + sender.getName() + "! This command was invoked via /" + label + " with argument " + args[0]);
            return true;
        } else if (sender instanceof Player player) {
            player.sendMessage("Hello, " + player.getName() + "! This command was invoked via /" + label);
            return true;
        } else {
            sender.sendMessage("Hello, console! This command was invoked via /" + label);
            sender.sendMessage("This plugin is on version " + this.plugin.getDescription().getVersion() + '!');
            return true;
        }
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
