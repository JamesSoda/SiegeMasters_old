package io.github.zaxarner.minecraft.castlesiege.player.ability;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by JamesCZ98 on 8/2/2019.
 */
public abstract class PassiveAbility extends Ability {


    public PassiveAbility(String name) {
        super(name);
    }

    @Override
    public ItemStack getItem() {
        return abilityItem.clone();
    }
}
