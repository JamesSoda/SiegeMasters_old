package io.github.zaxarner.minecraft.castlesiege.listeners;

import io.github.zaxarner.minecraft.castlesiege.Attribute;
import io.github.zaxarner.minecraft.castlesiege.AttributeModifier;
import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.ItemCreator;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.tasks.RegenerationTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

/**
 * Created by JamesCZ98 on 8/12/2019.
 */
public class PlayerInventoryListener implements Listener {

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        if (newItem != null && newItem.getType() == Material.SHIELD) {

            if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
            return;

        if (event.getInventory() == event.getWhoClicked().getInventory()) {
            event.setCancelled(true);
        }

        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onPlayerThrowPotion(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {

            Player player = (Player) event.getPotion().getShooter();
            PlayerProfile profile = PlayerProfile.getProfile(player);

            ItemStack item = event.getPotion().getItem();

            ItemCreator itemCreator = CastleSiege.getItemCreator();
            String configName = itemCreator.getItemConfigName(ItemUtils.getDisplayName(item));

            if (profile.getEquippedLoadout().hasTool(configName)) {
                switch (configName) {
                    case "volatile-brew":
                        event.getPotion().setVelocity(event.getPotion().getVelocity().multiply(itemCreator.getStat(configName, "speed").doubleValue()));

                        break;
                }
            }
        }
    }


    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ItemStack potionItem = potion.getItem();
        PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
        if (potionMeta == null)
            return;

        ProjectileSource thrower = potion.getShooter();

        if (thrower instanceof Player) {
            Player player = (Player) thrower;

            // VolatileBrew's ThrownPotion is a FIRE_RESISTANCE Potion
            ItemCreator itemCreator = CastleSiege.getItemCreator();
            String configName = itemCreator.getItemConfigName(ItemUtils.getDisplayName(potionItem));

            if (configName.equalsIgnoreCase("volatile-brew")) {


                boolean hit = false;

                Location loc = potion.getLocation();
                potion.teleport(potion.getLocation().add(0, 100, 0));
                potion.remove();

                EffectUtils.displayParticle(Particle.EXPLOSION_HUGE, loc, 1, 1);
                EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 5, .03f);
                EffectUtils.displayParticle(Particle.CRIT, loc, 100, 1.5f);
                EffectUtils.displayDustParticle(loc, 10, Color.GRAY, 5f);

                EffectUtils.playSound(Sound.ENTITY_GENERIC_EXPLODE, loc, 2f, .5f);
                EffectUtils.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, loc, 2f, .5f);

                for (Player p : AbilityUtils.getNearbyPlayers(loc, itemCreator.getStat(configName, "radius").doubleValue())) {
                    if (CastleSiege.getPlugin().getGame(p).areTeammates(p, player))
                        continue;

                    if (p.hasLineOfSight(potion)) {

                        PlayerUtils.damagePlayer(p, itemCreator.getStat(configName, "damage").intValue(),
                                player, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
                        hit = true;
                    }
                }

                potion.remove();

                if (hit)
                    EffectUtils.playSound(Sound.ENTITY_ARROW_HIT_PLAYER, player, 1f, 1f);
            }
        }
    }

    @EventHandler
    public void onPlayerDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.getProfile(player);


        ItemStack item = event.getItem();

        if (item.getType() == Material.POTION) {
            ItemCreator itemCreator = CastleSiege.getItemCreator();

            String configName = itemCreator.getItemConfigName(ItemUtils.getDisplayName(item));

            if (profile.getEquippedLoadout().hasTool(configName)) {
                switch (configName) {
                    case "regeneration-potion":
                        new RegenerationTask(player, itemCreator.getStat(configName, "heal-amount").intValue(),
                                itemCreator.getStat(configName, "delay").intValue(), null);
                        break;
                    case "speed-potion":
                        PlayerUtils.addAttributeModifier(player, new AttributeModifier(Attribute.MOVE_SPEED, itemCreator.getStat(configName, "boost").doubleValue()),
                                itemCreator.getStat(configName, "duration").intValue());
                        EffectUtils.playSound(Sound.ENTITY_VILLAGER_WORK_CLERIC, player.getLocation(), .5f, 1.8f);
                        break;
                }
            }
        }
    }


    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        if (event.getArrow() instanceof Arrow)
            event.setCancelled(true);
        else if (event.getArrow() instanceof Trident) {
            Trident trident = (Trident) event.getArrow();
            Player player = event.getPlayer();
            if (trident.getShooter() instanceof Player) {
                Player shooter = (Player) trident.getShooter();
                if (player != shooter) {
                    event.setCancelled(true);
                    return;
                }

                for(ItemStack item : player.getInventory()) {
                    if(item.getType() == Material.TRIDENT) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }


    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
