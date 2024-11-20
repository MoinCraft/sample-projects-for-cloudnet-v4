package com.github.moincraft.cloudnet.module.commandscheduler.data;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public record Schedule(String name,
                       List<String> commands,
                       String expression,
                       ZonedDateTime creationDate,
                       @Nullable ZonedDateTime lastExecution,
                       boolean singleUse,
                       boolean enabled) {

    private static final CronParser CRON_PARSER = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    public Schedule {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (commands == null) {
            throw new IllegalArgumentException("commands cannot be null");
        }
        if (expression == null) {
            throw new IllegalArgumentException("expression cannot be null");
        }
        if (creationDate == null) {
            throw new IllegalArgumentException("creationDate cannot be null");
        }
    }

    @Override
    public ZonedDateTime lastExecution() {
        return this.lastExecution == null ? this.creationDate() : this.lastExecution;
    }

    public static Schedule newSchedule(String name,
                                       String expression,
                                       boolean singleUse,
                                       boolean enabled) {
        return new Schedule(name, List.of(), expression, ZonedDateTime.now(), null, singleUse, enabled);
    }

    public Schedule withName(String name) {
        return new Schedule(name, this.commands(), this.expression(), this.creationDate(), this.lastExecution(), this.singleUse(), this.enabled());
    }

    public Schedule withCommands(List<String> commands) {
        return new Schedule(this.name(), commands, this.expression(), this.creationDate(), this.lastExecution(), this.singleUse(), this.enabled());
    }

    public Schedule withExpression(String expression) {
        return new Schedule(this.name(), this.commands(), expression, this.creationDate(), this.lastExecution(), this.singleUse(), this.enabled());
    }

    public Schedule withLastExecution(ZonedDateTime lastExecution) {
        return new Schedule(this.name(), this.commands(), this.expression(), this.creationDate(), lastExecution, this.singleUse(), this.enabled());
    }

    public Schedule withSingleUse(boolean singleUse) {
        return new Schedule(this.name(), this.commands(), this.expression(), this.creationDate(), this.lastExecution(), singleUse, this.enabled());
    }

    public Schedule withEnabled(boolean enabled) {
        return new Schedule(this.name(), this.commands(), this.expression(), this.creationDate(), this.lastExecution(), this.singleUse(), enabled);
    }

    public static boolean validateExpression(final Schedule schedule, String expression) {
        final var tempSchedule = schedule.withExpression(expression);
        return tempSchedule.determineNextExecution(ZonedDateTime.now()) != null;
    }

    public ZonedDateTime determineNextExecution(ZonedDateTime now) {
        // Second try to parse the expression using PrettyTimeParser
        ZonedDateTime parsedExecutions = null;
        try {
            var cron = CRON_PARSER.parse(this.expression());
            var nextExecution = ExecutionTime.forCron(cron).nextExecution(now);
            if (nextExecution.isPresent()) {
                parsedExecutions = nextExecution.get();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        // First try to parse the expression using CronExpression
        if (parsedExecutions == null) {
            final var nowDate = Date.from(now.toInstant());
            final var parser = new PrettyTimeParser(TimeZone.getTimeZone(this.creationDate().getZone().getId()));
            final var lastExecutionDate = Date.from(this.lastExecution().toInstant());
            var parsedDates = parser.parse(this.expression(), lastExecutionDate);
            parsedExecutions = parsedDates.stream()
                    .filter(date -> date.after(lastExecutionDate))
                    .min(Comparator.naturalOrder())
                    .map(date -> ZonedDateTime.ofInstant(date.toInstant(), this.creationDate().getZone()))
                    .orElse(null);
        }
        return parsedExecutions;
    }
}
