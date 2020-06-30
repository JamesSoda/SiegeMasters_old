package io.github.zaxarner.minecraft.castlesiege.commands;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.logging.Level.INFO;

/**
 * Created by JamesCZ98 on 12/13/2019.
 */
public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        sender.sendMessage(ChatColor.AQUA + "Our Discord: " + ChatColor.GOLD + "https://discord.gg/zSuMDrP");
        return true;

    }

}
