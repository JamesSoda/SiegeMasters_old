package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by JamesCZ98 on 12/18/2019.
 */
public class EnsnareAbility extends ActiveAbility implements Listener {

    public EnsnareAbility() {
        super("ensnare");
    }

    private static List<Vector> particleOffsets = WorldUtils.getHollowSphereOffsets(1, 15);

    @Override
    public boolean use(Player player) {

        Player target = AbilityUtils.getTargetPlayer(player, getStat("range").intValue(), 1.25);

        if(target == null)
            return false;

        if (PlayerUtils.isSameTeam(player, target))
            return false;

        if (!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        AbilityUtils.addStatusEffect(target, "snared", getStat("duration").intValue());
        target.sendMessage("");
        target.sendMessage(ChatColor.RED + "You have been snared!");
        target.sendMessage("");

        PlayerUtils.addCancelTask(target, new BukkitRunnable() {

            int count = 0;
            @Override
            public void run() {
                EffectUtils.playSound(Sound.ENTITY_ENDERMITE_DEATH, target.getLocation(), .5f, .5f);
                for(int i=0; i < particleOffsets.size(); i++) {
                    int finalI = i;
                    Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {
                        EffectUtils.displayParticle(Particle.CRIT_MAGIC, target.getLocation().add(0.0, 1.0, 0.0).add(particleOffsets.get(finalI)), 1, 0f);
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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(AbilityUtils.hasStatusEffect(event.getPlayer(), "snared")){
            event.setCancelled(true);
        }
    }
}
