package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by JamesCZ98 on 11/19/2019.
 */
public class GroundSlamAbility extends ActiveAbility implements Listener {

    public GroundSlamAbility() {
        super("ground-slam");
    }


    @Override
    public boolean use(Player player) {

        if (!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;


        EffectUtils.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, player.getLocation(), .5f, .25f);
        EffectUtils.displayDustParticle(player.getLocation(), 5, Color.GRAY, 3);
        EffectUtils.displayParticle(Particle.SMOKE_LARGE, player.getLocation(), 10, 0f);
        EffectUtils.displayCampfireParticle(player.getLocation(), 1f);

        player.setVelocity(player.getVelocity().setY(getStat("strength").doubleValue()));


        AbilityUtils.addStatusEffect(player, "ground-slam", 5);

        return true;
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) event.getEntity();

            Game game = CastleSiege.getPlugin().getGame(player);

            if (game == null)
                return;

            if (AbilityUtils.hasStatusEffect(player, "ground-slam")) {
                AbilityUtils.removeStatusEffect(player, "ground-slam");
                EffectUtils.displayParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getLocation(), 1, .02f);
                EffectUtils.displayParticle(Particle.LAVA, player.getLocation(), 10, 1);
                EffectUtils.playSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, player.getLocation(), 1.5f, 1f);
                EffectUtils.playSound(Sound.ENTITY_GENERIC_EXPLODE, player.getLocation(), 1f, .5f);

                int finDamage = getStat("damage").intValue();
                double fallDamage = event.getDamage();
                if (fallDamage < 6) {
                    event.setDamage(0.0);
                } else {
                    event.setDamage(fallDamage - 6);
                    finDamage += (int) (fallDamage - 6);
                }

                for (Player p : AbilityUtils.getNearbyPlayers(player.getLocation(), getStat("range").doubleValue())) {
                    if (!game.areTeammates(player, p)) {
                        EffectUtils.displayParticle(Particle.LAVA, p.getLocation(), 5, 1);


                        PlayerUtils.damagePlayer(p, finDamage, player, EntityDamageEvent.DamageCause.THORNS);
                        EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, p.getLocation(), 1, .02f);
                    }
                }
            }
        }
    }
}
