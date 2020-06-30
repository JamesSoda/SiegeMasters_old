package io.github.zaxarner.minecraft.castlesiege.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public class EffectUtils {


    public static void playSound(Sound sound, Location loc, float volume, float pitch) {
        World world = loc.getWorld();

        if(world == null) return;

        world.playSound(loc, sound, volume, pitch);
    }

    public static void playSound(Sound sound, Player player, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void displayParticle(Particle particle, Location loc, int count, float speed) {

        World world = loc.getWorld();

        if(world == null) return;

        world.spawnParticle(particle, loc, count, 0f, 0f, 0f, speed, null, true);

    }

    public static void displayBlockBreakParticle(Location loc, Material mat) {
        World world = loc.getWorld();

        if(world == null) return;

        world.spawnParticle(Particle.BLOCK_CRACK, loc, 10, mat.createBlockData());
    }

    public static void displayCampfireParticle(Location loc, float verticalSpeed) {
        World world = loc.getWorld();

        if(world == null) return;

        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 0, 0f, verticalSpeed, 0f, .1f, null, true);
    }

    public static void displayDustParticle(Location loc, int count, Color color, float particleSize) {

        World world = loc.getWorld();

        if(world == null) return;

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, particleSize);

        world.spawnParticle(Particle.REDSTONE, loc, count, 0f, 0f, 0f, 0, dustOptions, true);
    }

    public static void displayPotionParticle(Location loc, int count, Color color) {

        World world = loc.getWorld();

        if(world == null) return;

        for(int i=0; i < count; i++) {
            world.spawnParticle(Particle.SPELL_MOB, loc, 0, color.getRed() / 255, color.getGreen() / 255, color.getBlue() / 255, 1, null, true);
        }
    }


}
