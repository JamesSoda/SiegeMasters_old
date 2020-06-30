package io.github.zaxarner.minecraft.castlesiege.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JamesCZ98 on 7/30/2019.
 */
public class ItemUtils {


    public static boolean addItemFlag(ItemStack item, ItemFlag flag) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return false;

        meta.addItemFlags(flag);

        item.setItemMeta(meta);

        return true;
    }

    public static boolean addItemFlag(ItemStack item, ItemFlag[] flag) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return false;

        meta.addItemFlags(flag);

        item.setItemMeta(meta);

        return true;
    }

    public static boolean removeItemFlag(ItemStack item, ItemFlag flag) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return false;

        meta.removeItemFlags(flag);

        item.setItemMeta(meta);

        return true;
    }


    public static boolean setDisplayName(ItemStack item, String displayName) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return false;

        meta.setDisplayName(displayName);

        item.setItemMeta(meta);

        return true;
    }

    public static String getDisplayName(ItemStack item) {
        return getDisplayName(item, true);
    }

    public static String getDisplayName(ItemStack item, boolean includeColor) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return "";

        if (includeColor) {
            return meta.getDisplayName().trim();
        } else {
            return ChatColor.stripColor(meta.getDisplayName().trim());
        }
    }

    public static List<String> getLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasLore()) return new ArrayList<>();

        return meta.getLore();
    }

    public static void setLore(ItemStack item, String... lore) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        if(meta != null) {
            meta.setLore(Arrays.asList(lore));

            item.setItemMeta(meta);
        }
    }

    public static void setLore(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        if(meta != null) {
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static void addLore(ItemStack item, String line) {
        List<String> lore = getLore(item);
        lore.add(line);
        setLore(item, lore);
    }

    public static ItemStack getLeatherArmor(Material material, Color color) {

        if (material != Material.LEATHER_HELMET && material != Material.LEATHER_CHESTPLATE
                && material != Material.LEATHER_LEGGINGS && material != Material.LEATHER_BOOTS)
            return null;

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;

            armorMeta.setColor(color);
            item.setItemMeta(armorMeta);
        }

        addItemFlag(item, ItemFlag.HIDE_ATTRIBUTES);
        setDisplayName(item, ChatColor.DARK_AQUA + StringUtils.makePretty(material.name()));

        return item;
    }

}