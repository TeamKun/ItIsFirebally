package net.kunmc.lab.itisfirebally.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.itisfirebally.Config;
import net.kunmc.lab.itisfirebally.ItIsFireballyPlugin;
import net.kunmc.lab.itisfirebally.game.Game;

public class StartCommand extends Command {
    public StartCommand() {
        super("start");

        execute(ctx -> {
            ItIsFireballyPlugin plugin = ItIsFireballyPlugin.getInstance();
            Config config = plugin.config();
            if (config.origin.isEmpty()) {
                ctx.sendFailure("originを設定してください");
                return;
            }

            Game game = plugin.game();
            if (game.isRunning()) {
                ctx.sendFailure("すでに実行中です");
                return;
            }

            game.start();
            ctx.sendSuccess("スタート");
        });
    }
}
