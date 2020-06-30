package io.github.zaxarner.minecraft.castlesiege.commands.staff;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JamesCZ98 on 12/10/2019.
 */
public class ResetPlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if(args.length > 0) {

            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);

            if(target == null) {
                sender.sendMessage(ChatColor.RED + "That is not an online player!");
                return true;
            }

            PlayerUtils.cancelTasks(target);

            target.getInventory().clear();
            target.updateInventory();

            Game game = CastleSiege.getPlugin().getGame(target);

            if (game == null) {
                target.teleport(CastleSiege.getSpawn());
                return true;
            } else {
                PlayerUtils.addCancelTask(target, Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
                    game.teleportToSpawn(target);

                    Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {

                        PlayerUtils.equipLoadout(target);
                    }, 5L);
                }, game.getRespawnTime() * 20L).getTaskId());
            }

        }

        return false;
    }

}
