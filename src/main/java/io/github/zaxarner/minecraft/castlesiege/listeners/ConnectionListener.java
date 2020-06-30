package io.github.zaxarner.minecraft.castlesiege.listeners;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 7/24/2019
 */
public class ConnectionListener implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        AttributeInstance playerAttackSpeedAttribute = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED);
        if (playerAttackSpeedAttribute != null) {
            playerAttackSpeedAttribute.setBaseValue(4.0);

            for(AttributeModifier mod : playerAttackSpeedAttribute.getModifiers()) {
                playerAttackSpeedAttribute.removeModifier(mod);
            }
        }

        event.setJoinMessage(ChatColor.GREEN + "+ " + ChatColor.GRAY + player.getName());

        if (player.getGameMode() != GameMode.CREATIVE || player.getWorld() == CastleSiege.getSpawn().getWorld()) {
            player.teleport(CastleSiege.getSpawn());
            PlayerUtils.getLobbyInventory(player);
            player.setGameMode(GameMode.SURVIVAL);
        }

        PlayerProfile playerProfile = new PlayerProfile(player);
        PlayerProfile.registerProfile(player, playerProfile);

        int timeDifference = (int) System.currentTimeMillis() - playerProfile.getDataFile().getConfig().getInt("last-vote-time");
        if (playerProfile.getDataFile().getConfig().get("last-vote-time") != null && playerProfile.getDataFile().getConfig().getInt("last-vote-time") > 0 &&
                timeDifference > 86400000) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                player.sendMessage("");
                player.sendMessage(ChatColor.DARK_AQUA + "You have not voted today! " + ChatColor.GOLD + "/vote");
                player.sendMessage("");

            }, 2L);
        }


        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("castlesiege.admin")).forEach(p -> p.
                playSound(p.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 0.5f, 1.25f));

        PlayerUtils.updateAttackSpeed(player);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Game game = CastleSiege.getPlugin().getGame(player);
        if (game != null) {
            game.leaveGame(player);
            game.removePlayer(player);
        }

        player.getInventory().clear();

        event.setQuitMessage(ChatColor.RED + "- " + ChatColor.GRAY + player.getName());

        PlayerProfile.unregisterProfile(player);

    }

}
