package io.github.zaxarner.minecraft.castlesiege.game.map;

import io.github.zaxarner.minecraft.castlesiege.game.Barricade;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 12/27/2019.
 */
public class CrumblingCastleMap extends Map {

    private List<Location> attackerSpawns = new ArrayList<>();
    private List<Location> defenderSpawns = new ArrayList<>();

    public CrumblingCastleMap() throws MapInstantiationException {
        super("Crumbling Castle");


        defenderSpawns.add(new Location(getWorld(), -6.5, 113, 29.5, 90, 0));
        defenderSpawns.add(new Location(getWorld(), -18.5, 106, 46.5, 180, -30));
        defenderSpawns.add(new Location(getWorld(), -32.5, 113, 28.5, -90, 0));

        attackerSpawns.add(new Location(getWorld(), 2.5, 113, -52.5, 90, 0));
        attackerSpawns.add(new Location(getWorld(), -15.5, 113, -58.5, 0, 0));
        attackerSpawns.add(new Location(getWorld(), -32.5, 113, -51.5, -90, 0));

        ArrayList<Barricade> barricades = new ArrayList<>();

        barricades.add(new Barricade(this, Barricade.generateBarricadeLocations(new Location(getWorld(), -24, 113, -4),
                BlockFace.NORTH, 4, 3)));
        barricades.add(new Barricade(this, Barricade.generateBarricadeLocations(new Location(getWorld(), -6, 113, -20),
                BlockFace.SOUTH, 4, 3)));
        barricades.add(new Barricade(this, Barricade.generateBarricadeLocations(new Location(getWorld(), -10, 113, -4),
                BlockFace.NORTH, 0, 2)));
        barricades.add(new Barricade(this, Barricade.generateBarricadeLocations(new Location(getWorld(), -20, 113, -20),
                BlockFace.NORTH, 0, 2)));
        setBarricades(barricades);

    }

    @Override
    public List<Location> getAttackerSpawns() {
        return attackerSpawns;
    }

    @Override
    public List<Location> getDefenderSpawns() {
        return defenderSpawns;
    }

    @Override
    public int getMaxPlayers() {
        return 18;
    }

    private List<Vector> fill(Vector start, Vector end) {
        List<Vector> coords = new ArrayList<>();
        for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
            for (int y = start.getBlockY(); y < end.getBlockY(); y++) {
                for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
                    coords.add(new Vector(x, y, z));
                }
            }
        }

        return coords;
    }
}