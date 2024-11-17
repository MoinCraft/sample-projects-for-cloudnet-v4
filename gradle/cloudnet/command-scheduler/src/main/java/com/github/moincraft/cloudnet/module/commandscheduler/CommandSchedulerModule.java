package com.github.moincraft.cloudnet.module.commandscheduler;

import com.github.moincraft.cloudnet.module.commandscheduler.data.Schedule;
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
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import java.sql.Date;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
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

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void initModule(
            @NotNull DatabaseProvider databaseProvider,
            @NotNull @Named("taskScheduler") ScheduledExecutorService executor,
            @NotNull CommandProvider commandProvider,
            @NotNull NodeServerProvider nodeServerProvider
    ) {
        I18n.loadFromLangPath(CommandSchedulerModule.class);
        this.databaseProvider = databaseProvider;
        this.executor = executor;
        this.commandProvider = commandProvider;

        // Only start the scheduler on the head node
        if(nodeServerProvider.localNode().head()) {
            this.task = this.executor.scheduleAtFixedRate(this::checkSchedule, 0, 1, TimeUnit.SECONDS);
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void registerCommand(@NotNull CommandProvider commandProvider) {
        // register the bridge command
        commandProvider.register(CommandSchedulerCommand.class);
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
        var parser = new PrettyTimeParser(TimeZone.getTimeZone(schedule.lastExecution().getZone()));
        final var lastExecutionDate = Date.from(schedule.lastExecution().toInstant());
        var parsedDates = parser.parse(schedule.expression(), lastExecutionDate);
        for (var date : parsedDates) {
            if (date.before(Date.from(Instant.now())) && date.after(lastExecutionDate)) {
                var script = schedule.script();
                // run the script
                List<String> commands = script.commands();
                for (int i = 0; i < commands.size(); i++) {
                    var command = commands.get(i);
                    try {
                        ConsoleCommandSource.INSTANCE.sendMessage(I18n.trans("module-commandscheduler-executing-command", command));
                        this.commandProvider.execute(ConsoleCommandSource.INSTANCE, command).get();

                        // Only sleep if there are more commands to execute
                        if (i < commands.size() - 1) {
                            Thread.sleep(script.delay());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        break;
                    }
                }

                var newSchedule = this.getDatabase().get(schedule.name());
                if (newSchedule == null) {
                    break;
                }
                schedule = newSchedule.toInstanceOf(Schedule.class);

                // disable the schedule if it is single use
                if (schedule.singleUse()) {
                    schedule = schedule.withEnabled(false);
                }
                // update the last execution date
                this.getDatabase().insert(schedule.name(), Document.newJsonDocument().appendTree(schedule.withLastExecution(ZonedDateTime.now())));
                break;
            }
        }
        this.runningSchedules.remove(schedule.name());
    }

    Database getDatabase() {
        return this.databaseProvider.database(DATABASE_SCHEDULE_TABLE);
    }
}
