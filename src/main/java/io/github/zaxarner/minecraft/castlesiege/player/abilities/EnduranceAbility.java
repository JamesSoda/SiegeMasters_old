package io.github.zaxarner.minecraft.castlesiege.player.abilities;

import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by JamesCZ98 on 8/9/2019.
 */
public class EnduranceAbility extends ActiveAbility {


    public EnduranceAbility() {
        super("endurance");
    }


    @Override
    public boolean use(Player player) {

        if(player.getHealth() == PlayerUtils.getMaxHealth(player))
            return false;

        int magickLevel = MagickTask.getMagick(player);
        if(MagickTask.removeMagick(player, magickLevel)) {

            EffectUtils.playSound(Sound.BLOCK_CHORUS_FLOWER_GROW, player.getLocation(), 3f, 1.5f);
            EffectUtils.playSound(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, player.getLocation(), 3f, 3f);
            EffectUtils.displayPotionParticle(player.getLocation(), 10, Color.LIME);

            PlayerUtils.healPlayer(player, (int) (magickLevel * (getStat("percent").doubleValue() / 100.0)), player);

            return true;
        }
        return false;
    }
}
