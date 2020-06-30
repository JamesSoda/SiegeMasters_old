package io.github.zaxarner.minecraft.castlesiege.game;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.map.Map;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.tasks.BroadcastTask;
import io.github.zaxarner.minecraft.castlesiege.utils.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by JamesCZ98 on 8/6/2019.
 */
public abstract class Game {

    Scoreboard scoreboard;

    private List<Player> gamePlayers = new ArrayList<>();
    private Map map;

    private Team attackerTeam;
    private Team defenderTeam;

    private int attackerScore;
    private int defenderScore;

    private String attackerScoreName;
    private String defenderScoreName;

    private int duration = 0;

    Objective dummyObjective;

    private String timerName;

    private boolean ending = false;

    private List<String> tips = CastleSiege.getDataFile().getConfig().getStringList("tips");

    public Game(Map map) {
        this.map = map;


        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        attackerTeam = scoreboard.registerNewTeam("Attackers");
        attackerTeam.setAllowFriendlyFire(false);
        attackerTeam.setCanSeeFriendlyInvisibles(true);
        attackerTeam.setColor(ChatColor.YELLOW);
        attackerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        attackerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        attackerTeam.setPrefix("[A] ");
        attackerTeam.setDisplayName(ChatColor.YELLOW + "Attackers");

        defenderTeam = scoreboard.registerNewTeam("Defenders");
        defenderTeam.setAllowFriendlyFire(false);
        defenderTeam.setCanSeeFriendlyInvisibles(true);
        defenderTeam.setColor(ChatColor.DARK_PURPLE);
        defenderTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        defenderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        defenderTeam.setPrefix("[D] ");
        defenderTeam.setDisplayName(ChatColor.DARK_PURPLE + "Defenders");

        Objective health = scoreboard.registerNewObjective("health", "health", "Health");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        health.setRenderType(RenderType.HEARTS);

        dummyObjective = scoreboard.registerNewObjective("dummy", "dummy", "Game Info");
        dummyObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        dummyObjective.setRenderType(RenderType.INTEGER);

        timerName = ChatColor.DARK_AQUA + "Time Left: ";
        Score timerScore = dummyObjective.getScore(timerName);
        timerScore.setScore(5);

        dummyObjective.getScore(ChatColor.DARK_AQUA + "Map: " + ChatColor.AQUA + map.getName()).setScore(4);

        dummyObjective.getScore("").setScore(3);

        attackerScoreName = ChatColor.YELLOW + "Attacker Score: 0    ";
        Score attackerScore = dummyObjective.getScore(attackerScoreName);
        attackerScore.setScore(2);

        defenderScoreName = ChatColor.DARK_PURPLE + "Defender Score: 0    ";
        Score defenderScore = dummyObjective.getScore(defenderScoreName);
        defenderScore.setScore(1);

        Bukkit.getScheduler().runTaskTimer(CastleSiege.getPlugin(), new Runnable() {
            @Override
            public void run() {
                gameTick();
            }
        }, 0L, 20L);

    }

    public abstract String getGameTypeName();

    public abstract int getRespawnTime();

    public abstract int getTimeLimit();

    public abstract int getScoreTarget();

    public Map getMap() {
        return map;
    }

    public void endGame() {
        if(ending)
            return;

        ending = true;

        if (defenderScore > attackerScore) {

            broadcast(ChatColor.GOLD + "        --==Good Game!==--    ", true);
            broadcast("", true);
            broadcast(ChatColor.DARK_AQUA + "The " + ChatColor.DARK_PURPLE + "Defenders " + ChatColor.DARK_AQUA + "Won, " +
                    defenderScore + " to " + attackerScore + "!", true);
            broadcast("", true);

        } else if (attackerScore > defenderScore) {

            broadcast(ChatColor.GOLD + "        --==Good Game!==--    ", true);
            broadcast("", true);
            broadcast(ChatColor.DARK_AQUA + "The " + ChatColor.YELLOW + "Attackers " + ChatColor.DARK_AQUA + "Won, " +
                    attackerScore + " to " + defenderScore + "!", true);
            broadcast("", true);

        } else {

            if (attackerScore != 0) {

                broadcast(ChatColor.GOLD + "        --==Good Game!==--    ", true);
                broadcast("", true);
                broadcast(ChatColor.AQUA + "It's a draw, " + attackerScore + " to " + defenderScore, true);
                broadcast("", true);
            }
        }

        getPlayers().forEach(p -> {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, p, 0.5f, 1f);
            }, 2L);
            this.leaveGame(p);
        });

        if (CastleSiege.getPlugin().isEnabled()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                CastleSiege.getPlugin().removeGame(this);
            }, 5L);
        }


        WorldUtils.deleteWorld(map.getWorld());
        gamePlayers.clear();
    }

    public void gameTick() {
        if (gamePlayers.size() <= 1)
            return;

        handleGameTick();
        updateScoreboard();

        for (Player player : gamePlayers) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                PlayerProfile profile = PlayerProfile.getProfile(player);
                profile.removeExpBoostDuration(1);
            }
        }
    }

    public void updateScoreboard() {

        int timeLeft = getTimeLimit() - duration;

        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;

        scoreboard.resetScores(timerName);
        timerName = ChatColor.DARK_AQUA + "Time Left: " + ChatColor.AQUA + String.format("%d:%02d", minutes, seconds);
        Score timerScore = dummyObjective.getScore(timerName);
        timerScore.setScore(5);


        duration++;
        if (duration > getTimeLimit()) {
            endGame();
        } else {


            if (duration == (getTimeLimit() / 2) - 1) {
                broadcast("", false);
                broadcast(ChatColor.GOLD + "Half of the time remains!", false);
                broadcast("", false);
            } else if (duration == getTimeLimit() - 59) {
                broadcast("", false);
                broadcast(ChatColor.GOLD + "One minute remaining!", false);
                broadcast("", false);

                for (Player p : gamePlayers) {
                    EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, p, 0.5f, .5f);
                }
            } else if (duration == getTimeLimit() - 9) {
                broadcast("", false);
                broadcast(ChatColor.GOLD + "Ten seconds remaining!", false);
                broadcast("", false);
                for (Player p : gamePlayers) {
                    EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, p, 0.5f, .5f);
                }
            } else if (duration >= getTimeLimit() - 2) {
                for (Player p : gamePlayers) {
                    EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, p, 0.5f, .5f);
                }
            }
        }
    }

    public abstract void handleGameTick();


    public void joinGame(Player player) {
        if (getPlayers().contains(player))
            return;

        player.setScoreboard(scoreboard);

        if (getDefenders().size() > getAttackers().size()) {
            attackerTeam.addEntry(player.getName());
        } else {
            defenderTeam.addEntry(player.getName());
        }
        gamePlayers.add(player);
        teleportToSpawn(player);

        if(this instanceof DeathmatchGame) {
            BroadcastTask.broadcast(ChatColor.DARK_AQUA + player.getName() + ChatColor.AQUA + " joined " +
                    ChatColor.GOLD + "Deathmatch " + ChatColor.AQUA + "on " + ChatColor.GOLD + map.getName() + ChatColor.AQUA + "!");
        } else if(this instanceof ControlGame) {
            BroadcastTask.broadcast(ChatColor.DARK_AQUA + player.getName() + ChatColor.AQUA + " joined " +
                    ChatColor.GOLD + "Control " + ChatColor.AQUA + "on " + ChatColor.GOLD + map.getName() + ChatColor.AQUA + "!");
        }

        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
            player.setPlayerListName(getTeamChatColor(player) + getTeam(player).getPrefix() + player.getPlayerListName());
        }, 10L);

    }

    public void leaveGame(Player player) {
        attackerTeam.removeEntry(player.getName());
        defenderTeam.removeEntry(player.getName());
        player.teleport(CastleSiege.getSpawn());

        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        PlayerUtils.cancelTasks(player);
        PlayerUtils.getLobbyInventory(player);

        PlayerUtils.killStreakMap.put(player, 0);
        PlayerUtils.killsMap.put(player, 0);
        PlayerUtils.deathsMap.put(player, 0);
        PlayerUtils.assistsMap.put(player, 0);

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
            PlayerUtils.updateMaxHealth(player);
            PlayerUtils.updateMoveSpeed(player);
            PlayerUtils.updateAttackSpeed(player);
        }, 5L);


    }

    public void addAttackerScore() {
        attackerScore++;

        scoreboard.resetScores(attackerScoreName);
        attackerScoreName = ChatColor.YELLOW + "Attacker Score: " + attackerScore + "    ";
        Score attackerScoreboardScore = dummyObjective.getScore(attackerScoreName);
        attackerScoreboardScore.setScore(2);

        if (defenderScore == getScoreTarget()) {
            endGame();
        }

        if (attackerScore == getScoreTarget()) {
            endGame();
        }
    }

    public int getAttackerScore() {
        return attackerScore;
    }

    public void addDefenderScore() {
        defenderScore++;

        scoreboard.resetScores(defenderScoreName);
        defenderScoreName = ChatColor.DARK_PURPLE + "Defender Score: " + defenderScore + "    ";
        Score defenderScoreboardScore = dummyObjective.getScore(defenderScoreName);
        defenderScoreboardScore.setScore(1);

        if (attackerScore >= getScoreTarget()) {
            endGame();
        }

        if (defenderScore >= getScoreTarget()) {
            endGame();
        }
    }

    public int getDefenderScore() {
        return defenderScore;
    }

    public void removePlayer(Player player) {
        gamePlayers.remove(player);

        if(gamePlayers.size() == 0) {
            endGame();
        }
    }

    public void teleportToSpawn(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        if (getAttackers().contains(player)) {
            player.teleport(map.getAttackerSpawns().get(MathUtils.ranNumber(0, map.getAttackerSpawns().size() - 1)));
        } else if (getDefenders().contains(player)) {
            player.teleport(map.getDefenderSpawns().get(MathUtils.ranNumber(0, map.getDefenderSpawns().size() - 1)));
        }

        if (PlayerProfile.getProfile(player).getLevel() == 0) {
            player.sendMessage(ChatColor.GOLD + "Here's a tip:");
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + ChatColor.translateAlternateColorCodes('&', tips.get(0)).replace("|", "\n"));
            player.sendMessage("");
        } else {
            player.sendMessage(ChatColor.GOLD + "Here's a tip:");
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + ChatColor.translateAlternateColorCodes('&', tips.get(MathUtils.ranNumber(0, tips.size() - 1))).replace("|", "\n"));
            player.sendMessage("");
        }

        PlayerUtils.equipLoadout(player);
    }

    public boolean isNotPlaying(Player player) {
        return !gamePlayers.contains(player);
    }

    public List<Player> getPlayers() {
        return gamePlayers;
    }

    public List<Player> getAttackers() {
        List<Player> attackers = new ArrayList<>();

        for (String name : attackerTeam.getEntries()) {
            if (Bukkit.getPlayer(name) != null) {
                attackers.add(Bukkit.getPlayer(name));
            }
        }

        return attackers;
    }

    public List<Player> getDefenders() {
        List<Player> defenders = new ArrayList<>();

        for (String name : defenderTeam.getEntries()) {
            if (Bukkit.getPlayer(name) != null) {
                defenders.add(Bukkit.getPlayer(name));
            }
        }

        return defenders;
    }

    public Team getTeam(Player player) {
        if (!gamePlayers.contains(player))
            return null;

        if (getAttackers().contains(player))
            return attackerTeam;

        if (getDefenders().contains(player))
            return defenderTeam;

        return null;
    }

    public boolean areTeammates(Player p1, Player p2) {
        return getTeam(p1) == getTeam(p2);
    }

    public Color getTeamColor(Player player) {
        if (getAttackers().contains(player))
            return Color.YELLOW;

        if (getDefenders().contains(player))
            return Color.PURPLE;

        return Color.WHITE;
    }

    public Color getTeamColor(Team team) {
        if (team == attackerTeam)
            return Color.YELLOW;

        if (team == defenderTeam)
            return Color.PURPLE;

        return Color.WHITE;
    }

    public ChatColor getTeamChatColor(Player player) {
        if (getAttackers().contains(player))
            return ChatColor.YELLOW;

        if (getDefenders().contains(player))
            return ChatColor.DARK_PURPLE;

        return ChatColor.WHITE;
    }

    public ChatColor getTeamChatColor(Team team) {
        if (team == attackerTeam)
            return ChatColor.YELLOW;

        else if (team == defenderTeam)
            return ChatColor.DARK_PURPLE;

        return ChatColor.WHITE;
    }

    public void broadcast(String message, boolean lobby) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (gamePlayers.contains(p) || (lobby && CastleSiege.getPlugin().getGame(p) == null)) {
                p.sendMessage(message);
            }
        }
    }

    public Team getAttackerTeam() {
        return attackerTeam;
    }

    public Team getDefenderTeam() {
        return defenderTeam;
    }
}