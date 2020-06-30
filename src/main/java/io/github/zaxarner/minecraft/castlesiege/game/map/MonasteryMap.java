package io.github.zaxarner.minecraft.castlesiege.game.map;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 11/14/2019.
 */
public class MonasteryMap extends Map {


    private List<Location> attackerSpawns = new ArrayList<>();
    private List<Location> defenderSpawns = new ArrayList<>();

    public MonasteryMap() throws MapInstantiationException {
        super("Monastery");


        defenderSpawns.add(new Location(getWorld(), 79.5, 98, -145.5, -90, 0));
        defenderSpawns.add(new Location(getWorld(), 83.5, 98, -163.5, 0, 0));
        defenderSpawns.add(new Location(getWorld(), 86.5, 98, -188.5, -180, 0));
        defenderSpawns.add(new Location(getWorld(), 99.5, 93, -171.5, 90, 0));

        attackerSpawns.add(new Location(getWorld(), -59.5, 93, -164.5, -90, 0));
        attackerSpawns.add(new Location(getWorld(), -55, 93, -140.5, -135, 0));
        attackerSpawns.add(new Location(getWorld(), -51.5, 93, -171.5, 0, 0));
        attackerSpawns.add(new Location(getWorld(), -44.5, 98, -148.5, -90, 0));
    }

    @Override
    public int getMaxPlayers() { return 18; }

    @Override
    public List<Location> getAttackerSpawns() {

        return attackerSpawns;
    }

    @Override
    public List<Location> getDefenderSpawns() {

        return defenderSpawns;
    }

}
