package io.github.zaxarner.minecraft.castlesiege.listeners;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.ItemCreator;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.player.ability.Ability;
import io.github.zaxarner.minecraft.castlesiege.player.loadout.Loadout;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 8/1/2019.
 */
public class PlayerDamageListener implements Listener {

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        Player damagee = null;
        Player damager = null;


        if (event.isCancelled())
            return;

        if (event.getEntity() instanceof Player) {
            damagee = (Player) event.getEntity();
        }


        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                damager = (Player) arrow.getShooter();
            }
        }

        if (damager != null && damagee != null) {

            damagee.setNoDamageTicks(0);
            if (CastleSiege.getPlugin().getGame(damager).isNotPlaying(damager) || CastleSiege.getPlugin().getGame(damagee).isNotPlaying(damagee)) {
                event.setCancelled(true);
                return;
            }

            PlayerProfile damagerProfile = PlayerProfile.getProfile(damager);
            PlayerProfile damageeProfile = PlayerProfile.getProfile(damagee);

            int damage = (int) event.getDamage();
            String weapon = damagerProfile.getEquippedLoadout().getWeaponName();

            if(damager.getInventory().getItemInMainHand().getType() != CastleSiege.getItemCreator().getItem(weapon).getType()) {
                event.setDamage(0.0);
                return;
            }

            if(damagee.isBlocking()) {
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() ==
                    EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                damage = CastleSiege.getItemCreator().
                        getStat(weapon, "damage").intValue();


                if (PlayerUtils.itemDamage.get(CastleSiege.getItemCreator().getItem(weapon).getType()) != null) {
                    double vanillaDamage = event.getDamage();

                    if(vanillaDamage >= 1.5)
                        vanillaDamage = 1.5;
                    damage *= vanillaDamage;
                }

            } else if (event.getDamager() instanceof Arrow) {

                Arrow arrow = (Arrow) event.getDamager();

                if (arrow.hasMetadata("ricochet")) {
                    damage = Objects.requireNonNull(Ability.byName("Ricochet")).getStat("damage").intValue();
                } else {
                    damage = CastleSiege.getItemCreator().
                            getStat(weapon, "arrow-damage").intValue();
                }

                if (arrow.isCritical() &&
                        !weapon.equalsIgnoreCase("crossbow")) {
                    damage *= 1.5;
                }

                if (arrow.hasMetadata("flaming")) {
                    damagee.setFireTicks(3 * 20);
                }

                event.setDamage(damage);
            }



            PlayerUtils.damagePlayer(damagee, damage, damager, event.getCause());
            event.setDamage(0.0);


        } else {
            event.setCancelled(true);
            if(damager != null && damager.getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(false);
            }
            if(event.getDamager() instanceof Player && event.getEntity() instanceof ItemFrame && ((Player) event.getDamager()).getGameMode() == GameMode.CREATIVE)
                event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerProfile profile = PlayerProfile.getProfile(player);

            if (player.getWorld() == CastleSiege.getSpawn().getWorld()) {
                event.setCancelled(true);
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID && player.getWorld() == CastleSiege.getSpawn().getWorld()) {
                event.setCancelled(true);
                player.teleport(CastleSiege.getSpawn());
                return;
            }

            player.setNoDamageTicks(0);

            if (event.isCancelled())
                return;

            if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
                event.setCancelled(true);
                return;
            }

            // PLAYER DIED
            if (event.getFinalDamage() >= player.getHealth()) {
                event.setDamage(0.0);
                PlayerUtils.playerDeath(player, event.getCause());
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByTrident(EntityDamageByEntityEvent event) {
        Player damagee = null;
        Player damager = null;


        if (event.isCancelled())
            return;

        if (event.getEntity() instanceof Player) {
            damagee = (Player) event.getEntity();
        }


        if (event.getDamager() instanceof Trident) {
            damager = (Player) ((Trident) event.getDamager()).getShooter();

            if(damager != null && damagee != null) {
                AbilityUtils.knockback(damagee, damager.getLocation(), -2);
                event.setDamage(0.0);
                PlayerUtils.damagePlayer(damagee, 0, damager, EntityDamageEvent.DamageCause.PROJECTILE);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        event.getEntity().getInventory().clear();
        PlayerUtils.playerDeath(event.getEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK);
    }
}
