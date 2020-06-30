package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by JamesCZ98 on 8/6/2019.
 */
public class RepulseAbility extends ActiveAbility {

    public RepulseAbility() {
        super("repulse");
    }

    @Override
    public boolean use(Player player) {

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        RayTraceResult rayTrace = player.rayTraceBlocks(getStat("range").intValue());
        Block targetedBlock = null;

        if(rayTrace == null) {
            targetedBlock = AbilityUtils.getTargetBlock(player, getStat("range").intValue(), true);
            if (targetedBlock == null)
                return false;
        } else {
            targetedBlock = rayTrace.getHitBlock();
            if (targetedBlock == null)
                return false;
        }

        Location targetLoc = targetedBlock.getLocation();

        EffectUtils.displayParticle(Particle.CRIT_MAGIC, targetLoc, 100, 1.1f);
        EffectUtils.displayParticle(Particle.FIREWORKS_SPARK, targetLoc, 100, .3f);
        EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, targetLoc, 30, .03f);

        EffectUtils.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, targetLoc, 2f, 2f);
        EffectUtils.playSound(Sound.ENTITY_BAT_TAKEOFF, targetLoc, 2f, 1.5f);

        List<Entity> nearbyEntities = AbilityUtils.getNearbyEntities(targetLoc, getStat("radius").doubleValue());

        nearbyEntities.addAll(AbilityUtils.getNearbyPlayers(targetLoc, getStat("radius").doubleValue()));

        for(Entity ent : nearbyEntities) {
                EffectUtils.displayPotionParticle(ent.getLocation(), 15, Color.GRAY);
                AbilityUtils.knockback(ent, targetLoc, getStat("strength").doubleValue());
            if(ent == player)
                ent.setFallDistance(-5);
        }

        return true;
    }
}
