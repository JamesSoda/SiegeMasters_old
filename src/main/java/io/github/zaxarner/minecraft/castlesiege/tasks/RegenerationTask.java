package io.github.zaxarner.minecraft.castlesiege.tasks;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by JamesCZ98 on 12/18/2019.
 */
public class RegenerationTask extends BukkitRunnable {

    private final Player player;
    private final Player healer;

    private final int amount;

    public RegenerationTask(Player player, int amount, int delay, Player healer) {
        this.amount = amount;
        this.player = player;
        this.healer = healer;

        runTaskTimer(CastleSiege.getPlugin(), 0L, delay);

        PlayerUtils.addCancelTask(player, this.getTaskId());
    }

    private int runCount = 0;

    @Override
    public void run() {

        PlayerUtils.healPlayer(player, 1, healer);
        runCount++;

        if(runCount >= amount)
            this.cancel();
    }
}
