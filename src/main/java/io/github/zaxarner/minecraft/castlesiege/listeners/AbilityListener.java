package io.github.zaxarner.minecraft.castlesiege.listeners;

import io.github.zaxarner.minecraft.castlesiege.player.ability.Ability;
import io.github.zaxarner.minecraft.castlesiege.player.ability.ActiveAbility;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JamesCZ98 on 7/30/2019.
 */
public class AbilityListener implements Listener {


    private List<Material> dyes = new ArrayList<>(Arrays.asList(Material.BLACK_DYE, Material.BLUE_DYE, Material.BROWN_DYE, Material.CYAN_DYE, Material.GRAY_DYE, Material.GREEN_DYE, Material.LIGHT_BLUE_DYE, Material.LIGHT_GRAY_DYE,
            Material.LIME_DYE, Material.MAGENTA_DYE, Material.ORANGE_DYE, Material.PINK_DYE, Material.PURPLE_DYE, Material.RED_DYE, Material.WHITE_DYE, Material.YELLOW_DYE));


    @EventHandler
    public void onPlayerAbility(PlayerItemHeldEvent event) {


        Player player = event.getPlayer();

        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());

        if (heldItem == null) return;

        if (player.getGameMode() != GameMode.CREATIVE && dyes.contains(heldItem.getType())) {
            event.setCancelled(true);

            String abilityName = ItemUtils.getDisplayName(heldItem, false);

            Ability baseAbility = Ability.byName(abilityName);

            if (baseAbility == null) return;

            if (baseAbility instanceof ActiveAbility) {
                ActiveAbility ability = (ActiveAbility) baseAbility;

                if(AbilityUtils.hasStatusEffect(player, "silenced")) {
                    player.sendMessage("");
                    player.sendMessage(ChatColor.RED + "You are silenced! ");
                    player.sendMessage("");
                    EffectUtils.playSound(Sound.BLOCK_FIRE_EXTINGUISH, player, 2f, 2f);
                    return;
                }

                if(AbilityUtils.hasStatusEffect(player, "spawn-muted"))
                    return;

                if (ability.use(player)) {
                    ability.cooldown(player, event.getNewSlot());
                }
            }
        }

        if(player.getGameMode() != GameMode.CREATIVE && heldItem.getType() == Material.BOOK) {
            event.setCancelled(true);
        }

        if(player.getGameMode() != GameMode.CREATIVE && heldItem.getType() == Material.INK_SAC) {
            event.setCancelled(true);
        }
    }
}