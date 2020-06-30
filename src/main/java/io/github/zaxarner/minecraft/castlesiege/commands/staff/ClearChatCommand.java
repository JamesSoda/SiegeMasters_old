package io.github.zaxarner.minecraft.castlesiege.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JamesCZ98 on 12/13/2019.
 */
public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        for(Player p : Bukkit.getOnlinePlayers()) {
            for(int i=0; i < 100; i++) {
                p.sendMessage("");
            }
        }

        return true;

    }
}
