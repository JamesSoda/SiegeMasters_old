package io.github.zaxarner.minecraft.castlesiege.commands.staff;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by JamesCZ98 on 8/6/2019.
 */
public class EndCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        for(Game game : CastleSiege.getPlugin().getGames()) {
            game.endGame();
        }

        return true;
    }
}
