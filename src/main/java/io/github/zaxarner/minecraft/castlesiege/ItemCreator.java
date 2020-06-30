package io.github.zaxarner.minecraft.castlesiege;

import com.google.common.collect.Sets;
import io.github.zaxarner.minecraft.castlesiege.utils.CollectionUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 8/11/2019.
 */
public class ItemCreator {

    private DataFile itemsFile;

    public HashMap<String, ItemStack> items = new HashMap<>();
    public HashMap<String, HashMap<String, Object>> itemStats = new HashMap<>();

    public ItemCreator() {
        itemsFile = new DataFile("items.yml", null, true);

        initializeItems();
    }

    private void initializeItems() {
        items.clear();
        FileConfiguration config = itemsFile.getConfig();

        for (String name : config.getKeys(false)) {

            ItemStack item = createItem(name);

            if (item == null)
                continue;


            items.put(name, item);
        }
    }

    public DataFile getItemsFile() {
        return itemsFile;
    }

    public ItemStack getItem(String name) {
        String s = ChatColor.stripColor(name).toLowerCase().replace(" ", "-");
        return items.get(s);
    }

    public String getItemConfigName(String displayName) {
        FileConfiguration config = itemsFile.getConfig();

        for (String name : config.getKeys(false)) {

            String s = config.getString(name + ".display-name");

            if (s == null)
                continue;

            String otherName = ChatColor.translateAlternateColorCodes('&', s);

            if (otherName.equalsIgnoreCase(displayName)) {
                return name;
            }
        }

        return null;
    }

    private ItemStack createItem(String name) {
        ConfigurationSection section = itemsFile.getConfig().getConfigurationSection(name);

        if (section == null)
            return null;

        String materialName = section.getString("material");
        if (materialName == null)
            return null;

        Material mat = Material.getMaterial(materialName);
        if (mat == null)
            return null;

        int amount = 1;
        if (section.get("amount") != null) {
            amount = section.getInt("amount");
        }

        ItemStack item = new ItemStack(mat, amount);

        String displayName = section.getString("display-name");
        if (displayName != null) {
            ItemUtils.setDisplayName(item, ChatColor.translateAlternateColorCodes('&', displayName));
        }

        List<String> lore = new ArrayList<>();
        for (String s : section.getStringList("lore")) {
            String line = ChatColor.translateAlternateColorCodes('&', s);

            lore.add(line);
        }
        lore.add("");
        ItemUtils.setLore(item, lore);

        List<ItemFlag> flags = new ArrayList<>();
        for (String s : section.getStringList("flags")) {
            try {
                flags.add(ItemFlag.valueOf(s));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        ItemUtils.addItemFlag(item, flags.toArray(new ItemFlag[flags.size()]));


        ConfigurationSection miscSection = section.getConfigurationSection("misc");
        if (miscSection != null) {


            for (String s : miscSection.getKeys(false)) {
                String value = miscSection.getString(s);

                if (value == null)
                    continue;

                HashMap<String, Object> stats = itemStats.get(name);
                if (stats == null)
                    stats = new HashMap<>();
                stats.put(s, miscSection.get(s));
                itemStats.put(name, stats);

                if (isType(name, "RANGED") && (s.equalsIgnoreCase("melee-damage") ||
                        s.equalsIgnoreCase("attack-speed")))
                    continue;


                s = StringUtils.makePretty(s);
                value = StringUtils.makePretty(value);

                //String statLine = ChatColor.GRAY + s + ": " + ChatColor.DARK_AQUA + value;

                //ItemUtils.addLore(item, ChatColor.translateAlternateColorCodes('&', statLine));
            }
        }

        /*
        if (getStat(name, "attack-speed") != null && getStat(name, "damage") != null) {
            double attackSpeed = getStat(name, "attack-speed").doubleValue();
            int damage = getStat(name, "damage").intValue();

            ItemUtils.addLore(item, ChatColor.GRAY + "DPS: " + ChatColor.DARK_AQUA +
                    StringUtils.df2.format((double) damage * attackSpeed) + "/second");
        }
        */

        return item;
    }

    public List<ItemStack> getItems(String type) {
        FileConfiguration config = itemsFile.getConfig();

        List<ItemStack> typeItems = new ArrayList<>();

        for (String name : config.getKeys(false)) {

            String s = config.getString(name + ".type");
            if (s == null)
                continue;

            s = s.trim();

            if (s.contains(",")) {

                String[] types = s.split(",");

                for (String t : types) {
                    if (t.trim().equalsIgnoreCase(type))
                        typeItems.add(getItem(name));
                }

            } else {
                if (s.equalsIgnoreCase(type)) {
                    typeItems.add(getItem(name));
                }
            }
        }

        return typeItems;
    }

    public List<ItemStack> getItems(String... type) {

        List<Set<ItemStack>> lists = new ArrayList<>();

        for (String s : type) {
            Set<ItemStack> list = new HashSet<>(getItems(s));
            lists.add(list);

        }



        Set<ItemStack> items = new HashSet<>(lists.get(0));

        for(Set<ItemStack> list : lists) {
            items.retainAll(list);
        }

        return new ArrayList<>(items);
    }

    public boolean isType(String itemName, String type) {
        FileConfiguration config = itemsFile.getConfig();


        String s = config.getString(itemName + ".type");
        if (s == null)
            return false;

        s = s.trim();

        if (s.contains(",")) {

            String[] types = s.split(",");

            for (String t : types) {
                if (t.trim().equalsIgnoreCase(type))
                    return true;
            }

        } else {
            if (s.equalsIgnoreCase(type)) {
                return true;
            }
        }

        return false;
    }

    public List<AttributeModifier> getAttributeModifiers(String itemName) {
        FileConfiguration config = itemsFile.getConfig();

        List<AttributeModifier> modifiers = new ArrayList<>();

        List<String> list = config.getStringList(itemName + ".attribute-modifiers");

        for(String l : list) {
            AttributeModifier mod = AttributeModifier.getAttributeModifierFromString(l);
            if(mod != null) {
                modifiers.add(mod);
            }
        }

        return modifiers;
    }

    public Number getStat(String item, String stat) {
        HashMap<String, Object> statsMap = itemStats.get(item);

        if (statsMap == null)
            return null;

        Object obj = statsMap.get(stat);

        if (obj == null) {
            return null;
        }

        if (obj instanceof Number) {
            return (Number) obj;
        }

        CastleSiege.log("Stat [" + stat + "] is not a number on " + item, Level.SEVERE);

        return null;
    }
}