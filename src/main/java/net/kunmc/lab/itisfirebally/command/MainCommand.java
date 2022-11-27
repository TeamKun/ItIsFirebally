package net.kunmc.lab.itisfirebally.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.ConfigCommand;

public class MainCommand extends Command {
    public MainCommand(ConfigCommand configCommand) {
        super("fireball");

        addChildren(configCommand, new StartCommand(), new StopCommand());
    }
}
