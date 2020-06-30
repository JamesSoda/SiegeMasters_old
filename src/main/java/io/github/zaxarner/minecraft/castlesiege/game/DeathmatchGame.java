package io.github.zaxarner.minecraft.castlesiege.game;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.map.Map;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import java.util.Iterator;

/**
 * Created by JamesCZ98 on 11/11/2019.
 */
public class DeathmatchGame extends Game {

    public DeathmatchGame(Map map) {
        super(map);
    }

    @Override
    public String getGameTypeName() {
        return "Deathmatch";
    }

    @Override
    public int getRespawnTime() {
        return 3;
    }

    @Override
    public int getTimeLimit() {
        return 10 * 60;
    }

    @Override
    public int getScoreTarget() {
        return 30;
    }

    @Override
    public void handleGameTick() {}
}


/*



 */