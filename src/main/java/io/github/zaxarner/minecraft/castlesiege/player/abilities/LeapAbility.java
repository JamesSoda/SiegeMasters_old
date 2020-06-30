package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by JamesCZ98 on 7/31/2019.
 */
public class LeapAbility extends ActiveAbility {

    public LeapAbility() {
        super("leap");
    }


    @Override
    public boolean use(Player player) {

        if(!PlayerUtils.isGrounded(player))
            return false;

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;


        EffectUtils.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, player.getLocation(), .5f, 1.5f);
        EffectUtils.displayDustParticle(player.getLocation(), 5, Color.GRAY, 3);
        EffectUtils.displayParticle(Particle.SMOKE_LARGE, player.getLocation(), 10, 0f);
        EffectUtils.displayCampfireParticle(player.getLocation(), 1f);

        player.setVelocity(player.getVelocity().setY(getStat("strength").doubleValue()));
        player.setFallDistance(-4);
        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> player.setFallDistance(-10), 5L);



        return true;
    }
}
