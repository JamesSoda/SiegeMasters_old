package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by JamesCZ98 on 12/18/2019.
 */
public class SilenceAbility extends ActiveAbility {


    public SilenceAbility() {
        super("silence");
    }

    private static List<Vector> particleOffsets = WorldUtils.getHollowSphereOffsets(1, 30);

    @Override
    public boolean use(Player player) {


        Player target = AbilityUtils.getTargetPlayer(player, getStat("range").intValue(), 1.25);

        if(target == null)
            return false;

        if (PlayerUtils.isSameTeam(player, target))
            return false;

        if (!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        AbilityUtils.addStatusEffect(target, "silenced", getStat("duration").intValue());

        EffectUtils.playSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, target.getLocation(), 1f, 1f);
        PlayerUtils.addCancelTask(target, new BukkitRunnable() {

            int count = 0;

            @Override
            public void run() {
                EffectUtils.displayParticle(Particle.BARRIER, target.getEyeLocation().
                        add(particleOffsets.get(MathUtils.ranNumber(0, particleOffsets.size() - 1))), 1, .1f);
                if (count >= getStat("duration").intValue() - 1) {
                    this.cancel();
                }
                count++;
            }
        }.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 40L).getTaskId());

        return true;
    }
}
