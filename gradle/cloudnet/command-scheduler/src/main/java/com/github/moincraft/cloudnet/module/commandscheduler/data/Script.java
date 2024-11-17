package com.github.moincraft.cloudnet.module.commandscheduler.data;

import java.time.Duration;
import java.util.List;

public record Script(Duration delay, List<String> commands) {
    public Script {
        if (delay == null) {
            throw new IllegalArgumentException("delay cannot be null");
        }
        if (delay.isNegative()) {
            throw new IllegalArgumentException("delay cannot be negative");
        }
        if (commands == null) {
            throw new IllegalArgumentException("commands cannot be null");
        }
    }

    public static Script empty() {
        return new Script(Duration.ZERO, List.of());
    }

    public Script withDelay(Duration delay) {
        return new Script(delay, this.commands());
    }
}
