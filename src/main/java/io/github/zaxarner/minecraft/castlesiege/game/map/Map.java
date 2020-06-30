package io.github.zaxarner.minecraft.castlesiege.game.map;

import io.github.zaxarner.minecraft.castlesiege.game.Barricade;
import io.github.zaxarner.minecraft.castlesiege.utils.MathUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.omg.PortableInterceptor.INACTIVE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 11/6/2019
 */
public abstract class Map {

    private String name;

    public abstract List<Location> getAttackerSpawns();
    public abstract List<Location> getDefenderSpawns();

    public abstract int getMaxPlayers();

    private List<Barricade> barricades = new ArrayList<>();

    private World world;

    public Map(String name) throws MapInstantiationException {
        this.name = name;

        String worldName = name.replace(" ", "_");

        if(Bukkit.getWorld(worldName) != null)
            throw new MapInstantiationException("World already exists! [" + worldName + "]");

        World originalWorld = Bukkit.getWorld(worldName + "_original");
        if (originalWorld == null)
            throw new MapInstantiationException("Original world does not exist! [" + worldName + "]");

        File target = new File(Bukkit.getWorldContainer(), worldName);
        File source = originalWorld.getWorldFolder();
        WorldUtils.copyFileStructure(source, target);

        world = WorldUtils.createWorld(worldName);
        if (world == null)
            throw new MapInstantiationException("Game world could not be created! [" + worldName + "]");

        int random = MathUtils.ranNumber(0, 100);
        if(random < 30) {
            world.setTime(0);
        } else if(random < 60) {
            world.setTime(6000);
        } else if(random < 90) {
            world.setTime(12000);
        } else {
            world.setTime(18000);
        }

        random = MathUtils.ranNumber(0, 10);
        if(random <= 9) {
            world.setStorm(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setThunderDuration(Integer.MAX_VALUE);
        } else {
            world.setStorm(true);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setThunderDuration(Integer.MAX_VALUE);
        }


    }

    public World getWorld() {
        return this.world;
    }

    public String getName() { return name; }

    public List<Barricade> getBarricades() {
        return barricades;
    }

    public void setBarricades(List<Barricade> barricades) {
        this.barricades = barricades;
    }
}
