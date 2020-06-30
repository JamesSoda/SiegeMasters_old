package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by JamesCZ98 on 12/18/2019.
 */
public class BastionAbility extends ActiveAbility {

    public BastionAbility() {
        super("bastion");
    }

    private List<Vector> particleOffsets = WorldUtils.getHollowCircleOffsets(1, 50);

    @Override
    public boolean use(Player player) {

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        AbilityUtils.addStatusEffect(player, "bastion", getStat("duration").intValue());

        PlayerUtils.addCancelTask(player, new BukkitRunnable() {

            int count = 0;
            @Override
            public void run() {
                EffectUtils.playSound(Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, player.getLocation(), .5f, .1f);
                for(int i=0; i < particleOffsets.size(); i++) {
                    int finalI = i;
                    Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                        EffectUtils.displayDustParticle(player.getLocation().add(0.0, 1.0, 0.0).add(particleOffsets.get(finalI)), 1, Color.AQUA, 2f);
                    }, (long) Math.ceil((double) i * (20.0 / (double) particleOffsets.size())));
                }
                if(count >= getStat("duration").intValue()) {
                    this.cancel();
                }
                count++;
            }
        }.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 20L).getTaskId());


        return true;
    }
}
