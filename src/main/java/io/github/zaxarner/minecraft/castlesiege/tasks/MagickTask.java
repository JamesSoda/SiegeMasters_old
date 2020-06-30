package io.github.zaxarner.minecraft.castlesiege.tasks;

import io.github.zaxarner.minecraft.castlesiege.Attribute;
import io.github.zaxarner.minecraft.castlesiege.AttributeModifier;
import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.player.ability.Ability;
import io.github.zaxarner.minecraft.castlesiege.player.loadout.Loadout;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public class MagickTask extends BukkitRunnable {

    private static HashMap<Player, Integer> magickValues = new HashMap<>();

    private static final int MAX_MAGICK = 100;

    public MagickTask() {
        runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 10L);
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!magickValues.containsKey(p)) {
                magickValues.put(p, getMaxMagick(p));
            }
        }

        magickValues.keySet().removeIf(p -> p == null || !p.isOnline());

        for (Player p : magickValues.keySet()) {
            int magick = magickValues.get(p);

            if (magick + 1 < getMaxMagick(p)) {
                magick += 1;
            } else
                magick = getMaxMagick(p);

            magickValues.put(p, magick);
            refreshExpBar(p);
        }
    }

    private static int getMaxMagick(Player player) {
        int maxMagick = MAX_MAGICK;

        PlayerProfile playerProfile = PlayerProfile.getProfile(player);

        if(playerProfile != null) {
            Loadout loadout = playerProfile.getEquippedLoadout();

            if (loadout != null) {
                List<AttributeModifier> modifiers = loadout.getAttributeModifiers();


                for (AttributeModifier mod : modifiers) {
                    if (mod.getAttribute() == Attribute.MAX_MAGICK) {
                        maxMagick += mod.getValue();
                    }
                }
            }
        }

        return maxMagick;
    }

    private static void refreshExpBar(Player player) {
        if(CastleSiege.getPlugin().getGame(player) != null) {
            int magick = magickValues.get(player);
            player.setExp(MathUtils.clamp(magick / (float) MAX_MAGICK, 0f, 1f));
            player.setLevel(magick);
        } else {
            player.setExp(0f);
            player.setLevel(0);
        }
    }

    public static boolean removeMagick(Player player, int count) {
        int magick = magickValues.get(player);
        if (magick < count) {
            EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, player, 3f, .1f);
            return false;
        }

        magickValues.put(player, magick - count);
        refreshExpBar(player);

        return true;
    }

    public static void resetMagick(Player player) {
        magickValues.put(player, MAX_MAGICK);
        refreshExpBar(player);
    }

    public static int getMagick(Player player) {
        return magickValues.get(player);
    }

}
