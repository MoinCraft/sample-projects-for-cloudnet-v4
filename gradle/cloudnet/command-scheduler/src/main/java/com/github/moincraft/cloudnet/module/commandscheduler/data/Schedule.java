package com.github.moincraft.cloudnet.module.commandscheduler.data;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public record Schedule(String name,
                       Script script,
                       String expression,
                       ZonedDateTime creationDate,
                       @Nullable ZonedDateTime lastExecution,
                       boolean singleUse,
                       boolean enabled) {
    public Schedule {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (script == null) {
            throw new IllegalArgumentException("script cannot be null");
        }
        if (expression == null) {
            throw new IllegalArgumentException("expression cannot be null");
        }
        if (creationDate == null) {
            throw new IllegalArgumentException("creationDate cannot be null");
        }
        var parser = new PrettyTimeParser(TimeZone.getTimeZone(creationDate.getZone().getId()));
        var parsed = parser.parseSyntax(expression);
        if (parsed.isEmpty()) {
            throw new IllegalArgumentException("expression is not valid");
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
        return new Schedule(name, Script.empty(), expression, ZonedDateTime.now(), null, singleUse, enabled);
    }

    public Schedule withName(String name) {
        return new Schedule(name, this.script(), this.expression(), this.creationDate(), this.lastExecution(), this.singleUse(), this.enabled());
    }

    public Schedule withScript(Script script) {
        return new Schedule(this.name(), script, this.expression(), this.creationDate(), this.lastExecution(), this.singleUse(), this.enabled());
    }

    public Schedule withExpression(String expression) {
        return new Schedule(this.name(), this.script(), expression, this.creationDate(), this.lastExecution(), this.singleUse(), this.enabled());
    }

    public Schedule withLastExecution(ZonedDateTime lastExecution) {
        return new Schedule(this.name(), this.script(), this.expression(), this.creationDate(), lastExecution, this.singleUse(), this.enabled());
    }

    public Schedule withSingleUse(boolean singleUse) {
        return new Schedule(this.name(), this.script(), this.expression(), this.creationDate(), this.lastExecution(), singleUse, this.enabled());
    }

    public Schedule withEnabled(boolean enabled) {
        return new Schedule(this.name(), this.script(), this.expression(), this.creationDate(), this.lastExecution(), this.singleUse(), enabled);
    }
}
