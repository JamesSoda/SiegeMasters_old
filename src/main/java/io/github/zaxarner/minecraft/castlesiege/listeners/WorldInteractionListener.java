package io.github.zaxarner.minecraft.castlesiege.listeners;

import io.github.zaxarner.minecraft.castlesiege.game.Barricade;
import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.player.loadout.Loadout;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by JamesCZ98 on 7/26/2019.
 */
public class WorldInteractionListener implements Listener {

    public static final Material[] glasses = new Material[]{Material.GLASS, Material.GLASS_PANE,
            Material.WHITE_STAINED_GLASS, Material.WHITE_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS, Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS, Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS, Material.PINK_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS, Material.GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS, Material.CYAN_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS, Material.PURPLE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS, Material.BROWN_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS, Material.GREEN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS, Material.RED_STAINED_GLASS_PANE,
            Material.BLACK_STAINED_GLASS, Material.BLACK_STAINED_GLASS_PANE};

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRightClickBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.hasBlock() && event.getAction() == Action.PHYSICAL && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.FARMLAND)
            event.setCancelled(true);


        if (player.getGameMode() == GameMode.CREATIVE) {
            if (block != null && block.getBlockData() instanceof TrapDoor) {
                TrapDoor trapDoor = (TrapDoor) block.getBlockData();

                if (trapDoor.getMaterial() == Material.IRON_TRAPDOOR) {
                    trapDoor.setOpen(!trapDoor.isOpen());
                    block.setBlockData(trapDoor);
                    block.getState().update();
                }
            }

            return;

        }

        if (block != null) {


            if (block.getBlockData() instanceof TrapDoor) {
                event.setCancelled(true);
            }


            if (block.getType() == Material.BLAST_FURNACE || block.getType() == Material.FURNACE ||
                    block.getType() == Material.BARREL || block.getType() == Material.CHEST) {

                if (CastleSiege.getPlugin().getGame(player).isNotPlaying(player))
                    return;
                event.setCancelled(true);
            }

            if (block.getType() == Material.FLETCHING_TABLE) {

                if (CastleSiege.getPlugin().getGame(player).isNotPlaying(player))
                    return;
                event.setCancelled(true);

                PlayerProfile playerProfile = PlayerProfile.getProfile(player);
                Loadout loadout = playerProfile.getEquippedLoadout();

                String[] itemNames = loadout.getItemNames();
                for (int i = 0; i < itemNames.length; i++) {
                    ItemStack item = CastleSiege.getItemCreator().getItem(itemNames[i]);
                    if (item.getType() == Material.ARROW || item.getType() == Material.TIPPED_ARROW || item.getType() == Material.SPECTRAL_ARROW) {
                        player.getInventory().setItem(i, item.clone());
                    }
                }
            }

            if(block.getType() == Material.BREWING_STAND) {
                if (CastleSiege.getPlugin().getGame(player).isNotPlaying(player))
                    return;
                event.setCancelled(true);


                PlayerProfile playerProfile = PlayerProfile.getProfile(player);
                Loadout loadout = playerProfile.getEquippedLoadout();

                String[] itemNames = loadout.getItemNames();
                for (int i = 0; i < itemNames.length; i++) {
                    ItemStack item = CastleSiege.getItemCreator().getItem(itemNames[i]);
                    if (item.getType() == Material.POTION || item.getType() == Material.LINGERING_POTION || item.getType() == Material.SPLASH_POTION) {
                        player.getInventory().setItem(i, item.clone());
                    }
                }

            }

        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SURVIVAL && event.getBlockPlaced().getType() != Material.SCAFFOLDING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemFrameBreak(HangingBreakEvent event){
        if(event.getCause() != HangingBreakEvent.RemoveCause.ENTITY)
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemFrameBreakByEntity(HangingBreakByEntityEvent event) {
        Entity ent = event.getRemover();

        if(ent instanceof Player) {
            Player player = (Player) ent;

            if(player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Block block = event.getBlock();

        if (player.getGameMode() != GameMode.CREATIVE && block.getType() != Material.SCAFFOLDING)
            event.setCancelled(true);

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        Game game = CastleSiege.getPlugin().getGame(player);

        if (game == null)
            return;

        if (Barricade.isBarricadeBlock(game.getMap(), block)) {
            Barricade barricade = Barricade.getBarricade(game.getMap(), block);

            if (barricade == null) return;


            if (barricade.isPlaced())
                barricade.destroy(false);


        } else if (Arrays.asList(glasses).contains(block.getType()))
            WorldUtils.breakGlass(block, 25);
    }



    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }
}