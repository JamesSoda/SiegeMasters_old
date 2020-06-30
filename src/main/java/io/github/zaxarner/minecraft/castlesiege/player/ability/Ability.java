package io.github.zaxarner.minecraft.castlesiege.player.ability;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.ItemCreator;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 7/30/2019.
 */
public abstract class Ability {

    public final static List<Ability> abilities = new ArrayList<>();

    private final String name;

    public ItemStack abilityItem;


    public Ability(String name) {
        this.name = name;

        ItemCreator itemCreator = CastleSiege.getItemCreator();

        abilityItem = itemCreator.getItem(name);
    }

    public abstract ItemStack getItem();

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return ItemUtils.getDisplayName(abilityItem);
    }
    public Number getStat(String stat) {
        return CastleSiege.getItemCreator().getStat(name, stat);
    }

    public static void registerAbility(Ability ability) {
        if(byName(ability.name) == null)
            abilities.add(ability);
        if(ability instanceof Listener) {
            Listener listener = (Listener) ability;
            CastleSiege.getPlugin().getServer().getPluginManager().registerEvents(listener, CastleSiege.getPlugin());
        }
    }

    public static Ability byName(String name) {
        name = name.replace(" ", "-");
        for(Ability a : abilities) {
            if(a.name.equalsIgnoreCase(name) || a.getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', name))) {
                return a;
            }
        }
        return null;
    }
}
