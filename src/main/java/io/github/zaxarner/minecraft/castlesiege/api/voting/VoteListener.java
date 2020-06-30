package io.github.zaxarner.minecraft.castlesiege.api.voting;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.tasks.BroadcastTask;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 12/10/2019.
 */
public class VoteListener implements Listener {


    @EventHandler(priority = EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {

        String username = event.getVote().getUsername().trim();

        if (username.length() <= 0) {
            CastleSiege.log("Received Vote with no username!", Level.WARNING);
            return;
        }

        Player player = Bukkit.getPlayer(username);
        if(player != null) {

            PlayerProfile profile = PlayerProfile.getProfile(player);

            handleVote(profile.getDataFile(), player.getName());



        } else {
            CastleSiege.log(username + " voted but they were not online! Trying to find their profile...", Level.WARNING);

            File users = new File(CastleSiege.getPlugin().getDataFolder(), "users");

            String[] files = users.list();
            if(files != null) {
                for (String file : files) {
                    DataFile dataFile = new DataFile(file, "users", false);
                    String name = dataFile.getConfig().getString("name");
                    if(name != null && name.equalsIgnoreCase(username)) {

                        handleVote(dataFile, name);
                    }

                }
            }
            CastleSiege.log("Could not find user profile...", Level.WARNING);
        }
    }


    private void handleVote(DataFile dataFile, String name) {
        int current = dataFile.getConfig().getInt("exp-boost-duration");

        if(dataFile.getConfig().get("votes") != null) {
            dataFile.getConfig().set("votes", dataFile.getConfig().getInt("votes") + 1);
        } else {
            dataFile.getConfig().set("votes", 1);
        }

        dataFile.getConfig().set("exp-boost-duration", current + 300);
        dataFile.getConfig().set("last-vote-time", System.currentTimeMillis());
        dataFile.save();

        BroadcastTask.broadcast(ChatColor.GOLD + name + ChatColor.GRAY + " voted and received a 5-minute " +
                ChatColor.DARK_AQUA + "Exp Boost" + ChatColor.GRAY + "!", false);

    }
}
