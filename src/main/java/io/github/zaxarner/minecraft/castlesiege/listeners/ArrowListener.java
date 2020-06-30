package io.github.zaxarner.minecraft.castlesiege.listeners;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;


/**
 * Created by JamesCZ98 on 7/25/2019.
 */
public class ArrowListener implements Listener {


    private final int brazierWidth = 5;
    private final int brazierLength = 5;
    private final int brazierHeight = 5;


    private final List<Vector> brazierOffsets = WorldUtils.getCuboidOffsets(new int[]{brazierWidth, brazierHeight, brazierLength});

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            if (player == null)
                return;
            PlayerProfile profile = PlayerProfile.getProfile(player);

            if (event.getEntity() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getEntity();
                String name = "";

                for (String item : profile.getEquippedLoadout().getItemNames()) {
                    if (CastleSiege.getItemCreator().getItem(item).getType() == Material.ARROW) {
                        name = item;
                        break;
                    }
                }


                AbilityUtils.addStatusEffect(arrow, ChatColor.stripColor(name), 20);

                if (AbilityUtils.hasStatusEffect(arrow, "weightless-arrows"))
                    arrow.setGravity(false);

                boolean flaming = false;
                Location l = player.getEyeLocation().getBlock().getLocation().clone().add(1.0, 0, 1.0).subtract(new Vector(brazierWidth / 2.0, brazierHeight / 2.0, brazierLength / 2.0));
                for (Vector v : brazierOffsets) {
                    l.add(v);
                    if (l.getBlock().getType() == Material.BLAST_FURNACE) {
                        flaming = true;
                    }
                    l.subtract(v);
                }

                if (flaming) {
                    arrow.setFireTicks(Integer.MAX_VALUE);
                    arrow.setColor(Color.RED);
                    arrow.setMetadata("flaming", new FixedMetadataValue(CastleSiege.getPlugin(), ""));
                }

                ItemStack item = player.getInventory().getItemInMainHand();

                if (item.getType() == Material.CROSSBOW) {
                    arrow.setVelocity(arrow.getVelocity().multiply(1.75));
                }
            } else if (event.getEntity() instanceof Trident) {
                Trident trident = (Trident) event.getEntity();
                trident.setVelocity(trident.getVelocity().multiply(.7));
                trident.setBounce(true);
            }
        }
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {

            Arrow arrow = (Arrow) event.getEntity();

            Block block = event.getHitBlock();

            if (block == null) return;

            if (block.getType() == Material.SCAFFOLDING) {
                arrow.remove();
                block.breakNaturally();
            }

            if (Arrays.asList(WorldInteractionListener.glasses).contains(block.getType())) {
                arrow.remove();
                WorldUtils.breakGlass(block, 15);
            }

            if (arrow.hasMetadata("ricochet")) {
                Vector velocity = arrow.getVelocity();

                MetadataValue metadataValue = arrow.getMetadata("ricochet").get(0);
                int bouncesLeft = metadataValue.asInt();

                if (bouncesLeft <= 0) {
                    arrow.removeMetadata("ricochet", CastleSiege.getPlugin());
                    Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
                        if (!arrow.isDead())
                            arrow.remove();
                    }, 60 * 20L);
                    return;
                }

                Location arrowLocation = arrow.getLocation();
                BlockFace blockFace = event.getHitBlockFace();

                if (blockFace == null)
                    blockFace = BlockFace.UP;

                Vector normal = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
                double dotProduct = velocity.dot(normal);
                Vector newVelocity = velocity.subtract(normal.multiply(dotProduct).multiply(1.8));

                arrow.remove();

                Arrow newArrow = arrow.getWorld().spawnArrow(arrowLocation, newVelocity,
                        (float) newVelocity.length() * .8f, 3f);
                newArrow.setShooter(arrow.getShooter());

                newArrow.setMetadata("ricochet", new FixedMetadataValue(CastleSiege.getPlugin(), --bouncesLeft));

            }
        }
    }
}