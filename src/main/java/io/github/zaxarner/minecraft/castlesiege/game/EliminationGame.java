package io.github.zaxarner.minecraft.castlesiege.game;

import io.github.zaxarner.minecraft.castlesiege.game.map.Map;

/**
 * Created by JamesCZ98 on 3/7/2020.
 */
public class EliminationGame extends Game {

    public EliminationGame(Map map) {
        super(map);
    }

    @Override
    public String getGameTypeName() {
        return "Elimination";
    }

    @Override
    public int getRespawnTime() {
        return -1;
    }

    @Override
    public int getTimeLimit() {
        return 2 * 60;
    }

    @Override
    public int getScoreTarget() {
        return 3;
    }

    @Override
    public void handleGameTick() {

    }
}
