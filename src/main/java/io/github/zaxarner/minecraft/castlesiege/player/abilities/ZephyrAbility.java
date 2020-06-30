package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.player.ability.PassiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public class ZephyrAbility extends PassiveAbility {

    public ZephyrAbility() {
        super("zephyr");

        Bukkit.getScheduler().runTaskTimer(CastleSiege.getPlugin(), () -> {

            for(Game game : CastleSiege.getPlugin().getGames()) {
                if(game == null)
                    continue;
                for (Player p : game.getPlayers()) {
                    PlayerProfile profile = PlayerProfile.getProfile(p);

                    if (profile != null && CastleSiege.getPlugin().getGame(p) != null) {

                        if (game.isNotPlaying(p))
                            continue;



                        if (profile.getEquippedLoadout().hasAbility(this.getName()) && p.getGameMode() != GameMode.CREATIVE) {
                            p.removePotionEffect(PotionEffectType.JUMP);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10,
                                    getStat("amplifier").intValue(), true, false));
                            EffectUtils.displayPotionParticle(p.getLocation(), 1, Color.WHITE);
                            EffectUtils.displayDustParticle(p.getLocation(), 1, Color.WHITE, 1f);
                        }
                    }
                }
            }

        }, 0L, 5L);
    }
}
