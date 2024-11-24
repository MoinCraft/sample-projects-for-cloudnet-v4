package com.github.moincraft.cloudnet.module.commandscheduler.data;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

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

    /**
     * Comparator for dates parsed by PrettyTimeParser.
     * The comparator will sort the dates by the following rules:
     * - If one date is before the current time and the other is after, the one before will be sorted last
     * - If both dates are before or after the current time, the one closest to the current time will be sorted first
     *
     * @param now the current time
     * @return the comparator
     */
    private static Comparator<Date> prettyTimeDateComparator(ZonedDateTime now) {
        return (o1, o2) -> {
            var date1 = ZonedDateTime.ofInstant(o1.toInstant(), now.getZone());
            var date2 = ZonedDateTime.ofInstant(o2.toInstant(), now.getZone());
            if(date1.isBefore(now) && date2.isAfter(now)) {
                return 1;
            } else if(date1.isAfter(now) && date2.isBefore(now)) {
                return -1;
            }
            return Duration.between(now, date1).abs().compareTo(Duration.between(now, date2).abs());
        };
    }

    public ZonedDateTime determineNextExecution(ZonedDateTime now) {
        // First try to parse the expression using CronExpression
        ZonedDateTime parsedExecution = null;
        try {
            var cron = CRON_PARSER.parse(this.expression());
            var nextExecution = ExecutionTime.forCron(cron).nextExecution(now);
            if (nextExecution.isPresent()) {
                parsedExecution = nextExecution.get();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        // Second try to parse the expression using PrettyTimeParser
        if (parsedExecution == null) {
            final var parser = new PrettyTimeParser(TimeZone.getTimeZone(this.creationDate().getZone().getId()));
            var parsedDates = parser.parse(this.expression(), Date.from(this.lastExecution().toInstant()));
            parsedExecution = parsedDates.stream()
                    .min(Schedule.prettyTimeDateComparator(now))
                    .map(date -> ZonedDateTime.ofInstant(date.toInstant(), this.creationDate().getZone()))
                    .orElse(null);
        }
        return parsedExecution;
    }
}
