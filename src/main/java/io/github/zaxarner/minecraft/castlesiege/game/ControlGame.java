package io.github.zaxarner.minecraft.castlesiege.game;

import io.github.zaxarner.minecraft.castlesiege.game.map.Map;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JamesCZ98 on 12/24/2019.
 */
public class ControlGame extends Game {

    private List<ControlPoint> controlPoints = new ArrayList<>();

    public ControlGame(Map map) {
        super(map);
    }

    @Override
    public String getGameTypeName() {
        return "Control";
    }

    @Override
    public int getRespawnTime() {
        return 5;
    }

    @Override
    public int getTimeLimit() {
        return 10 * 60;
    }

    @Override
    public int getScoreTarget() {
        return 600;
    }

    @Override
    public void handleGameTick() {
        int defenderOldScore = getDefenderScore();
        int attackerOldScore = getAttackerScore();
        int defenderCaps = 0;
        int attackerCaps = 0;
        for (ControlPoint point : controlPoints) {
            point.handleTick();

            if (point.getControllingTeam() == getAttackerTeam() && point.getCapturingTeam() == null) {
                addAttackerScore();
                attackerCaps++;
            }
            else if (point.getControllingTeam() == getDefenderTeam() && point.getCapturingTeam() == null) {
                addDefenderScore();
                defenderCaps++;
            }
        }

        int defenderNewScore = getDefenderScore();
        int attackerNewScore = getAttackerScore();

        int halfway = getScoreTarget() / 2;

        if (attackerNewScore >= halfway && attackerOldScore < halfway) {
            broadcast("", false);
            broadcast(ChatColor.GOLD + "The " + getAttackerTeam().getDisplayName() + ChatColor.GOLD + " are halfway to Victory!", false);
            broadcast("", false);
        } else if (attackerNewScore >= getScoreTarget() - (3 * attackerCaps)) {
            for (Player p : getPlayers()) {
                EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, p, 0.5f, .5f);
            }
        }

        if (defenderNewScore >= halfway && defenderOldScore < halfway) {
            broadcast("", false);
            broadcast(ChatColor.GOLD + "The " + getDefenderTeam().getDisplayName() + ChatColor.GOLD + " are halfway to Victory!", false);
            broadcast("", false);
        } else if (defenderNewScore >= getScoreTarget() - (3 * defenderCaps)) {
            for (Player p : getPlayers()) {
                EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, p, 0.5f, .5f);
            }
        }
    }

    @Override
    public void leaveGame(Player player) {
        for (ControlPoint point : controlPoints)
            point.getBossBar().removePlayer(player);

        super.leaveGame(player);
    }

    public void setControlPoints(ControlPoint... points) {
        this.controlPoints = Arrays.asList(points);
    }
}