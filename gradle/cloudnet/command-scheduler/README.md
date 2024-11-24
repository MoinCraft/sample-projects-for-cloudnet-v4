# Command-Scheduler Module

The Command-Scheduler module is a module that allows you to execute commands at a specific time or interval.

<!-- TOC -->
* [Command-Scheduler Module](#command-scheduler-module)
  * [Features](#features)
  * [Commands](#commands)
    * [Listing all schedules](#listing-all-schedules)
    * [Creating a new schedule](#creating-a-new-schedule)
    * [Setting the expression of a schedule](#setting-the-expression-of-a-schedule)
    * [Setting the single-use property of a schedule](#setting-the-single-use-property-of-a-schedule)
    * [Enabling or disabling a schedule](#enabling-or-disabling-a-schedule)
    * [Adding a command to a schedule](#adding-a-command-to-a-schedule)
    * [Inserting a command at a specific index in a schedule](#inserting-a-command-at-a-specific-index-in-a-schedule)
    * [Removing a command from a schedule](#removing-a-command-from-a-schedule)
    * [Renaming a schedule](#renaming-a-schedule)
    * [Deleting a schedule](#deleting-a-schedule)
  * [Expressions](#expressions)
    * [Cron Expressions](#cron-expressions)
    * [Human-Readable Expressions](#human-readable-expressions)
* [Data Storage](#data-storage)
* [Building](#building)
* [Installation](#installation)
* [Configuration](#configuration)
<!-- TOC -->

## Features

- Schedule commands to be executed at a specific time
- Schedule commands to be executed at regular intervals
- Human-readable expressions for scheduling commands
- Single-use schedules that are disabled after execution
- Enable or disable schedules
- List all schedules and their details
- Add, remove, and modify commands in a schedule
- Rename schedules
- Delete schedules
- Cron expressions for advanced scheduling
- Command execution logging

## Commands

Below you can find a list of all commands that are available in the Command-Scheduler module.
Required arguments are marked with `<` and `>`, optional arguments are marked with `[` and `]`.

These commands require the `commandscheduler.command` permission that is granted to the console by default.

### Listing all schedules

Command: `scheduler list`

This command lists all schedules that are currently registered in the scheduler.
The output will contain the name of the schedule, the expression, the next execution time, and whether the schedule is enabled among other information.

### Creating a new schedule

Command: `scheduler create <name> <expression> <singleUse> <enabled>`

Parameters:
- `name`: The name of the schedule
- `expression`: The cron expression or human-readable expression for the schedule
- `singleUse`: Whether the schedule should be disabled after it has been executed once
- `enabled`: Whether the schedule should be enabled

This command creates a new schedule with the specified name, expression, single-use property, and enabled status.

### Setting the expression of a schedule

Command: `scheduler set <schedule> expression <expression>`

Parameters:
- `schedule`: The schedule to modify
- `expression`: The new cron expression or human-readable expression for the schedule

This command sets a new expression for the specified schedule. If the expression is invalid, an error message will be shown.

### Setting the single-use property of a schedule

Command: `scheduler set <schedule> singleUse <singleUse>`

Parameters:
- `schedule`: The schedule to modify
- `singleUse`: Whether the schedule should be disabled after it has been executed once

This command sets the single-use property of the specified schedule. If `singleUse` is true, the schedule will be disabled after its first execution.

### Enabling or disabling a schedule

Command: `scheduler set <schedule> enabled <enabled>`

Parameters:
- `schedule`: The schedule to modify
- `enabled`: Whether the schedule should be enabled

This command enables or disables the specified schedule based on the `enabled` parameter.

### Adding a command to a schedule

Command: `scheduler command <schedule> add <command>`

Parameters:
- `schedule`: The schedule to modify
- `command`: The command to add to the schedule

This command adds a new command to the specified schedule.

### Inserting a command at a specific index in a schedule

Command: `scheduler command <schedule> insert <index> <command>`

Parameters:
- `schedule`: The schedule to modify
- `index`: The index at which to insert the command
- `command`: The command to insert

This command inserts a new command at the specified index in the schedule's command list. If the index is out of bounds, the command will be added to the end of the list.

### Removing a command from a schedule

Command: `scheduler command <schedule> remove <command>`

Parameters:
- `schedule`: The schedule to modify
- `command`: The command to remove, either by index or by command string

This command removes a command from the specified schedule. The command can be specified either by its index in the command list or by the command string itself.

### Renaming a schedule

Command: `scheduler rename <schedule> <newName>`

Parameters:
- `schedule`: The schedule to rename
- `newName`: The new name for the schedule

This command renames the specified schedule to the new name provided.

### Deleting a schedule

Command: `scheduler delete <schedule>`

Parameters:
- `schedule`: The schedule to delete

This command deletes the specified schedule from the system.

## Expressions

The Command-Scheduler module supports two types of expressions for scheduling commands: cron expressions and human-readable expressions.

### Cron Expressions

Cron expressions are a standard way to define schedules in Unix-like systems. They consist of five fields that represent the minute, hour, day of the month, month, and day of the week when the command should be executed.

The format of a cron expression is as follows:

```
* * * * *
- - - - -
| | | | |
| | | | +----- Day of the week (0 - 7) (Sunday is both 0 and 7)
| | | +------- Month (1 - 12)
| | +--------- Day of the month (1 - 31)
| +----------- Hour (0 - 23)
+------------- Minute (0 - 59)
```

For example, the expression `0 0 * * *` would execute the command at midnight every day.

### Human-Readable Expressions

Human-readable expressions are a more user-friendly way to define schedules.
They allow you to specify the time and frequency of the command execution in a more natural language format.
Examples of human-readable expressions include `every 5 minutes`, `at 3:00 PM`, and `every day at 6:00 AM`.

Some common keywords that can be used in human-readable expressions include:
- `every`: Specifies that the command should be executed at regular intervals
- `at`: Specifies a specific time of day when the command should be executed
- `on`: Specifies a specific day of the week when the command should be executed
- `in`: Specifies a relative time in the future when the command should be executed

Multiple times can be combined in a single expression, for example `at 3:00 PM and 9:00 PM`.

# Data Storage

The Command-Scheduler module uses a database to store schedule information. 
Each schedule is stored as a document in the database, with the schedule name as the key.
The document contains all the details of the schedule, including the expression, single-use property, enabled status, and the list of commands.

# Building

To build the Command-Scheduler module, you can use the following command:

```shell
./gradlew :cloudnet:command-scheduler:shadowJar
```

The compiled JAR file can be found in the `build/libs` directory of the module.

# Installation

To install the Command-Scheduler module, you need to copy the compiled JAR file to the `modules` directory of your CloudNet installation.
After restarting the CloudNet node, the module will be loaded automatically.

# Configuration

The Command-Scheduler module does not require any additional configuration.
