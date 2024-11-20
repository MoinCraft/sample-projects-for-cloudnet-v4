package com.github.moincraft.cloudnet.module.commandscheduler;

import com.github.moincraft.cloudnet.module.commandscheduler.data.Schedule;
import com.google.common.reflect.TypeToken;
import eu.cloudnetservice.common.language.I18n;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.node.cluster.NodeServerProvider;
import eu.cloudnetservice.node.command.CommandProvider;
import eu.cloudnetservice.node.command.source.ConsoleCommandSource;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class CommandSchedulerModule extends DriverModule {
    private final static String DATABASE_SCHEDULE_TABLE = "command_scheduler";

    private DatabaseProvider databaseProvider;
    private ScheduledFuture<?> task;
    private ScheduledExecutorService executor;
    private CommandProvider commandProvider;
    private final Set<String> runningSchedules = new HashSet<>();

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED, order = 127)
    public void initModule(@NotNull DatabaseProvider databaseProvider,
                           @NotNull @Named("taskScheduler") ScheduledExecutorService executor,
                           @NotNull CommandProvider commandProvider,
                           @NotNull NodeServerProvider nodeServerProvider) {
        I18n.loadFromLangPath(CommandSchedulerModule.class);
        this.databaseProvider = databaseProvider;
        this.executor = executor;
        this.commandProvider = commandProvider;

        // Only start the scheduler on the head node
        if (nodeServerProvider.localNode().head()) {
            this.task = this.executor.scheduleAtFixedRate(this::checkSchedule, 0, 1, TimeUnit.SECONDS);
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void registerCommand(@NotNull CommandProvider commandProvider) {
        // register the bridge command
        commandProvider.register(CommandSchedulerCommand.class);
        commandProvider.register(SleepCommand.class);
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED, order = 126)
    public void migrateSchedules() {
        var database = this.getDatabase();
        if (database.entries().isEmpty()) {
            return;
        }
        ConsoleCommandSource.INSTANCE.sendMessage("Migrating schedules to new format");
        database.entries().forEach((key, document) -> {
            if (document.contains("script") && document.readDocument("script").contains("commands")) {
                List<String> commands = document.readDocument("script").readObject("commands", new TypeToken<List<String>>() {
                }.getType());
                var schedule = new Schedule(key,
                        commands,
                        document.getString("expression"),
                        document.readObject("creationDate", ZonedDateTime.class),
                        document.readObject("lastExecution", ZonedDateTime.class),
                        document.getBoolean("singleUse"),
                        document.getBoolean("enabled"));
                schedule = schedule.withCommands(commands);
                database.insert(key, Document.newJsonDocument().appendTree(schedule));
            }
        });
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void stop() {
        this.task.cancel(true);
    }

    private void checkSchedule() {
        this.getDatabase().entries().forEach((_, document) -> {
            // check if the time is now
            var schedule = document.toInstanceOf(Schedule.class);
            if (schedule.enabled() && !this.runningSchedules.contains(schedule.name())) {
                this.runningSchedules.add(schedule.name());
                this.executor.execute(() -> this.tryRunSchedule(schedule));
            }
        });
    }

    private void tryRunSchedule(Schedule schedule) {
        final var nextExecution = schedule.determineNextExecution(schedule.lastExecution());
        if (nextExecution != null && nextExecution.isBefore(ZonedDateTime.now())) {
            // run the script
            for (String command : schedule.commands()) {
                try {
                    ConsoleCommandSource.INSTANCE.sendMessage(I18n.trans("module-commandscheduler-executing-command", command));
                    this.commandProvider.execute(ConsoleCommandSource.INSTANCE, command).get();
                } catch (InterruptedException | ExecutionException e) {
                    break;
                }
            }

            var newSchedule = this.getDatabase().get(schedule.name());
            if (newSchedule == null) {
                return;
            }
            schedule = newSchedule.toInstanceOf(Schedule.class);

            // disable the schedule if it is single use
            if (schedule.singleUse()) {
                schedule = schedule.withEnabled(false);
            }
            // update the last execution date
            this.getDatabase().insert(schedule.name(), Document.newJsonDocument().appendTree(schedule.withLastExecution(ZonedDateTime.now())));
        }

        this.runningSchedules.remove(schedule.name());
    }

    Database getDatabase() {
        return this.databaseProvider.database(DATABASE_SCHEDULE_TABLE);
    }
}
