package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by JamesCZ98 on 12/16/2019.
 */
public class SingeAbility extends ActiveAbility {

    public SingeAbility() {
        super("singe");
    }

    List<Vector> particleOffsets = WorldUtils.getHollowCircleOffsets(1, 30);


    @Override
    public boolean use(Player player) {

        if (!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;


        Location loc = player.getLocation();
        EffectUtils.displayParticle(Particle.FLAME, loc, 200, .1f);
        EffectUtils.playSound(Sound.ENTITY_BLAZE_SHOOT, loc, 1f, .2f);
        EffectUtils.playSound(Sound.BLOCK_FIRE_AMBIENT, loc, 1f, 1f);


        for(Player p : AbilityUtils.getNearbyPlayers(loc, getStat("radius").intValue())) {
            if(!PlayerUtils.isSameTeam(player, p)) {
                PlayerUtils.damagePlayer(p, 0, player, EntityDamageEvent.DamageCause.FIRE);
                p.setFireTicks(20 * getStat("duration").intValue());

                PlayerUtils.addCancelTask(p, new BukkitRunnable() {

                    int count = 0;
                    @Override
                    public void run() {
                        EffectUtils.playSound(Sound.BLOCK_CAMPFIRE_CRACKLE, p.getLocation(), .5f, .5f);
                        for(int i=0; i < particleOffsets.size(); i++) {
                            int finalI = i;
                            Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                                EffectUtils.displayParticle(Particle.FLAME, p.getLocation().add(0.0, 1.0, 0.0).add(particleOffsets.get(finalI)), 1, 0f);
                            }, (long) Math.ceil((double) i * (10.0 / (double) particleOffsets.size())));
                        }
                        if(count >= getStat("duration").intValue() * 2 || p.getFireTicks() == 0) {
                            this.cancel();
                        }
                        count++;
                    }
                }.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 10L).getTaskId());

            }
        }




        return true;
    }
}

