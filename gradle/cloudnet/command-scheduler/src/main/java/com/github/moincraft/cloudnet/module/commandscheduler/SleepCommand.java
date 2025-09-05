package com.github.moincraft.cloudnet.module.commandscheduler;

import eu.cloudnetservice.driver.language.I18n;
import eu.cloudnetservice.node.command.annotation.Description;
import eu.cloudnetservice.node.command.source.CommandSource;
import jakarta.inject.Singleton;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

@Singleton
@Permission("commandscheduler.command.sleep")
@Description(value = "Sleeps for a specified amount of time and then resumes execution", translatable = false)
public class SleepCommand {
    @Command("sleep <duration>")
    public void sleep(
            @NotNull CommandSource source,
            @Argument("duration") long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            source.sendMessage(I18n.i18n().translate("module-commandscheduler-sleep-interrupted"));
        }
    }
}
