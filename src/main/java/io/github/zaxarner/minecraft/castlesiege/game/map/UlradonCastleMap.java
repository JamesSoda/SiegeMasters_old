package io.github.zaxarner.minecraft.castlesiege.game.map;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 11/7/2019.
 */
public class UlradonCastleMap extends Map {

    private List<Location> attackerSpawns = new ArrayList<>();
    private List<Location> defenderSpawns = new ArrayList<>();

    public UlradonCastleMap() throws MapInstantiationException {
        super("game");


        attackerSpawns.add(new Location(getWorld(), 30.5, 104, -127.5, 90, 0));
        defenderSpawns.add(new Location(getWorld(), -63.5, 114, -152.5, 90, 0));
    }

    @Override
    public int getMaxPlayers() {
        return 18;
    }

    @Override
    public List<Location> getAttackerSpawns() {

        return attackerSpawns;
    }

    @Override
    public List<Location> getDefenderSpawns() {

        return defenderSpawns;
    }
}