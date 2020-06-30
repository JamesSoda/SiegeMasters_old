package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

/**
 * Created by JamesCZ98 on 8/15/2019.
 */
public class ShieldBashAbility extends ActiveAbility implements Listener {

    public ShieldBashAbility() {

        super("shield-bash");

    }

    @Override
    public boolean use(Player player) {

        if(!PlayerUtils.isGrounded(player))
            return false;

        if(player.getInventory().getItemInOffHand().getType() != Material.SHIELD && player.getInventory().getItemInMainHand().getType() != Material.SHIELD) {
            player.sendMessage(ChatColor.RED + "You must be holding a Shield to cast this!");
            return false;
        }

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
        player.setVelocity(vel.multiply(getStat("strength").doubleValue()).setY(.5));
        AbilityUtils.addStatusEffect(player, "shield-bash", 3);
        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
            player.setFallDistance(15f);
        }, 2L);


        return true;
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            Game game = CastleSiege.getPlugin().getGame(player);

            if(game == null)
                return;

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (AbilityUtils.hasStatusEffect(player, "shield-bash")) {
                    double fallDamage = event.getDamage() / 3.0;
                    if(fallDamage < 6)
                        event.setDamage(0.0);
                    else
                        event.setDamage(fallDamage);

                    EffectUtils.playSound(Sound.ITEM_SHIELD_BLOCK, player.getLocation(), 2f, .5f);
                    EffectUtils.playSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, player.getLocation(), 1.5f, .8f);
                    EffectUtils.displayParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1, 1);
                    EffectUtils.displayPotionParticle(player.getLocation(), 5, Color.GRAY);
                    EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 5, .02f);

                    for (Player p : AbilityUtils.getNearbyPlayers(player.getLocation(), getStat("radius").doubleValue())) {
                        if (!game.areTeammates(p, player)) {
                            EffectUtils.displayParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 1, 1);

                            PlayerUtils.damagePlayer(p, getStat("damage").intValue(), player, EntityDamageEvent.DamageCause.THORNS);
                            AbilityUtils.knockback(p, player.getLocation(), .25);
                        }
                    }
                }
            }
        }
    }
}
