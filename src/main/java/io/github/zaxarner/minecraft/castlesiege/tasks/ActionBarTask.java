package io.github.zaxarner.minecraft.castlesiege.tasks;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by JamesCZ98 on 11/27/2019.
 */
public class ActionBarTask extends BukkitRunnable {


    public ActionBarTask() {
        this.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 20L, 5L);

        Bukkit.getScheduler().runTaskTimer(CastleSiege.getPlugin(), () -> {

            for(Player p : Bukkit.getOnlinePlayers()) {
                PlayerUtils.recentExpGained.put(p, 0);
            }
        }, 60L,60L);
    }

    @Override
    public void run() {

        for(Player p : Bukkit.getOnlinePlayers()) {
            PlayerUtils.updateActionBar(p);
        }

    }

}
