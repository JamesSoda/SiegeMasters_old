package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by JamesCZ98 on 8/9/2019.
 */
public class DefianceAbility extends ActiveAbility {


    public DefianceAbility() {
        super("defiance");

    }

    private List<Vector> particleOffsets = WorldUtils.getHollowSphereOffsets(1, 20);

    @Override
    public boolean use(Player player) {

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        AbilityUtils.addStatusEffect(player, "Defiance", getStat("duration").intValue());


        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {

                if(player.getGameMode() == GameMode.SPECTATOR) {
                    this.cancel();
                }

                EffectUtils.playSound(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, player.getLocation(), .25f, 2f);
                if(count >= 4 * getStat("duration").intValue())
                    this.cancel();

                count++;
                Location loc = player.getEyeLocation();
                for(Vector v : particleOffsets) {
                    loc.add(v);
                    EffectUtils.displayParticle(Particle.CRIT_MAGIC, loc, 1, 0f);
                    loc.subtract(v);
                }
            }
        }.runTaskTimer(CastleSiege.getPlugin(), 0L, 5L);

        return true;
    }
}
