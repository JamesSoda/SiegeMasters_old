package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public class WarpAbility extends ActiveAbility {

    public WarpAbility() {
        super("warp");
    }

    private List<Vector> particleOffsets = WorldUtils.getHollowSphereOffsets(2, 15);


    @Override
    public boolean use(Player player) {

        RayTraceResult rayTrace = player.rayTraceBlocks(getStat("range").intValue());

        Location targetedLocation = null;

        if(rayTrace == null) {
            Block targetedBlock = AbilityUtils.getTargetBlock(player, getStat("range").intValue(), true);
            if(targetedBlock == null)
                return false;
            targetedLocation = targetedBlock.getLocation();
        } else {

            Vector hit = rayTrace.getHitPosition();

            targetedLocation = new Location(player.getWorld(), hit.getX(), hit.getY(), hit.getZ());
            Block targetedBlock = rayTrace.getHitBlock();
            BlockFace targetedFace = rayTrace.getHitBlockFace();


            if (targetedBlock != null && targetedFace != null) {
                targetedLocation = WorldUtils.getCenter(targetedBlock.getRelative(targetedFace).getLocation()).subtract(0.0, .5, 0.0);
            }
        }

        if (!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();

        Location particleLocation = player.getLocation().add(0.0, .5, 0.0);
        for (Vector v : particleOffsets) {
            particleLocation.add(v);
            EffectUtils.displayPotionParticle(particleLocation, 1, Color.BLACK);
            particleLocation.subtract(v);
        }

        EffectUtils.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, particleLocation, 1f, 1.5f);

        targetedLocation.setPitch(pitch);
        targetedLocation.setYaw(yaw);

        player.setFallDistance((player.getFallDistance() - 5) / 2);
        player.teleport(targetedLocation);

        particleLocation = player.getLocation().add(0.0, .5, 0.0);
        for (Vector v : particleOffsets) {
            particleLocation.add(v);
            EffectUtils.displayPotionParticle(particleLocation, 1, Color.BLACK);
            particleLocation.subtract(v);
        }

        EffectUtils.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, particleLocation, 1f, 1.5f);

        return true;
    }
}
