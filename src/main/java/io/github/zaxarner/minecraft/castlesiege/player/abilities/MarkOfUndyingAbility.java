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
 * Created by JamesCZ98 on 1/3/2020.
 */
public class MarkOfUndyingAbility extends ActiveAbility {


    public MarkOfUndyingAbility() {
        super("mark-of-undying");
    }

    private List<Vector> particleOffsets = WorldUtils.getHollowCircleOffsets(1, 30);

    @Override
    public boolean use(Player player) {

        if (!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        Player target = AbilityUtils.getTargetPlayer(player, getStat("range").intValue(), 1.25f);

        if(target == null)
            return false;

        if (!PlayerUtils.isSameTeam(player, target))
            return false;

        AbilityUtils.addStatusEffect(target, "mark-of-undying", getStat("duration").intValue());

        PlayerUtils.addCancelTask(target, new BukkitRunnable() {

            int count = 0;
            @Override
            public void run() {
                EffectUtils.playSound(Sound.BLOCK_BELL_RESONATE, target.getLocation(), .5f, 2f);
                for(int i=0; i < particleOffsets.size(); i++) {
                    int finalI = i;
                    Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                        EffectUtils.displayDustParticle(target.getLocation().add(0.0, 1.0, 0.0).add(particleOffsets.get(finalI)), 1, Color.BLUE, 1f);
                    }, (long) Math.ceil((double) i * (20.0 / (double) particleOffsets.size())));
                }
                if(count >= getStat("duration").intValue() || !AbilityUtils.hasStatusEffect(target, "mark-of-undying")) {
                    this.cancel();
                }
                count++;
            }
        }.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 20L).getTaskId());

        return true;
    }
}
