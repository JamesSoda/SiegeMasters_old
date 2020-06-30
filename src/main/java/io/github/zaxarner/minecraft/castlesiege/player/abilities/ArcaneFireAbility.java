package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public class ArcaneFireAbility extends ActiveAbility implements Listener {


    public ArcaneFireAbility() {
        super("arcane-fire");
    }

    @Override
    public boolean use(Player player) {

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        Vector direction = player.getEyeLocation().getDirection().multiply(getStat("speed").doubleValue());

        Fireball fireball = (Fireball) player.getWorld().spawnEntity(player.getEyeLocation().add(direction), EntityType.FIREBALL);
        fireball.setDirection(direction);
        fireball.setVelocity(direction);
        fireball.setIsIncendiary(false);
        fireball.setFireTicks(Integer.MAX_VALUE);
        fireball.setYield(0f);
        fireball.setShooter(player);
        EffectUtils.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, fireball.getLocation(), 1f, 1f);

        int particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(!fireball.isDead()) {
                    EffectUtils.displayParticle(Particle.FLAME, fireball.getLocation(), 5, 0f);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(CastleSiege.getPlugin(), 0L, 1L).getTaskId();


        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
            Bukkit.getScheduler().cancelTask(particleTask);
            fireball.remove();
        }, 600L);

        return true;
    }

    @EventHandler
    public void onFireballHit(ProjectileHitEvent event) {

        if(event.getEntity() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();

            if(fireball.getShooter() instanceof Player) {
                Player player = (Player) fireball.getShooter();

                Location loc = fireball.getLocation();

                EffectUtils.playSound(Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, loc, 1.5f, 1f);
                EffectUtils.playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST, loc, 2f, 1f);

                EffectUtils.displayParticle(Particle.EXPLOSION_LARGE, loc, 3, 1f);
                EffectUtils.displayParticle(Particle.FLAME, loc, 100, .1f);
                EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 5, .02f);

                boolean hit = false;

                for (Player p : AbilityUtils.getNearbyPlayers(loc, getStat("radius").doubleValue())) {
                    if (p != player && p.hasLineOfSight(fireball)) {
                        PlayerUtils.damagePlayer(p, getStat("damage").intValue(),
                                player, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
                        hit = true;
                    }
                }

                fireball.remove();

                if(hit)
                    EffectUtils.playSound(Sound.ENTITY_ARROW_HIT_PLAYER, player, 1f, 1f);
            }
        }

    }
}
