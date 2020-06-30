package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by JamesCZ98 on 8/5/2019.
 */
public class RadianceAbility extends ActiveAbility {

    public RadianceAbility() {
        super("radiance");
    }

    @Override
    public boolean use(Player player) {

        Player target = AbilityUtils.getTargetPlayer(player, getStat("range").intValue(), 1.25);

        if(target == null || target == player)
            return false;

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;



        if(CastleSiege.getPlugin().getGame(player).areTeammates(player, target)) {

            EffectUtils.playSound(Sound.BLOCK_BEACON_ACTIVATE, player, 1f, 2f);

            PlayerUtils.healPlayer(target, getStat("amount").intValue(), player);
            EffectUtils.displayParticle(Particle.HEART, target.getEyeLocation(), 5, .1f);
            EffectUtils.displayDustParticle(target.getLocation(), 5, Color.LIME, 3f);

            return true;
        }
        return false;
    }
}
