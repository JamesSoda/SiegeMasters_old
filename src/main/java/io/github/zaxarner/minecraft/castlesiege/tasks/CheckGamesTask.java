package io.github.zaxarner.minecraft.castlesiege.tasks;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.ControlGame;
import io.github.zaxarner.minecraft.castlesiege.game.ControlPoint;
import io.github.zaxarner.minecraft.castlesiege.game.DeathmatchGame;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.game.map.CrumblingCastleMap;
import io.github.zaxarner.minecraft.castlesiege.game.map.Map;
import io.github.zaxarner.minecraft.castlesiege.game.map.MapInstantiationException;
import io.github.zaxarner.minecraft.castlesiege.game.map.MonasteryMap;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import io.github.zaxarner.minecraft.castlesiege.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 11/18/2019.
 */
public class CheckGamesTask extends BukkitRunnable {

    public CheckGamesTask() {
        this.runTaskTimer(CastleSiege.getPlugin(), 20L, 10 * 20L);
    }

    @Override
    public void run() {

        List<Game> runningGames = CastleSiege.getPlugin().getGames();

        if (runningGames.size() >= 9)
            return;

        Game game = null;

        if (runningGames.size() == 0) {
            CastleSiege.log("Creating Game because runningGames is empty...", Level.INFO);

            try {
                game = selectGameMapCombination();
            } catch (MapInstantiationException e) {
                e.printStackTrace();
            }
        } else {

            int players = Bukkit.getOnlinePlayers().size();
            int playerCapacity = 0;

            for (Game g : runningGames) {
                if (g != null && g.getMap() != null) {
                    playerCapacity += g.getMap().getMaxPlayers();
                }
            }

            if (players > playerCapacity) {
                CastleSiege.log("Creating Game because playerCapacity is reached...", Level.INFO);
                try {
                    game = selectGameMapCombination();
                } catch (MapInstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        if (game != null) {
            CastleSiege.getPlugin().newGame(game);

            BroadcastTask.broadcast("");
            BroadcastTask.broadcast(ChatColor.DARK_AQUA + "A new " + ChatColor.GOLD + game.getGameTypeName() + ChatColor.DARK_AQUA +
                    " Game has started on ", true);

            BroadcastTask.broadcast(ChatColor.GOLD + game.getMap().getName() + ChatColor.DARK_AQUA +
                    ". Type " + ChatColor.GOLD + "/join" + ChatColor.DARK_AQUA + " to play!");
            BroadcastTask.broadcast("");
        }
    }

    private Game selectGameMapCombination() throws MapInstantiationException {

        switch (MathUtils.ranNumber(0, 3)) {
            case 0:
                ControlGame controlCC = new ControlGame(new CrumblingCastleMap());

                controlCC.setControlPoints(new ControlPoint(controlCC, "Courtyard",
                        new Location(controlCC.getMap().getWorld(), -14.5, 113, -11.5), 3, -1, 10, null, null));

                return controlCC;
            case 1:
                return new DeathmatchGame(new CrumblingCastleMap());
            case 2:

                if(Bukkit.getOnlinePlayers().size() >= 6) {

                    ControlGame controlMonastery = new ControlGame(new MonasteryMap());


                    controlMonastery.setControlPoints(
                            new ControlPoint(controlMonastery, "Waterfall",
                                    new Location(controlMonastery.getMap().getWorld(), 21.5, 95, -160.5), 5, -1, 10, null, null),
                            new ControlPoint(controlMonastery, "Courtyard",
                                    new Location(controlMonastery.getMap().getWorld(), 21.5, 95, -126.5), 5, -1, 10, null, null),
                            new ControlPoint(controlMonastery, "Temple",
                                    new Location(controlMonastery.getMap().getWorld(), 21.5, 100, -94.5), 5, -1, 10, null, null));

                    return controlMonastery;
                }
            case 3:
                if(Bukkit.getOnlinePlayers().size() >= 6) {
                    return new DeathmatchGame(new MonasteryMap());
                }
        }


        return null;
    }

}