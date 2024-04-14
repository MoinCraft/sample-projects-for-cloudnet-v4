package com.github.moincraft.bukkit.plugin;

import eu.cloudnetservice.driver.provider.CloudServiceFactory;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceTask;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Singleton
public class CreateServiceCommand implements TabExecutor {

    /**
     * The service task provider allow us to get all the tasks that are available in CloudNet
     */
    private final ServiceTaskProvider serviceTaskProvider;

    /**
     * The cloud service factory allows us to create a new service in CloudNet
     */
    private final CloudServiceFactory cloudServiceFactory;

    @Inject
    public CreateServiceCommand(ServiceTaskProvider serviceTaskProvider,
                                CloudServiceFactory cloudServiceFactory) {
        this.serviceTaskProvider = serviceTaskProvider;
        this.cloudServiceFactory = cloudServiceFactory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            final var builder = new ComponentBuilder();
            builder.append("Please provide a service task name").color(ChatColor.RED);
            return false;
        } else if (args.length > 1) {
            final var builder = new ComponentBuilder();
            builder.append("Too many arguments").color(ChatColor.RED);
            return false;
        }

        // Get the service task by the name that was provided as the first argument
        final var serviceTask = this.serviceTaskProvider.serviceTask(args[0]);
        if (serviceTask == null) {
            final var builder = new ComponentBuilder();
            builder.append("The service task ").color(ChatColor.RED);
            builder.append(args[0]).color(ChatColor.AQUA);
            builder.append(" does not exist").color(ChatColor.RED);
            return false;
        }

        // Create the service in CloudNet
        // This usually tries to also start the service
        final var result = this.cloudServiceFactory.createCloudService(ServiceConfiguration.builder(serviceTask).build());

        final var builder = new ComponentBuilder();
        builder.append("The service ").color(ChatColor.GREEN);
        builder.append(result.serviceInfo().serviceId().name()).color(ChatColor.AQUA);
        builder.append(" has been created").color(ChatColor.GREEN);
        builder.append("\n").reset();

        builder.append("Its current status is ").color(ChatColor.YELLOW);
        builder.append(result.state().name()).color(ChatColor.AQUA);
        builder.append("\n").reset();

        builder.append("Its current life cycle status is ").color(ChatColor.YELLOW);
        builder.append(result.serviceInfo().lifeCycle().name()).color(ChatColor.AQUA);
        builder.append("\n").reset();

        sender.spigot().sendMessage(builder.build());

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return this.serviceTaskProvider.serviceTasks()
                    .stream()
                    .map(ServiceTask::name)
                    .filter(name -> name.startsWith(args[0])).toList();
        }
        return List.of();
    }
}
