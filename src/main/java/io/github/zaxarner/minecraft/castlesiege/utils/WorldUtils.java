package io.github.zaxarner.minecraft.castlesiege.utils;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 8/1/2019.
 */
public class WorldUtils {

    public static void breakGlass(Block block, int chance) {
        if (block == null || block.getType() == Material.AIR) return;

        Material type = block.getType();

        block.breakNaturally();

        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, 5f, 1f);


        block.getWorld().playEffect(block.getLocation().add(MathUtils.ranDouble(-.1, .1), MathUtils.ranDouble(-.1, .25),
                MathUtils.ranDouble(-.1, .1)), Effect.STEP_SOUND, Material.GLASS);

        if (MathUtils.ranNumber(0, 100) < chance) {

            if (block.getRelative(BlockFace.UP).getType() == type)
                breakGlass(block.getRelative(BlockFace.UP), chance);

            if (block.getRelative(BlockFace.DOWN).getType() == type)
                breakGlass(block.getRelative(BlockFace.DOWN), chance);

            if (block.getRelative(BlockFace.NORTH).getType() == type)
                breakGlass(block.getRelative(BlockFace.NORTH), chance);

            if (block.getRelative(BlockFace.SOUTH).getType() == type)
                breakGlass(block.getRelative(BlockFace.SOUTH), chance);

            if (block.getRelative(BlockFace.EAST).getType() == type)
                breakGlass(block.getRelative(BlockFace.EAST), chance);

            if (block.getRelative(BlockFace.WEST).getType() == type)
                breakGlass(block.getRelative(BlockFace.WEST), chance);
        }
    }

    public static String getLocationString(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    public static Location getLocationFromString(String string) {
        string = string.trim();

        if (string.equalsIgnoreCase(""))
            return null;

        String[] parts = string.split(":");

        if (parts.length == 4) {
            World world = Bukkit.getServer().getWorld(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);

            return new Location(world, x, y, z);
        }

        return null;
    }

    public static boolean compare(Location loc1, Location loc2) {
        return loc1.getWorld() == loc2.getWorld() && loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static List<Vector> getSphereOffsets(int radius) {
        return getSphereOffsets(radius, 5);
    }

    public static List<Vector> getSphereOffsets(int radius, int degreeChange) {
        List<Vector> sphere = new ArrayList<>();

        for(int i=0; i < radius; i++) {
            for (int azimuthalAngle = 0; azimuthalAngle < 360; azimuthalAngle += degreeChange) {
                for (int polarAngle = 0; polarAngle < 180; polarAngle += degreeChange) {
                    double x = Math.sin(Math.toRadians(polarAngle)) * Math.cos(Math.toRadians(azimuthalAngle)) * radius;
                    double y = Math.sin(Math.toRadians(polarAngle)) * Math.sin(Math.toRadians(azimuthalAngle)) * radius;
                    double z = Math.cos(Math.toRadians(polarAngle)) * radius;
                    sphere.add(new Vector(x, y, z));
                }
            }
        }

        return sphere;
    }

    public static List<Vector> getHollowSphereOffsets(int radius) {
        return getHollowSphereOffsets(radius, 5);
    }

    public static List<Vector> getHollowSphereOffsets(int radius, int degreeChange) {
        List<Vector> sphere = new ArrayList<>();

        for (int azimuthalAngle = 0; azimuthalAngle < 360; azimuthalAngle += degreeChange) {
            for (int polarAngle = 0; polarAngle < 180; polarAngle += degreeChange) {
                double x = Math.sin(Math.toRadians(polarAngle)) * Math.cos(Math.toRadians(azimuthalAngle)) * radius;
                double y = Math.sin(Math.toRadians(polarAngle)) * Math.sin(Math.toRadians(azimuthalAngle)) * radius;
                double z = Math.cos(Math.toRadians(polarAngle)) * radius;
                sphere.add(new Vector(x, y, z));
            }
        }

        return sphere;
    }

    public static List<Vector> getCircleOffsets(int radius, int degreeChange) {
        List<Vector> circle = new ArrayList<>();

        for(int i=0; i < radius; i++) {
            for (int degree = 0; degree < 360; degree += degreeChange) {
                double radians = Math.toRadians(degree);
                double x = Math.cos(radians) * radius;
                double z = Math.sin(radians) * radius;

                circle.add(new Vector(x, 0, z));
            }
        }

        return circle;
    }

    public static List<Vector> getHollowCircleOffsets(int radius) {
        return getHollowCircleOffsets(radius, 5);
    }

    public static List<Vector> getHollowCircleOffsets(int radius, int degreeChange) {
        List<Vector> circle = new ArrayList<>();

        for (int degree = 0; degree < 360; degree += degreeChange) {
            double radians = Math.toRadians(degree);
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;

            circle.add(new Vector(x, 0, z));
        }

        return circle;
    }

    /**
     * Calculates a list of offsets given a dimension.
     * @param dimensions an array of length 3
     * @return
     */
    public static List<Vector> getCuboidOffsets(int[] dimensions) {
        if (dimensions.length != 3) {
            return new ArrayList<>(0);
        }

        List<Vector> offsets = new ArrayList<>();


        for (int x = 0; x < Math.abs(dimensions[0]); x++) {
            for (int y = 0; y < Math.abs(dimensions[1]); y++) {
                for (int z = 0; z < Math.abs(dimensions[2]); z++) {
                    int xOffset = x;
                    int yOffset = y;
                    int zOffset = z;
                    if(dimensions[0] < 0)
                        xOffset *= -1;
                    if(dimensions[1] < 0)
                        yOffset *= -1;
                    if(dimensions[2] < 0)
                        zOffset *= -1;
                    offsets.add(new Vector(xOffset, yOffset, zOffset));
                }
            }
        }


        return offsets;
    }

    public static List<Vector> getSquareOffsets(int width, int height, BlockFace direction) {

        List<Vector> offsets = new ArrayList<>();

        if(width > 0) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    if (direction == BlockFace.NORTH) {
                        offsets.add(new Vector(0, y, -x));
                    } else if (direction == BlockFace.SOUTH) {
                        offsets.add(new Vector(0, y, x));
                    } else if (direction == BlockFace.WEST) {
                        offsets.add(new Vector(-x, y, 0));
                    } else if (direction == BlockFace.EAST) {
                        offsets.add(new Vector(x, y, 0));
                    }

                }
            }
        } else {
            for(int y = 0; y < height; y++) {
                offsets.add(new Vector(0, y, 0));
            }
        }

        return offsets;
    }

    public static List<Vector> getHollowCuboidOffsets(int[] dimensions) {
        if (dimensions.length != 3) {
            return new ArrayList<>(0);
        }

        List<Vector> offsets = new ArrayList<>();
        for (int x = 0; x < dimensions[0]; x++) {
            for (int y = 0; y < dimensions[1]; y++) {
                for (int z = 0; z < dimensions[2]; z++) {
                    if (x == 0 || y == 0 || z == 0 || x == dimensions[0] - 1 || y == dimensions[1] - 1 || z == dimensions[2] - 1) {
                        offsets.add(new Vector(x, y, z));
                    }
                }
            }
        }
        return offsets;
    }

    public static List<Vector> getHollowRectangleOffsets(int[] dimensions) {
        return getHollowRectangleOffsets(dimensions, 1.0);
    }

    public static List<Vector> getHollowRectangleOffsets(int[] dimensions, double change) {
        if (dimensions.length != 2) {
            return new ArrayList<>(0);
        }

        List<Vector> offsets = new ArrayList<>();
        for (double x = 0.0; x < dimensions[0]; x += change) {
            for (double z = 0.0; z < dimensions[1]; z += change) {
                if (x == 0 || z == 0 || x == dimensions[0] - 1 || z == dimensions[1] - 1) {
                    offsets.add(new Vector(x, 0, z));
                }
            }
        }
        return offsets;
    }

    public static boolean copyFileStructure(File source, File target) {

        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static World createWorld(String worldName) {

        WorldCreator worldCreator = new WorldCreator(worldName);

        return worldCreator.createWorld();
    }

    public static void deleteWorld(World world) {
        if (world != null) {
            CastleSiege.log("Deleting world...", Level.INFO);

            for (Player p : world.getPlayers()) {
                PlayerUtils.cancelTasks(p);
                if (p.getGameMode() != GameMode.CREATIVE)
                    p.getInventory().clear();
                p.teleport(CastleSiege.getSpawn());
            }

            try {

                Files.walk(world.getWorldFolder().toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);

                CastleSiege.log("Deleted files.", Level.INFO);
                Bukkit.getServer().unloadWorld(world, false);
                CastleSiege.log("Unloaded world.", Level.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Location getAveragePosition(List<Location> locs) {

        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        for(Location l : locs) {
            x += l.getX();
            y += l.getY();
            z += l.getZ();
        }

        return new Location(locs.get(0).getWorld(), x/locs.size(), y/locs.size(), z/locs.size());
    }

    public static Location getCenter(Location loc) {
        Location result = loc.clone();

        result.setX(result.getBlockX() + .5);
        result.setY(result.getBlockY() + .5);
        result.setZ(result.getBlockZ() + .5);

        return result;
    }
}