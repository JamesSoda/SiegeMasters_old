package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public class DisengageAbility extends ActiveAbility {

    public DisengageAbility() {
        super("disengage");
    }



    @Override
    public boolean use(Player player) {
        if(!PlayerUtils.isGrounded(player))
            return false;

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        Location loc = player.getLocation();
        loc.setPitch(0f);
        Vector dir = loc.getDirection();


        EffectUtils.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, loc, .5f, 1f);
        EffectUtils.displayDustParticle(loc, 5, Color.GRAY, 3);
        EffectUtils.displayParticle(Particle.SMOKE_LARGE, player.getLocation(), 10, 0f);
        EffectUtils.displayParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 50, .125f);


        Vector vel = new Vector(dir.getX(), 0f, dir.getZ());
        vel = vel.normalize();
        player.setVelocity(vel.multiply(-1 * getStat("strength").doubleValue()).setY(getStat("strength").doubleValue() / 2.0));
        player.setFallDistance(-10f);

        return true;
    }
}
