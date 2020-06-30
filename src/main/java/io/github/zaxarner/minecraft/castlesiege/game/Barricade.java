package io.github.zaxarner.minecraft.castlesiege.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.map.Map;
import io.github.zaxarner.minecraft.castlesiege.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 7/26/2019.
 */
public class Barricade implements Listener {

    private Map map;
    private List<Location> blockLocations;

    private Location center;

    private int currentTaskId;

    private int level = 0;

    private ArmorStand armorStand;


    public Barricade(Map map, List<Location> blockLocations) {
        this.map = map;
        this.blockLocations = blockLocations;

        center = WorldUtils.getAveragePosition(blockLocations);

        destroy(true);


        refreshEntity();
        Bukkit.getPluginManager().registerEvents(this, CastleSiege.getPlugin());


        new BukkitRunnable() {

            @Override
            public void run() {

                if (map.getWorld().getPlayers().size() <= 0)
                    return;

                refreshEntity();

                for (Location loc : blockLocations) {
                    EffectUtils.displayDustParticle(loc, 1, Color.GRAY, .3f);
                }

            }
        }.runTaskTimer(CastleSiege.getPlugin(), 0L, 20L);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        List<Entity> ents = AbilityUtils.getNearbyEntities(player.getEyeLocation(), 3);

        for (Entity ent : ents) {
            if (ent == armorStand) {
                ItemStack stick = player.getInventory().getItemInMainHand();
                if (stick.getType() != Material.STICK)
                    return;

                if (isTaskRunning())
                    return;

                if (getLevel() == 4) {
                    player.sendMessage(ChatColor.DARK_AQUA + "That Barricade is fully " + ChatColor.GOLD + "upgraded" + ChatColor.DARK_AQUA + "!");
                    return;
                }

                if (!isPlaced())
                    place(player);
                else {
                    upgrade(player);
                }

                if (stick.getAmount() > 1)
                    stick.setAmount(stick.getAmount() - 1);
                else
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                break;
            }
        }
    }

    private void refreshEntity() {
        if (armorStand != null)
            armorStand.remove();

        armorStand = (ArmorStand) Objects.requireNonNull(center.getWorld()).spawnEntity(center.clone().add(0.0, 255.0, 0.0), EntityType.ARMOR_STAND);

        armorStand.setMarker(true);
        armorStand.setSilent(true);
        armorStand.setVisible(false);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.teleport(center);

        if (isPlaced()) {
            if (level == 4) {
                armorStand.setCustomName(ChatColor.GOLD + "Fully Upgraded!");
            } else {
                armorStand.setCustomName(ChatColor.GOLD + "Use Building Supplies!");
            }
        } else {
            armorStand.setCustomName(ChatColor.GOLD + "Use Building Supplies!");
        }

        PacketContainer destroyEntity = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyEntity.getIntegerArrays().write(0, new int[]{armorStand.getEntityId()});

        PacketContainer spawnEntity = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawnEntity.getIntegers().write(0, armorStand.getEntityId());
        spawnEntity.getIntegers().write(6, 78);
        // Set yaw pitch
        spawnEntity.getIntegers().write(4, 0);
        spawnEntity.getIntegers().write(5, 0);
        // Set location
        spawnEntity.getDoubles().write(0, center.getX());
        spawnEntity.getDoubles().write(1, center.getY());
        spawnEntity.getDoubles().write(2, center.getZ());
        spawnEntity.getUUIDs().write(0, UUID.randomUUID());

        for (Player p : map.getWorld().getPlayers()) {
            if (p.getInventory().getItemInMainHand().getType() == Material.STICK && p.getNearbyEntities(3, 3, 3).contains(armorStand)) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, spawnEntity);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            } else if (p.getInventory().getItemInMainHand().getType() == Material.WOODEN_AXE && level > 0 && p.getNearbyEntities(3, 3, 3).contains(armorStand)) {
                try {
                    armorStand.setCustomName(ChatColor.GOLD + "Use Sledgehammer!");
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, spawnEntity);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, destroyEntity);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        armorStand.setCustomNameVisible(true);

    }

    public boolean isPlaced() {
        for (Location loc : blockLocations) {
            if (loc.getBlock().getType() != Material.AIR)
                return true;
        }

        return false;
    }

    public boolean isTaskRunning() {
        return Bukkit.getScheduler().isCurrentlyRunning(currentTaskId) || Bukkit.getScheduler().isQueued(currentTaskId);
    }

    public int getLevel() {
        return level;
    }

    public void upgrade(Player player) {
        if (!isPlaced()) {
            return;
        }

        if (isTaskRunning())
            return;

        player.sendMessage(ChatColor.DARK_AQUA + "Upgrading Barricade...");

        currentTaskId = new BukkitRunnable() {

            int index = 0;

            @Override
            public void run() {

                Location loc = blockLocations.get(index);
                if (loc.getBlock().getType() == Material.BIRCH_FENCE) {
                    loc.getBlock().breakNaturally();
                    loc.getBlock().setType(Material.OAK_FENCE);
                } else if (loc.getBlock().getType() == Material.OAK_FENCE) {
                    loc.getBlock().breakNaturally();
                    loc.getBlock().setType(Material.SPRUCE_FENCE);
                } else if (loc.getBlock().getType() == Material.SPRUCE_FENCE) {
                    loc.getBlock().breakNaturally();
                    loc.getBlock().setType(Material.DARK_OAK_FENCE);
                } else if (loc.getBlock().getType() == Material.DARK_OAK_FENCE) {
                    player.sendMessage(ChatColor.DARK_AQUA + "That Barricade is fully " + ChatColor.GREEN + "upgraded" + ChatColor.DARK_AQUA + "!");
                    cancel();
                    return;
                }

                if (loc.getWorld() != null)
                    loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.OAK_FENCE);

                index++;

                if (index == blockLocations.size()) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Barricade " + ChatColor.GREEN + "upgraded" + ChatColor.DARK_AQUA + "!");
                    level++;
                    cancel();
                    refreshEntity();
                }
            }
        }.runTaskTimer(CastleSiege.getPlugin(), 0L, 15L).getTaskId();

    }

    public void place(Player player) {
        player.sendMessage(ChatColor.DARK_AQUA + "Placing Barricade...");

        if (isTaskRunning())
            return;

        currentTaskId = new BukkitRunnable() {

            int index = 0;

            @Override
            public void run() {


                Location loc = blockLocations.get(index);
                loc.getBlock().setType(Material.BIRCH_FENCE);

                if (loc.getWorld() != null)
                    loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.OAK_FENCE);

                index++;

                if (index == blockLocations.size()) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Barricade " + ChatColor.GREEN + "completed" + ChatColor.DARK_AQUA + "!");
                    level = 1;
                    cancel();
                    refreshEntity();
                }
            }
        }.runTaskTimer(CastleSiege.getPlugin(), 0L, 15L).getTaskId();

    }

    public void destroy(boolean completely) {

        Bukkit.getScheduler().cancelTask(currentTaskId);

        level--;

        if (level < 0)
            level = 0;

        if (completely)
            level = 0;

        EffectUtils.playSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, center, 1f, 1.4f);
        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
            for (Location loc : blockLocations) {
                EffectUtils.displayBlockBreakParticle(loc, Material.OAK_FENCE);
                if (level == 0)
                    loc.getBlock().breakNaturally();
                else if (level == 1) {
                    loc.getBlock().breakNaturally();
                    loc.getBlock().setType(Material.BIRCH_FENCE);
                } else if (level == 2) {
                    loc.getBlock().breakNaturally();
                    loc.getBlock().setType(Material.OAK_FENCE);
                } else if (level == 3) {
                    loc.getBlock().breakNaturally();
                    loc.getBlock().setType(Material.SPRUCE_FENCE);
                } else if (level == 4) {
                    loc.getBlock().breakNaturally();
                    loc.getBlock().setType(Material.DARK_OAK_FENCE);
                }
            }
        }, 5L);

        refreshEntity();
    }

    public static boolean isBarricadeBlock(Map map, Block block) {
        for (Barricade barricade : map.getBarricades()) {
            for (Location loc : barricade.blockLocations) {
                if (WorldUtils.compare(loc, block.getLocation()))
                    return true;
            }
        }
        return false;
    }

    public static Barricade getBarricade(Map map, Block block) {
        for (Barricade barricade : map.getBarricades()) {
            for (Location loc : barricade.blockLocations) {
                if (WorldUtils.compare(loc, block.getLocation()))
                    return barricade;
            }
        }

        return null;
    }

    /**
     * @param origin    is either of the bottom corners of a barricade
     * @param direction is the direction the barricade builds towards, either NORTH, SOUTH, EAST, or WEST
     * @param width     the width of the Barricade
     * @param height    the height of the Barricade
     * @return A list of locations for a Barricade
     */
    public static List<Location> generateBarricadeLocations(Location origin, BlockFace direction, int width, int height) {


        List<Vector> offsets = WorldUtils.getSquareOffsets(width, height, direction);
        List<Location> locs = new ArrayList<>();
        for (Vector v : offsets) {
            Location loc = origin.clone().add(v);
            if(loc.getBlock().getType() == Material.AIR)
                locs.add(loc);
        }

        return locs;
    }
}
