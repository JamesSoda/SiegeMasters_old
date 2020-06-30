package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created on 5/20/2020.
 */
public class SwapAbility extends ActiveAbility {

    public SwapAbility() {
        super("swap");
    }

    private List<Vector> particleOffsets = WorldUtils.getHollowSphereOffsets(2, 15);

    @Override
    public boolean use(Player player) {

        Player target = AbilityUtils.getTargetPlayer(player, getStat("range").intValue(), 1.25);

        if(target == null || PlayerUtils.isSameTeam(player, target))
            return false;

        if (!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        Location playerLoc = player.getLocation();
        Location targetLoc = target.getLocation();

        target.teleport(playerLoc);
        player.teleport(targetLoc);

        Location particleLocation = target.getLocation().add(0.0, .5, 0.0);
        EffectUtils.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, particleLocation, 1f, 1.5f);
        for (Vector v : particleOffsets) {
            particleLocation.add(v);
            EffectUtils.displayPotionParticle(particleLocation, 1, Color.BLACK);
            particleLocation.subtract(v);
        }
        particleLocation = player.getLocation().add(0.0, .5, 0.0);
        EffectUtils.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, particleLocation, 1f, 1.5f);
        for (Vector v : particleOffsets) {
            particleLocation.add(v);
            EffectUtils.displayPotionParticle(particleLocation, 1, Color.BLACK);
            particleLocation.subtract(v);
        }

        PlayerUtils.updateLatestAttacker(player, target);

        return true;
    }
}
