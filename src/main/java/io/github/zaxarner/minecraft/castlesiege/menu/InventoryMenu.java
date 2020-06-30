package io.github.zaxarner.minecraft.castlesiege.menu;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * Created by JamesCZ98 on 8/3/2019.
 */
public abstract class InventoryMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    private String title;
    private int size;

    public InventoryMenu(String title, int size) {
        inventory = Bukkit.createInventory(this, size, title);

        this.title = title;
        this.size = size;

        Bukkit.getServer().getPluginManager().registerEvents(this, CastleSiege.getPlugin());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void openInventory(Player player) {

        Inventory inv = Bukkit.createInventory(this, size, title);

        if(inventory != null) {
            for (int i = 0; i < size; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    inv.setItem(i, item.clone());
                }
            }
        }

        player.openInventory(inv);
    }

    public abstract void onClick(InventoryClickEvent event);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        onClick(event);
    }

    public ItemStack createGuiItem(Material material, String name, int amount, String... description) {
        ItemStack item = new ItemStack(material, amount);
        ItemUtils.setDisplayName(item, name);
        ItemUtils.setLore(item, description);
        ItemUtils.addItemFlag(item, ItemFlag.HIDE_ATTRIBUTES);

        return item;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }
}