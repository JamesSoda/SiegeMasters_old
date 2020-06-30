package io.github.zaxarner.minecraft.castlesiege.player.loadout;

import io.github.zaxarner.minecraft.castlesiege.Attribute;
import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.ItemCreator;
import io.github.zaxarner.minecraft.castlesiege.AttributeModifier;
import io.github.zaxarner.minecraft.castlesiege.utils.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 11/27/2019.
 */
public class  Loadout {

    private String[] itemNames = new String[9];

    private String weaponName;
    private String[] abilityNames = new String[4];
    private String[] toolNames = new String[4];

    public Loadout() {}

    public Loadout(ConfigurationSection section) {


        itemNames[0] = section.getString("ability-0");
        itemNames[1] = section.getString("ability-1");
        itemNames[2] = section.getString("ability-2");
        itemNames[3] = section.getString("ability-3");

        itemNames[4] = section.getString("weapon");

        itemNames[5] = section.getString("tool-0");
        itemNames[6] = section.getString("tool-1");
        itemNames[7] = section.getString("tool-2");
        itemNames[8] = section.getString("tool-3");

        refreshLoadout();


    }

    public void refreshLoadout() {
        weaponName = itemNames[4];

        for(int i=0; i < 4; i++) {
            abilityNames[i] = itemNames[i];
        }

        for(int i=0; i < 4; i++) {
            toolNames[i] = itemNames[i + 5];
        }
    }

    public boolean setItem(int slot, String itemName) {
        ItemCreator itemCreator = CastleSiege.getItemCreator();

        if(slot < 0 || slot >= 9)
            return false;

        if(slot < 4 && itemCreator.isType(itemName, "ABILITY")) {
            itemNames[slot] = itemName;

        } else if(slot == 4 && itemCreator.isType(itemName, "WEAPON")) {
            itemNames[slot] = itemName;

        } else if(slot >= 5 && itemCreator.isType(itemName, "TOOL")) {
            itemNames[slot] = itemName;

        } else {
            return false;
        }


        refreshLoadout();
        return true;
    }

    public String getWeaponName() {
        return itemNames[4];
    }

    public String[] getAbilityNames() {
        return abilityNames;
    }

    public boolean hasAbility(String abilityName) {
        return CollectionUtils.arrayContains(abilityNames, abilityName);
    }

    public String[] getToolNames() {
        return toolNames;
    }

    public boolean hasTool(String toolName) {
        return CollectionUtils.arrayContains(toolNames, toolName);
    }

    public void setItemNames(String[] items) {
        this.itemNames = items;
    }

    public String[] getItemNames() {
        return itemNames;
    }

    public boolean hasItem(String itemName) {
        return CollectionUtils.arrayContains(itemNames, itemName);
    }

    public boolean hasItemOfType(String type) {
        ItemCreator itemCreator = CastleSiege.getItemCreator();
        for(String s : itemNames) {
            if(itemCreator.isType(s, type))
                return true;
        }
        return false;
    }

    public List<AttributeModifier> getAttributeModifiers() {

        ItemCreator itemCreator = CastleSiege.getItemCreator();

        ArrayList<AttributeModifier> attributeModifiers = new ArrayList<>(itemCreator.getAttributeModifiers(weaponName));

        for(String s : abilityNames) {
            attributeModifiers.addAll(itemCreator.getAttributeModifiers(s));
        }
        for(String s : toolNames) {
            attributeModifiers.addAll(itemCreator.getAttributeModifiers(s));
        }


        return attributeModifiers;
    }

}
