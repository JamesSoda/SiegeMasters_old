package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 8/6/2019.
 */
public class TombAbility extends ActiveAbility {

    public TombAbility() {
        super("tomb");
    }

    private List<Vector> blockOffsets = WorldUtils.getHollowSphereOffsets(getStat("radius").intValue(), 10);

    @Override
    public boolean use(Player player) {

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        List<Block> blocksChanged = new ArrayList<>();

        Location loc = player.getLocation();
        EffectUtils.playSound(Sound.BLOCK_STONE_PLACE, loc, 1f, 1f);
        for(Vector v : blockOffsets) {
            loc.add(v);
            if(loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(Material.OBSIDIAN);
                blocksChanged.add(loc.getBlock());
            }
            loc.subtract(v);
        }

        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {

            EffectUtils.playSound(Sound.BLOCK_STONE_BREAK, player.getLocation(), 1f, 1f);
            for(Block b : blocksChanged) {
                if(b.getType() == Material.OBSIDIAN) {
                    b.setType(Material.AIR);
                }
            }

        }, getStat("duration").intValue() * 20L);

        return true;
    }

}
