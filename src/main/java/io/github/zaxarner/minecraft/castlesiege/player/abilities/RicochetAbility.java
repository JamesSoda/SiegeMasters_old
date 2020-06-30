package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Created by JamesCZ98 on 8/5/2019.
 */
public class RicochetAbility extends ActiveAbility {

    public RicochetAbility() {
        super("ricochet");
    }

    @Override
    public boolean use(Player player) {
        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        Vector direction = player.getLocation().getDirection().multiply(getStat("speed").doubleValue());

        EffectUtils.playSound(Sound.ENTITY_VILLAGER_WORK_FLETCHER, player.getLocation(), 1f, 1f);
        for(int i=0; i < getStat("arrows").intValue(); i++) {
            Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(), direction,
                    (float) direction.length(), getStat("spread").floatValue());

            //Arrow arrow = player.launchProjectile(Arrow.class, direction);
            arrow.setShooter(player);

            arrow.setMetadata("ricochet", new FixedMetadataValue(CastleSiege.getPlugin(), getStat("bounces").intValue()));
        }

        return true;
    }
}