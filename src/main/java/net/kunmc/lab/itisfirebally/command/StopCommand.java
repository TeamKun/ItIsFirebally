package net.kunmc.lab.itisfirebally.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.itisfirebally.ItIsFireballyPlugin;
import net.kunmc.lab.itisfirebally.game.Game;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");

        execute(ctx -> {
            ItIsFireballyPlugin plugin = ItIsFireballyPlugin.getInstance();

            Game game = plugin.game();
            if (!game.isRunning()) {
                ctx.sendFailure("実行されていません");
                return;
            }

            game.stop();
            ctx.sendSuccess("ストップ");
        });
    }
}
