package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by JamesCZ98 on 8/7/2019.
 */
public class TauntAbility extends ActiveAbility {

    public TauntAbility() {
        super("taunt");
    }

    private List<Vector> particleOffsets = WorldUtils.getHollowSphereOffsets(getStat("radius").intValue(), 5);

    @Override
    public boolean use(Player player) {

        if(!MagickTask.removeMagick(player, getStat("cost").intValue()))
            return false;

        EffectUtils.displayParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(), 10, 1f);
        EffectUtils.displayParticle(Particle.CRIT_MAGIC, player.getLocation(), 100, 1f);

        EffectUtils.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, player.getLocation(), 2f, -1.5f);
        EffectUtils.playSound(Sound.BLOCK_ANVIL_LAND, player.getLocation(), 2f, .25f);

        List<Entity> nearbyEntities = AbilityUtils.getNearbyEntities(player.getLocation(),
                getStat("radius").doubleValue());

        nearbyEntities.addAll(AbilityUtils.getNearbyPlayers(player.getLocation(), getStat("radius").doubleValue()));

        Location particleLocation = player.getLocation().add(0.0, .5, 0.0);
        for(Vector v : particleOffsets) {
            particleLocation.add(v);
            EffectUtils.displayParticle(Particle.CRIT_MAGIC, particleLocation, 1, 0f);
            particleLocation.subtract(v);
        }

        for(Entity ent : nearbyEntities) {
            if(ent instanceof Player) {
                Player p = (Player) ent;
                if(!CastleSiege.getPlugin().getGame(p).areTeammates(p, player)) {
                    EffectUtils.displayPotionParticle(ent.getLocation(), 15, Color.GRAY);
                    AbilityUtils.knockback(ent, player.getLocation(),
                            getStat("strength").doubleValue() * -1);
                    PlayerUtils.damagePlayer(p, 0, player, EntityDamageEvent.DamageCause.MAGIC);
                }

            } else {
                EffectUtils.displayPotionParticle(ent.getLocation(), 15, Color.GRAY);
                AbilityUtils.knockback(ent, player.getLocation(),
                        getStat("strength").doubleValue() * -1);

            }
        }


        return true;
    }

}
