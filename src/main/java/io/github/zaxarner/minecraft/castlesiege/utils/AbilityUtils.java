package io.github.zaxarner.minecraft.castlesiege.utils;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public class AbilityUtils {


    public static Player getTargetPlayer(Player player, int range, double precision) {

        Player target = null;

        if(player.getLocation().getY() < 0)
            return null;

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection();

        World world = loc.getWorld();
        if(world == null) return null;

        for(int i=0; i <= range; i++) {
            loc.add(dir.getX() * i, dir.getY() * i, dir.getZ() * i);
            for(Entity entity : world.getNearbyEntities(loc, precision, precision, precision)) {
                if(entity instanceof Player && ((Player) entity).getGameMode() == GameMode.SURVIVAL) {
                    target = (Player) entity;
                }
            }
            loc.subtract(dir.getX() * i, dir.getY() * i, dir.getZ() * i);
        }

        if(target == null)
            return null;

        if(target.getGameMode() != GameMode.SURVIVAL)
            return null;

        if(target.hasPotionEffect(PotionEffectType.INVISIBILITY)) return null;

        if(target == player)
            return null;

        return target;
    }

    private static HashMap<Entity, List<String>> statuses = new HashMap<>();

    public static void addStatusEffect(Entity entity, String status, int duration) {
        if(hasStatusEffect(entity, status))
            return;

        List<String> playerStatuses = statuses.get(entity);

        if(playerStatuses == null) {
            playerStatuses = new ArrayList<>();
        }

        playerStatuses.add(status);

        statuses.put(entity, playerStatuses);

        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {

            removeStatusEffect(entity, status);

        }, duration * 20L);
    }

    public static void removeStatusEffect(Entity entity, String status) {
        if(!hasStatusEffect(entity, status))
            return;

        List<String> playerStatuses = statuses.get(entity);

        if(playerStatuses == null) {
            playerStatuses = new ArrayList<>();
        }


        playerStatuses.remove(status);

        statuses.put(entity, playerStatuses);
    }

    public static boolean hasStatusEffect(Entity entity, String status) {

        List<String> playerStatuses = statuses.get(entity);

        if(playerStatuses == null) {
            playerStatuses = new ArrayList<>();
        }

        return playerStatuses.contains(status);
    }

    public static Block getTargetBlock(Player player, int range, boolean includeAir) {
        if (player.getLocation().getY() < 0)
            return player.getLocation().getBlock();

        BlockIterator blockIterator = new BlockIterator(player.getLocation(), 1.75, range);
        Block lastBlock = blockIterator.next();

        Block lastAirBlock = null;

        while (blockIterator.hasNext()) {
            lastBlock = blockIterator.next();
            if(lastBlock.getType() == Material.AIR) {
                lastAirBlock = lastBlock;
                continue;
            }

            break;
        }

        if (lastBlock.getType() == Material.AIR) {
            if (includeAir)
                return lastAirBlock;
        } else
            return lastAirBlock;

        return null;
    }

    public static List<Player> getNearbyPlayers(Location location, double range) {

        List<Player> players = new ArrayList<>();

        World world = location.getWorld();
        if(world == null)
            return players;

        for(Entity ent : world.getNearbyEntities(location, range, range, range)) {
            if(ent instanceof Player && ((Player) ent).getGameMode() == GameMode.SURVIVAL)
                players.add((Player) ent);
        }

        return players;
    }

    public static List<Entity> getNearbyEntities(Location location, double range) {

        List<Entity> entities = new ArrayList<>();

        World world = location.getWorld();
        if(world == null)
            return entities;

        entities.addAll(world.getNearbyEntities(location, range, range, range));

        List<Entity> players = new ArrayList<>();
        entities.stream().filter(ent -> ent instanceof Player).forEach(players::add);

        entities.removeAll(players);

        return entities;
    }

    public static List<Entity> getNearbyEntities(Location location, double xRange, double yRange, double zRange) {

        List<Entity> entities = new ArrayList<>();

        World world = location.getWorld();
        if(world == null)
            return entities;

        entities.addAll(world.getNearbyEntities(location, xRange, yRange, zRange));

        List<Entity> players = new ArrayList<>();
        entities.stream().filter(ent -> ent instanceof Player).forEach(players::add);

        entities.removeAll(players);

        return entities;
    }

    public static void knockback(Entity entity, Location sourceLocation, double strength) {

        if(entity instanceof ArmorStand)
            return;

        Vector direction = entity.getLocation().toVector().subtract(sourceLocation.toVector()).normalize();

        entity.setVelocity(direction.multiply(strength));
    }

    public static void slow(Player player, double percentage) {

    }
}