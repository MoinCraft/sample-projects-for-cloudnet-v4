package com.github.moincraft.cloudnet.module.commandscheduler;

import com.github.moincraft.cloudnet.module.commandscheduler.data.Schedule;
import eu.cloudnetservice.common.language.I18n;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.node.command.annotation.Description;
import eu.cloudnetservice.node.command.exception.ArgumentNotAvailableException;
import eu.cloudnetservice.node.command.source.CommandSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.type.Either;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Singleton
@Permission("commandscheduler.command")
//@Description("module-commandscheduler-command-description")
@Description(value = "This is the command to manage and control your command schedules.", translatable = false)
public class CommandSchedulerCommand {

    private final CommandSchedulerModule module;

    @Inject
    public CommandSchedulerCommand(
            @NotNull CommandSchedulerModule module
    ) {
        this.module = module;
    }

    @Parser(name = "schedule", suggestions = "schedule")
    public Schedule parseSchedule(CommandInput input) {
        var inputString = input.readString();
        if (this.module.getDatabase().contains(inputString)) {
            return Objects.requireNonNull(this.module.getDatabase().get(inputString)).toInstanceOf(Schedule.class);
        }
        throw new ArgumentNotAvailableException(I18n.trans("module-commandscheduler-schedule-not-found", inputString));
    }

    @Suggestions("schedule")
    public Iterable<String> suggestScheduleName() {
        return this.module.getDatabase().keys();
    }

    @Suggestions("scheduleCommands")
    public Iterable<String> suggestScheduleCommands(CommandContext<CommandSource> context, CommandInput input) {
        Optional<Schedule> schedule = context.optional("schedule");
        if (schedule.isEmpty()) {
            return List.of();
        }
        var commands = new ArrayList<String>();
        for (var command : schedule.get().commands()) {
            // Add quotes to the command as escaped spaces are not properly supported
            commands.add('"' + command + '"');
        }
        var commandAmount = commands.size();
        for (int i = 0; i < commandAmount; i++) {
            commands.add(String.valueOf(i));
        }

        final var returnCommands = new ArrayList<String>();
        final String prefix = input.readString();
        for (var command : commands) {
            if (command.startsWith(prefix)) {
                returnCommands.add(command);
            }
        }

        returnCommands.sort(Comparator.naturalOrder());

        return returnCommands;
    }

    @Command("scheduler list")
    public void listSchedules(@NotNull CommandSource source) {
        var count = this.module.getDatabase().documentCount();
        if (count == 0) {
            source.sendMessage(I18n.trans("module-commandscheduler-no-schedules"));
            return;
        }

        if (count == 1) {
            source.sendMessage(I18n.trans("module-commandscheduler-one-schedule"));
        } else {
            source.sendMessage(I18n.trans("module-commandscheduler-multiple-schedules", count));
        }
        source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-header"));
        source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-separator"));
        this.module.getDatabase().entries().forEach((name, document) -> {
            final var schedule = document.toInstanceOf(Schedule.class);
            final var nextExecution = schedule.determineNextExecution(schedule.lastExecution());

            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-internal-id", name));
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-name", schedule.name()));
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-creation", schedule.creationDate()));
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-expression", schedule.expression()));
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-single-use", schedule.singleUse()));
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-enabled", schedule.enabled()));
            final List<String> commands = schedule.commands();
            if (commands.isEmpty()) {
                source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-script-empty"));
            } else {
                source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-script"));
                for (var command : commands) {
                    source.sendMessage("  - " + command);
                }
            }
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-last-execution", schedule.lastExecution()));
            if (nextExecution == null) {
                source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-next-execution-invalid"));
            } else {
                source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-key-next-execution", nextExecution));
            }
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-list-element-separator"));
        });
    }

    @Command("scheduler create <name> <expression> <singleUse> <enabled>")
    public void createSchedule(
            @NotNull CommandSource source,
            @Argument("name") String name,
            @Argument("expression") @Quoted String expression,
            @Argument("singleUse") boolean singleUse,
            @Argument("enabled") boolean enabled
    ) {
        var schedule = Schedule.newSchedule(
                name,
                expression,
                singleUse,
                enabled);
        this.saveSchedule(source, schedule);
        source.sendMessage(I18n.trans("module-commandscheduler-schedule-created", name));
    }

    @Command("scheduler set <schedule> expression <expression>")
    public void setExpression(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule,
            @Argument("expression") @Quoted String expression
    ) {
        if (!Schedule.validateExpression(schedule, expression)) {
            source.sendMessage(I18n.trans("module-commandscheduler-expression-parse-error", expression));
            return;
        }

        schedule = schedule.withExpression(expression);
        this.saveSchedule(source, schedule);
        source.sendMessage(I18n.trans("module-commandscheduler-expression-set", schedule.name(), expression));
    }

    @Command("scheduler set <schedule> singleUse <singleUse>")
    public void setSingleUse(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule,
            @Argument("singleUse") boolean singleUse
    ) {
        schedule = schedule.withSingleUse(singleUse);
        this.saveSchedule(source, schedule);
        if (singleUse) {
            source.sendMessage(I18n.trans("module-commandscheduler-single-use-set", schedule.name()));
        } else {
            source.sendMessage(I18n.trans("module-commandscheduler-single-use-unset", schedule.name()));
        }
    }

    @Command("scheduler set <schedule> enabled <enabled>")
    public void setEnabled(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule,
            @Argument("enabled") boolean enabled
    ) {
        schedule = schedule.withEnabled(enabled);
        this.saveSchedule(source, schedule);
        if (enabled) {
            source.sendMessage(I18n.trans("module-commandscheduler-enabled", schedule.name()));
        } else {
            source.sendMessage(I18n.trans("module-commandscheduler-disabled", schedule.name()));
        }
    }

    @Command("scheduler command <schedule> add <command>")
    public void addCommand(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule,
            @Argument("command") @Greedy String command
    ) {
        var commands = schedule.commands();
        commands.add(command);
        this.saveSchedule(source, schedule.withCommands(commands));
        source.sendMessage(I18n.trans("module-commandscheduler-command-added", command, schedule.name()));
    }

    @Command("scheduler command <schedule> insert <index> <command>")
    public void insertCommand(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule,
            @Argument("index") int index,
            @Argument("command") @Greedy String command
    ) {
        var commands = schedule.commands();
        if (index < 0) {
            source.sendMessage(I18n.trans("module-commandscheduler-index-negative-error"));
            return;
        }
        if (index <= commands.size()) {
            commands.add(index, command);
        } else {
            commands.add(command);
        }
        this.saveSchedule(source, schedule.withCommands(commands));
        source.sendMessage(I18n.trans("module-commandscheduler-command-inserted", command, index, schedule.name()));
    }

    @Command("scheduler command <schedule> remove <command>")
    public void removeCommand(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule,
            @Argument(value = "command", suggestions = "scheduleCommands") @Quoted Either<Integer, String> command
    ) {
        String removed;
        var commands = schedule.commands();
        if (command.primary().isPresent()) {
            int index = command.primary().get();
            if (index < 0 || index >= commands.size()) {
                source.sendMessage(I18n.trans("module-commandscheduler-index-out-of-bounds-error", index, schedule.name(), commands.size()));
                return;
            }
            removed = commands.remove(index);
            if (removed == null) {
                source.sendMessage(I18n.trans("module-commandscheduler-command-remove-index-error", index, schedule.name()));
                return;
            } else {
                source.sendMessage(I18n.trans("module-commandscheduler-command-removed-index", removed, index, schedule.name()));
            }
        } else if (command.fallback().isPresent()) {
            final String commandString = command.fallback().get();
            if (!commands.remove(commandString)) {
                source.sendMessage(I18n.trans("module-commandscheduler-command-remove-error", commandString, schedule.name()));
                return;
            }
            removed = commandString;
        } else {
            source.sendMessage(I18n.trans("module-commandscheduler-parameter-error"));
            return;
        }

        this.saveSchedule(source, schedule.withCommands(commands));
        source.sendMessage(I18n.trans("module-commandscheduler-command-removed", removed, schedule.name()));
    }


    @Command("scheduler rename <schedule> <newName>")
    public void renameSchedule(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule,
            @Argument("newName") String newName) {
        final var oldName = schedule.name();
        if (this.module.getDatabase().delete(schedule.name())) {
            schedule = schedule.withName(newName);
            this.saveSchedule(source, schedule);
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-renamed", oldName, newName));
        } else {
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-rename-error", oldName));
        }
    }

    @Command("scheduler delete <schedule>")
    public void deleteSchedule(
            @NotNull CommandSource source,
            @Argument(value = "schedule", parserName = "schedule") Schedule schedule
    ) {
        if (this.module.getDatabase().delete(schedule.name())) {
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-deleted", schedule.name()));
        } else {
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-delete-error", schedule.name()));
        }
    }

    private void saveSchedule(@NotNull CommandSource source, Schedule schedule) {
        if (this.module.getDatabase().insert(schedule.name(), Document.newJsonDocument().appendTree(schedule))) {
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-saved", schedule.name()));
        } else {
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-save-error", schedule.name()));
        }
    }

    private @Nullable Schedule loadSchedule(@NotNull CommandSource source, String scheduleName) {
        var document = this.module.getDatabase().get(scheduleName);
        if (document == null) {
            source.sendMessage(I18n.trans("module-commandscheduler-schedule-not-found", scheduleName));
            return null;
        }

        return document.toInstanceOf(Schedule.class);
    }

}
