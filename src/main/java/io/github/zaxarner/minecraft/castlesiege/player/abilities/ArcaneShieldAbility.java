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
 * Created by JamesCZ98 on 12/14/2019.
 */
public class ArcaneShieldAbility extends ActiveAbility {


    public ArcaneShieldAbility() {
        super("arcane-shield");
    }

    List<Vector> particleOffsets = WorldUtils.getHollowCircleOffsets(1, 30);

    @Override
    public boolean use(Player player) {

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;


        for(Player p : AbilityUtils.getNearbyPlayers(player.getLocation(), getStat("range").doubleValue())) {
            if(PlayerUtils.isSameTeam(p, player)) {
                PlayerUtils.addPotionEffect(p, PotionEffectType.ABSORPTION, getStat("duration").intValue(), getStat("amplifier").intValue(), true);

                PlayerUtils.addCancelTask(p, new BukkitRunnable() {

                    int count = 0;
                    @Override
                    public void run() {
                        EffectUtils.playSound(Sound.BLOCK_BELL_RESONATE, p.getLocation(), .5f, 2f);
                        for(int i=0; i < particleOffsets.size(); i++) {
                            int finalI = i;
                            Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                                EffectUtils.displayDustParticle(p.getLocation().add(0.0, 1.0, 0.0).add(particleOffsets.get(finalI)), 1, Color.BLUE, 1f);
                            }, (long) Math.ceil((double) i * (20.0 / (double) particleOffsets.size())));
                        }
                        if(count >= getStat("duration").intValue() || !p.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                            this.cancel();
                        }
                        count++;
                    }
                }.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 20L).getTaskId());
            }
        }

        return true;
    }
}
