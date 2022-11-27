package net.kunmc.lab.itisfirebally;

import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import net.kunmc.lab.itisfirebally.command.MainCommand;
import net.kunmc.lab.itisfirebally.game.Game;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItIsFireballyPlugin extends JavaPlugin {
    private static ItIsFireballyPlugin INSTANCE;
    private Config config;
    private Game game;


    public static ItIsFireballyPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.config = new Config(this);
        ConfigCommand configCommand = new ConfigCommandBuilder(config).build();
        CommandLib.register(this, new MainCommand(configCommand));

        this.game = new Game(this, config);
    }

    @Override
    public void onDisable() {
        game.stop();
    }

    public Game game() {
        return game;
    }

    public Config config() {
        return config;
    }
}
