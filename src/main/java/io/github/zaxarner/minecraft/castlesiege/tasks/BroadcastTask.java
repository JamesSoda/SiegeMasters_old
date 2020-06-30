package io.github.zaxarner.minecraft.castlesiege.tasks;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

/**
 * Created by JamesCZ98 on 11/10/2019.
 */
public class BroadcastTask extends BukkitRunnable {

    private int messageIndex = 0;

    private static DataFile broadcastFile = new DataFile("broadcasts.yml", null, true);

    public static String PREFIX = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(broadcastFile.getConfig().getString("prefix")));

    private List<String> messages = broadcastFile.getConfig().getStringList("messages");

    public BroadcastTask() {
        int frequency = broadcastFile.getConfig().getInt("frequency");
        this.runTaskTimerAsynchronously(CastleSiege.getPlugin(), frequency * 20L, frequency * 20L);
    }

    @Override
    public void run() {

        if(messageIndex >= messages.size())
            messageIndex = 0;

        String message = messages.get(messageIndex);
        broadcast(message, true);

        messageIndex++;
    }

    public static void broadcast(String message) {
        broadcast(message, false);
    }

    public static void broadcast(String message, boolean prefix) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(prefix)
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + message));
            else
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
