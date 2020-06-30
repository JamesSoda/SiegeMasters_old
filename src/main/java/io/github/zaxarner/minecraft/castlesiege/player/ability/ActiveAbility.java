package io.github.zaxarner.minecraft.castlesiege.player.ability;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by JamesCZ98 on 7/30/2019.
 */
public abstract class ActiveAbility extends Ability {

    public ActiveAbility(String name) {
        super(name);
    }

    public abstract boolean use(Player player);

    @Override
    public ItemStack getItem() {
        return abilityItem.clone();
    }

    public void cooldown(Player player, int slot) {
        PlayerProfile playerProfile = PlayerProfile.getProfile(player);

        int cooldown = getStat("cooldown").intValue();

        ItemStack cooldownItem = CastleSiege.getItemCreator().getItem("cooldown-item").clone();

        ItemMeta cooldownItemMeta = cooldownItem.getItemMeta();
        if(cooldownItemMeta == null)
            return;


        cooldownItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        cooldownItemMeta.setDisplayName(getDisplayName() + ChatColor.GRAY + " is on cooldown");
        cooldownItem.setItemMeta(cooldownItemMeta);

        player.getInventory().setItem(slot, cooldownItem);

        int task = new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {


                if(!PlayerUtils.hasCancelTask(player, this.getTaskId())) {
                    this.cancel();
                    return;
                }

                if(counter < cooldown) {
                        cooldownItem.setAmount(cooldown - counter);
                        player.getInventory().setItem(slot, cooldownItem);

                } else {

                    player.getInventory().setItem(slot, getItem());
                    this.cancel();

                    return;
                }

                counter++;
            }
        }.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 20L).getTaskId();

        PlayerUtils.addCancelTask(player, task);
    }


}
