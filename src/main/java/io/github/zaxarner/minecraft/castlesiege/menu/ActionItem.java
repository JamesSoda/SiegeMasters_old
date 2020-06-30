package io.github.zaxarner.minecraft.castlesiege.menu;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 12/14/2019.
 */
public abstract class ActionItem extends ItemStack implements Listener {


    private final static List<ActionItem> actionItems = new ArrayList<>();

    public final static HashMap<Integer, ActionItem> lobbyItems = new HashMap<>();

    public ActionItem(Material type, String title, String... lore) {
        super(type);
        ItemUtils.setDisplayName(this, title);
        ItemUtils.setLore(this, lore);

        CastleSiege.getPlugin().getServer().getPluginManager().registerEvents(this, CastleSiege.getPlugin());
        actionItems.add(this);
    }


    public static void registerActionItems() {
        ActionItem joinGameItem = new ActionItem(Material.COMPASS, ChatColor.DARK_AQUA + "Join the Game",
                ChatColor.GOLD + "[Click] " +
                ChatColor.GRAY + "to join a Game!") {
            @Override
            public boolean action(Player player) {
                CastleSiege.gameMenu.openInventory(player);
                return true;
            }
        };

        ActionItem loadoutItem = new ActionItem(Material.IRON_SWORD, ChatColor.DARK_AQUA + "View & Edit Loadouts",
                ChatColor.GOLD + "[Click] " + ChatColor.GRAY + "to change and equip different Loadouts!") {
            @Override
            public boolean action(Player player) {
                CastleSiege.loadoutMenu.openInventory(player);
                return true;
            }
        };

        ItemUtils.addItemFlag(loadoutItem, ItemFlag.HIDE_ATTRIBUTES);

        ActionItem tutorialItem = new ActionItem(Material.WRITTEN_BOOK, ChatColor.GOLD + "Tutorial",
                ChatColor.GRAY + "Read to learn about the game!") {
            @Override
            public boolean action(Player player) {
                return false;
            }
        };

        BookMeta bookMeta = (BookMeta) tutorialItem.getItemMeta();
        if(bookMeta != null) {
            bookMeta.setAuthor("Zaxarner");
            bookMeta.setTitle(ChatColor.GOLD + "Tutorial");
            bookMeta.addPage("" + ChatColor.DARK_PURPLE + "SiegeMasters\n" +
                    ChatColor.RESET + "Welcome!\n\n" +
                    ChatColor.DARK_PURPLE + "How do I play?\n" +
                    ChatColor.DARK_PURPLE + "/join" + ChatColor.RESET + " or the Compass!\n\n" +
                    "Read this short tutorial for more information!\n\n" +
                    ChatColor.DARK_PURPLE + "/discord" + ChatColor.RESET + " for more information!\n\n");

            bookMeta.addPage("" + ChatColor.DARK_PURPLE + "SiegeMasters\n" +
                    ChatColor.RESET + "Weapons!\n\n" +
                    "Daggers, Shortswords, Battle Axes, " +
                    "Greatswords, Great Axes, Bows, and Crossbows\n\n" +
                    ChatColor.DARK_PURPLE + "==> " + ChatColor.RESET + "MAKE SURE YOU TIME YOUR ATTACKS!\n\n" +
                    "Spam-Clicking attacks deal reduced damage.");

            bookMeta.addPage("" + ChatColor.DARK_PURPLE + "SiegeMasters\n" +
                    ChatColor.RESET + "Abilities!\n\n" +
                    "Press 1, 2, 3, and 4 to use your abilities!\n\n" +
                    "Open your inventory and hover over abilities to see what they do!");

            bookMeta.addPage("" + ChatColor.DARK_PURPLE + "SiegeMasters\n" +
                    ChatColor.RESET + "Loadouts!\n\n" +
                    "You can change everything about your Loadout or equip a different one!\n\n" +
                    ChatColor.DARK_PURPLE + "How do I change my Loadout?\n" +
                    ChatColor.DARK_PURPLE + "/loadout" + ChatColor.RESET + " or the Sword!\n\n");

            tutorialItem.setItemMeta(bookMeta);
        }

        ItemUtils.addItemFlag(tutorialItem, ItemFlag.HIDE_ATTRIBUTES);



        actionItems.add(joinGameItem);
        actionItems.add(loadoutItem);

        lobbyItems.put(1, joinGameItem);
        lobbyItems.put(3, loadoutItem);
        lobbyItems.put(5, tutorialItem);
    }

    public static ActionItem getActionItem(String name) {
        for(ActionItem a : actionItems) {
            if(ItemUtils.getDisplayName(a).equalsIgnoreCase(name) || ItemUtils.getDisplayName(a, false).equalsIgnoreCase(name))
                return a;
        }

        return null;
    }

    public abstract boolean action(Player player);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            ItemStack item = player.getInventory().getItemInMainHand();
            ActionItem actionItem = ActionItem.getActionItem(ItemUtils.getDisplayName(item));
            if(actionItem != null) {

                if(actionItem.action(player))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if(clickedItem != null) {
            ActionItem actionItem = ActionItem.getActionItem(ItemUtils.getDisplayName(clickedItem));
            if (actionItem != null) {

                actionItem.action(player);
                event.setCancelled(true);
            }
        }
    }
}
