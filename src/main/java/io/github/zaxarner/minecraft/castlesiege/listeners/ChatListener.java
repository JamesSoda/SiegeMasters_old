package io.github.zaxarner.minecraft.castlesiege.listeners;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.commands.staff.StaffChatCommand;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

import static java.util.logging.Level.INFO;

/**
 * Created by JamesCZ98 on 11/30/2019.
 */
public class ChatListener implements Listener {


    private String[] regexFilters = new String[] {".*n[i1l]gg[e3]r.*", ".*k[i1l]k[e3].*", ".*b[e3][a@]n[e3]r.*", ".*f[a@]g.*", ".*f[a@]gg[o0][t71].*"};

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        for(String s : regexFilters) {
            if (event.getMessage().toLowerCase().matches(s)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Read the /rules");
                return;
            }
        }


        if(StaffChatCommand.staffChatters.contains(player)) {
            event.setCancelled(true);
            StaffChatCommand.staffChatBroadcast(ChatColor.YELLOW + "" + ChatColor.BOLD + player.getName() + ChatColor.YELLOW + " : " + event.getMessage());
        } else {

            PlayerProfile profile = PlayerProfile.getProfile(player);

            event.setFormat(ChatColor.GRAY + "[" + profile.getLevel() + "]" + ChatColor.RESET + event.getFormat());
        }
    }
}