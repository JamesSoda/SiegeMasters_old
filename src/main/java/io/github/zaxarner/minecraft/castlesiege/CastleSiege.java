package io.github.zaxarner.minecraft.castlesiege;

import co.aikar.commands.PaperCommandManager;
import io.github.zaxarner.minecraft.castlesiege.api.voting.VoteListener;
import io.github.zaxarner.minecraft.castlesiege.commands.*;
import io.github.zaxarner.minecraft.castlesiege.commands.staff.*;
import io.github.zaxarner.minecraft.castlesiege.game.Barricade;
import io.github.zaxarner.minecraft.castlesiege.game.DeathmatchGame;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.listeners.*;
import io.github.zaxarner.minecraft.castlesiege.menu.ActionItem;
import io.github.zaxarner.minecraft.castlesiege.menu.InventoryMenu;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.player.abilities.*;
import io.github.zaxarner.minecraft.castlesiege.player.ability.Ability;
import io.github.zaxarner.minecraft.castlesiege.player.loadout.Loadout;
import io.github.zaxarner.minecraft.castlesiege.tasks.*;
import io.github.zaxarner.minecraft.castlesiege.utils.*;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 7/24/2019.
 */
public class CastleSiege extends JavaPlugin {

    private static DataFile config;
    public static InventoryMenu loadoutMenu;

    public static InventoryMenu abilityMenu;

    public static InventoryMenu damageAbilitiesMenu;
    public static InventoryMenu movementAbilitiesMenu;
    public static InventoryMenu supportAbilitiesMenu;
    public static InventoryMenu utilityAbilitiesMenu;

    public static InventoryMenu toolMenu;
    public static InventoryMenu weaponMenu;
    public static InventoryMenu gameMenu;

    private static ItemCreator itemCreator;

    private List<Game> runningGames = new ArrayList<>();

    private HashMap<Player, Integer> selectedLoadout = new HashMap<>();
    private HashMap<Player, Integer> selectedSlot = new HashMap<>();


    @Override
    public void onEnable() {
        super.onEnable();

        itemCreator = new ItemCreator();


        config = new DataFile("config.yml", null, true);
        config.save();



        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new ArrowListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
        getServer().getPluginManager().registerEvents(new WorldInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new VoteListener(), this);

        initializeInventoryMenus();
        initializeAbilities();
        PlayerUtils.initializeWeaponFields();
        ActionItem.registerActionItems();

        new MagickTask();
        new BroadcastTask();
        new CheckGamesTask();
        new ActionBarTask();


        PluginCommand loadoutCommand = getCommand("loadout");
        if (loadoutCommand != null) {
            loadoutCommand.setExecutor(new LoadoutCommand());
            loadoutCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + loadoutCommand.getUsage());
        }

        PluginCommand joinCommand = getCommand("join");
        if (joinCommand != null) {
            joinCommand.setExecutor(new JoinCommand());
            joinCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + joinCommand.getUsage());
        }

        PluginCommand leaveCommand = getCommand("leave");
        if (leaveCommand != null) {
            leaveCommand.setExecutor(new LeaveCommand());
            leaveCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + leaveCommand.getUsage());
        }

        PluginCommand expCommand = getCommand("exp");
        if (expCommand != null) {
            expCommand.setExecutor(new ExpCommand());
            expCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + expCommand.getUsage());
        }

        PluginCommand voteCommand = getCommand("vote");
        if (voteCommand != null) {
            voteCommand.setExecutor(new VoteCommand());
            voteCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + voteCommand.getUsage());
        }

        PluginCommand endCommand = getCommand("end");
        if (endCommand != null) {
            endCommand.setExecutor(new EndCommand());
            endCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + endCommand.getUsage());
        }

        PluginCommand staffChatCommand = getCommand("staffchat");
        if (staffChatCommand != null) {
            staffChatCommand.setExecutor(new StaffChatCommand());
            staffChatCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + staffChatCommand.getUsage());
        }

        PluginCommand clearChatCommand = getCommand("clearchat");
        if (clearChatCommand != null) {
            clearChatCommand.setExecutor(new ClearChatCommand());
            clearChatCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + clearChatCommand.getUsage());
        }

        PluginCommand kickCommand = getCommand("kick");
        if (kickCommand != null) {
            kickCommand.setExecutor(new KickCommand());
            kickCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + kickCommand.getUsage());
        }

        PluginCommand resetPlayerCommand = getCommand("resetplayer");
        if (resetPlayerCommand != null) {
            resetPlayerCommand.setExecutor(new ResetPlayerCommand());
            resetPlayerCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + resetPlayerCommand.getUsage());
        }

        PluginCommand discordCommand = getCommand("discord");
        if(discordCommand != null) {
            discordCommand.setExecutor(new DiscordCommand());
            discordCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + discordCommand.getUsage());
        }

        PluginCommand lobbyCommand = getCommand("lobby");
        if(lobbyCommand != null) {
            lobbyCommand.setExecutor(new LobbyCommand());
            lobbyCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + lobbyCommand.getUsage());
        }

        PluginCommand creativeCommand = getCommand("creative");
        if(creativeCommand != null) {
            creativeCommand.setExecutor(new CreativeCommand());
            creativeCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + creativeCommand.getUsage());
        }

        PluginCommand pvpCommand = getCommand("pvp");
        if(pvpCommand != null) {
            pvpCommand.setExecutor(new PVPCommand());
            pvpCommand.setUsage(BroadcastTask.PREFIX + ChatColor.GRAY + pvpCommand.getUsage());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerProfile playerProfile = new PlayerProfile(p);
            PlayerProfile.registerProfile(p, playerProfile);
            if(p.getGameMode() != GameMode.CREATIVE && p.getWorld() == getSpawn().getWorld()) {
                PlayerUtils.getLobbyInventory(p);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        for (Player p : StaffChatCommand.staffChatters) {
            p.sendMessage(ChatColor.GOLD + "[StaffChat] Server is " + ChatColor.BOLD + "RELOADING");
            p.sendMessage(ChatColor.GOLD + "[StaffChat] Toggled " + ChatColor.RED + "off");
        }

        for (Game game : runningGames) {
            if (game != null) {

                //TODO: Siege gamemode
                /*
                if(game instanceof )
                for (CapturePoint capturePoint : game.getMap().getCapturePoints()) {
                    capturePoint.getCaptureBar().removeAll();
                    Bukkit.removeBossBar(capturePoint.getCaptureBar().getKey());

                    capturePoint.getEnemyCaptureBar().removeAll();
                    Bukkit.removeBossBar(capturePoint.getEnemyCaptureBar().getKey());

                    capturePoint.getDefendBar().removeAll();
                    Bukkit.removeBossBar(capturePoint.getDefendBar().getKey());

                    capturePoint.getEnemyDefendBar().removeAll();
                    Bukkit.removeBossBar(capturePoint.getEnemyDefendBar().getKey());
                }
                 */

                game.endGame();
            }
        }
    }

    private void initializeAbilities() {
        Ability.registerAbility(new ArcaneFireAbility());
        Ability.registerAbility(new ArcaneShieldAbility());
        Ability.registerAbility(new BastionAbility());
        Ability.registerAbility(new DefianceAbility());
        Ability.registerAbility(new DisengageAbility());
        Ability.registerAbility(new EnduranceAbility());
        Ability.registerAbility(new EnsnareAbility());
        Ability.registerAbility(new GroundSlamAbility());
        Ability.registerAbility(new LeapAbility());
        Ability.registerAbility(new MarkOfUndyingAbility());
        Ability.registerAbility(new RadianceAbility());
        Ability.registerAbility(new RepulseAbility());
        Ability.registerAbility(new RicochetAbility());
        Ability.registerAbility(new ShieldBashAbility());
        Ability.registerAbility(new SilenceAbility());
        Ability.registerAbility(new SingeAbility());
        Ability.registerAbility(new SwapAbility());
        Ability.registerAbility(new TauntAbility());
        Ability.registerAbility(new TombAbility());
        Ability.registerAbility(new WarpAbility());
        Ability.registerAbility(new ZephyrAbility());
    }

    private void initializeInventoryMenus() {

        gameMenu = new InventoryMenu("Current Games", 9) {

            @Override
            public void openInventory(Player player) {

                List<Game> runningGames = CastleSiege.getPlugin().getGames();

                for (int i = 0; i < 9; i++) {
                    gameMenu.getInventory().setItem(i, new ItemStack(Material.AIR));
                }

                for (int i = 0; i < runningGames.size(); i++) {
                    Game game = runningGames.get(i);

                    if (game == null)
                        continue;

                    if (game.getPlayers().size() >= game.getMap().getMaxPlayers()) {
                        ItemStack closed = new ItemStack(Material.RED_WOOL, i + 1);
                        ItemUtils.setDisplayName(closed, ChatColor.RED + "Game #" + (i + 1));
                        ItemUtils.setLore(closed, ChatColor.DARK_AQUA + "Map: " + ChatColor.AQUA + game.getMap().getName(),
                                ChatColor.DARK_AQUA + "Gamemode: " + ChatColor.AQUA + game.getGameTypeName(), "",
                                ChatColor.RED + "Players: " + game.getPlayers().size() + "/" + game.getMap().getMaxPlayers());
                        gameMenu.getInventory().setItem(i, closed);
                    } else {
                        ItemStack open = new ItemStack(Material.GREEN_WOOL, i + 1);
                        ItemUtils.setDisplayName(open, ChatColor.GREEN + "Game #" + (i + 1));
                        ItemUtils.setLore(open, ChatColor.DARK_AQUA + "Map: " + ChatColor.AQUA + game.getMap().getName(),
                                ChatColor.DARK_AQUA + "Gamemode: " + ChatColor.AQUA + game.getGameTypeName(), "",
                                ChatColor.GREEN + "Players: " + game.getPlayers().size() + "/" + game.getMap().getMaxPlayers());
                        gameMenu.getInventory().setItem(i, open);
                    }
                }
                super.openInventory(player);
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.getType() == Material.BARRIER)
                    return;

                Player player = (Player) event.getWhoClicked();

                int slot = event.getRawSlot();

                List<Game> games = CastleSiege.getPlugin().getGames();

                if (games.get(slot) != null) {
                    Game game = games.get(slot);

                    if (game.getPlayers().size() < game.getMap().getMaxPlayers()) {
                        game.joinGame(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "That Game is full!");
                    }
                }
            }
        };

        //region loadoutMenu
        loadoutMenu = new InventoryMenu("Your Loadouts", 18) {
            @Override
            public void openInventory(Player player) {
                PlayerProfile profile = PlayerProfile.getProfile(player);


                for (int i = 0; i < 9; i++) {

                    Loadout loadout = profile.getLoadout(i);
                    if (loadout == null) {
                        ItemStack barrier = new ItemStack(Material.BARRIER);
                        ItemUtils.setDisplayName(barrier, ChatColor.GREEN + "Loadout #" + (i + 1));
                        ItemUtils.setLore(barrier, ChatColor.GRAY + "You have not unlocked this");

                        if (profile.getUnlockedLoadouts() == (i)) {
                            ItemUtils.addLore(barrier, ChatColor.GOLD + "[Right-Click] " + ChatColor.GRAY + "to unlock this Loadout!");
                            ItemUtils.addLore(barrier, ChatColor.GRAY + "Costs " + ChatColor.GOLD + "5 Keys");
                            ItemUtils.addLore(barrier, ChatColor.GRAY + "You have " + ChatColor.GOLD + profile.getKeys() + " Keys");
                        } else {
                            ItemUtils.addLore(barrier, ChatColor.GRAY + "You must unlock all previous Loadouts");
                            ItemUtils.addLore(barrier, ChatColor.GRAY + "before you can unlock this");
                        }

                        loadoutMenu.getInventory().setItem(i, barrier);
                        continue;
                    }

                    ItemStack logo = itemCreator.getItem(loadout.getWeaponName());
                    if (logo != null && logo.getType() != Material.AIR) {
                        logo = logo.clone();

                        ItemUtils.setDisplayName(logo, ChatColor.GREEN + "Loadout #" + (i + 1));
                        ItemUtils.setLore(logo, ChatColor.GOLD + "[Right-Click] " + ChatColor.GRAY + "to view/edit Loadout");

                        if (profile.getCurrentLoadout() == i) {
                            ItemUtils.addLore(logo, ChatColor.GOLD + "This is your current Loadout!");
                            logo.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
                            ItemUtils.addItemFlag(logo, ItemFlag.HIDE_ENCHANTS);
                        } else {
                            ItemUtils.addLore(logo, ChatColor.GOLD + "[Left-Click] " + ChatColor.GRAY + "to equip this Loadout");
                        }


                    } else {
                        logo = new ItemStack(Material.BARRIER);
                        ItemUtils.setDisplayName(logo, ChatColor.GREEN + "Loadout #" + (i + 1));
                        ItemUtils.setLore(logo, ChatColor.GRAY + "You have not unlocked this");
                    }
                    loadoutMenu.getInventory().setItem(i, logo);
                }

                super.openInventory(player);
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getType() == Material.AIR)
                    return;

                Player player = (Player) event.getWhoClicked();
                PlayerProfile profile = PlayerProfile.getProfile(player);

                int slot = event.getRawSlot();
                int loadoutNumber = slot + 1;

                if (clickedItem.getType() == Material.BARRIER) {
                    if (loadoutNumber == profile.getUnlockedLoadouts() + 1) {
                        if (event.isRightClick()) {
                            if (profile.unlockLoadout()) {
                                player.sendMessage(ChatColor.GREEN + "Successfully unlocked " + ChatColor.GOLD + "Loadout #" + (loadoutNumber));
                                loadoutMenu.openInventory(player);
                            } else {
                                player.sendMessage(ChatColor.RED + "Failed to unlock " + ChatColor.GOLD + "Loadout #" + (loadoutNumber));
                            }
                        }
                    }
                    return;
                }


                player.closeInventory();

                if (event.isRightClick()) {

                    InventoryMenu loadout = getLoadoutMenu(player, slot);

                    player.closeInventory();
                    loadout.openInventory(player);
                } else if (event.isLeftClick()) {


                    if (profile.setCurrentLoadout(slot)) {
                        player.sendMessage(ChatColor.GREEN + "Successfully set Loadout to " + ChatColor.GOLD + "Loadout #" + loadoutNumber);
                    } else {
                        player.sendMessage(ChatColor.RED + "Failed to set Loadout to " + ChatColor.GOLD + "Loadout #" + loadoutNumber);
                    }
                }
            }
        };
        //endregion

        //region abilityMenu
        abilityMenu = new InventoryMenu("Abilities", 18) {
            @Override
            public void openInventory(Player player) {

                getInventory().setItem(1, createGuiItem(Material.GRAY_DYE, ChatColor.DARK_AQUA + "Damage Abilities", 1,
                        ChatColor.GRAY + "View the " + ChatColor.DARK_AQUA + "Damage " + ChatColor.GRAY + "Abilities!"));

                getInventory().setItem(3, createGuiItem(Material.GREEN_DYE, ChatColor.DARK_AQUA + "Movement Abilities", 1,
                        ChatColor.GRAY + "View the " + ChatColor.DARK_AQUA + "Movement " + ChatColor.GRAY + "Abilities!"));

                getInventory().setItem(5, createGuiItem(Material.LIGHT_BLUE_DYE, ChatColor.DARK_AQUA + "Support Abilities", 1,
                        ChatColor.GRAY + "View the " + ChatColor.DARK_AQUA + "Support " + ChatColor.GRAY + "Abilities!"));

                getInventory().setItem(7, createGuiItem(Material.LIGHT_GRAY_DYE, ChatColor.DARK_AQUA + "Utility Abilities", 1,
                        ChatColor.GRAY + "View the " + ChatColor.DARK_AQUA + "Utility " + ChatColor.GRAY + "Abilities!"));

                getInventory().setItem(13, createGuiItem(Material.CLAY_BALL,
                        ChatColor.GREEN + "Back", 1,
                        ChatColor.GRAY + "Go back to Loadout selection"));

                super.openInventory(player);
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();

                if (event.getRawSlot() == 13) {
                    event.getWhoClicked().closeInventory();
                    if (selectedLoadout.get(player) != null) {
                        InventoryMenu loadout = getLoadoutMenu(player, selectedLoadout.get(player));

                        player.closeInventory();
                        loadout.openInventory(player);
                    } else {
                        player.closeInventory();
                        loadoutMenu.openInventory(player);
                    }
                }

                ItemStack clicked = event.getCurrentItem();
                if (clicked == null || clicked.getType() == Material.AIR) {
                    return;
                }

                if (event.getRawSlot() == 1) {
                    damageAbilitiesMenu.openInventory(player);
                } else if (event.getRawSlot() == 3) {
                    movementAbilitiesMenu.openInventory(player);
                } else if (event.getRawSlot() == 5) {
                    supportAbilitiesMenu.openInventory(player);
                } else if (event.getRawSlot() == 7) {
                    utilityAbilitiesMenu.openInventory(player);
                }

            }
        };

        //endregion

        damageAbilitiesMenu = new InventoryMenu("Damage Abilities", 54) {
            @Override
            public void openInventory(Player player) {
                openCategoryMenu(this, player, "ABILITY", "DAMAGE");
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                Player player = (Player) event.getWhoClicked();

                event.setCancelled(true);

                handleCategoryMenuClick(this, player, event.getRawSlot(), event.isRightClick());

            }
        };

        movementAbilitiesMenu = new InventoryMenu("Movement Abilities", 54) {
            @Override
            public void openInventory(Player player) {
                openCategoryMenu(this, player, "ABILITY", "MOVEMENT");
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                Player player = (Player) event.getWhoClicked();

                event.setCancelled(true);

                handleCategoryMenuClick(this, player, event.getRawSlot(), event.isRightClick());

            }
        };

        supportAbilitiesMenu = new InventoryMenu("Support Abilities", 54) {
            @Override
            public void openInventory(Player player) {
                openCategoryMenu(this, player, "ABILITY", "SUPPORT");
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                Player player = (Player) event.getWhoClicked();

                event.setCancelled(true);

                handleCategoryMenuClick(this, player, event.getRawSlot(), event.isRightClick());

            }
        };

        utilityAbilitiesMenu = new InventoryMenu("Utility Abilities", 54) {
            @Override
            public void openInventory(Player player) {
                openCategoryMenu(this, player, "ABILITY", "UTILITY");
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                Player player = (Player) event.getWhoClicked();

                event.setCancelled(true);

                handleCategoryMenuClick(this, player, event.getRawSlot(), event.isRightClick());

            }
        };

        toolMenu = new InventoryMenu("Tools", 54) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();

                ItemStack clicked = event.getCurrentItem();
                if (clicked == null || clicked.getType() == Material.AIR) {
                    return;
                }

                manageLoadoutMenuClick(player, clicked);

            }
        };


        List<ItemStack> utilities = itemCreator.getItems("TOOL");
        for (int i = 0; i < utilities.size(); i++) {
            toolMenu.getInventory().setItem(i, utilities.get(i));
        }
        //endregion

        //region weaponMenu

        weaponMenu = new InventoryMenu("Weapons", 9) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();

                ItemStack clicked = event.getCurrentItem();
                if (clicked == null || clicked.getType() == Material.AIR) {
                    return;
                }

                manageLoadoutMenuClick(player, clicked);

            }
        };

        List<ItemStack> weapons = itemCreator.getItems("WEAPON");
        for (int i = 0; i < weapons.size(); i++) {
            weaponMenu.getInventory().setItem(i, weapons.get(i));
        }
        //endregion


    }

    private void openCategoryMenu(InventoryMenu invMenu, Player player, String... type) {


        List<ItemStack> sortedList = new ArrayList<>(itemCreator.getItems(type));
        sortedList.sort(Comparator.comparing(ItemUtils::getDisplayName));

        PlayerProfile profile = PlayerProfile.getProfile(player);
        for (int i = 0; i < sortedList.size(); i++) {

            ItemStack item = sortedList.get(i);
            String displayName = ItemUtils.getDisplayName(item);
            String configName = itemCreator.getItemConfigName(displayName);

            if (profile.hasItemUnlocked(configName)) {
                invMenu.getInventory().setItem(i, sortedList.get(i));
            } else {
                ItemStack barrier = new ItemStack(Material.BARRIER);
                ItemUtils.setDisplayName(barrier, displayName);
                ItemUtils.setLore(barrier, ItemUtils.getLore(item));
                ItemUtils.addLore(barrier, " ");
                ItemUtils.addLore(barrier, ChatColor.GOLD + "[Right-Click] " + ChatColor.GRAY + "to unlock this Ability!");

                int cost = itemCreator.getStat(configName, "keys").intValue();
                if (cost == 1)
                    ItemUtils.addLore(barrier, ChatColor.GRAY + "Costs " + ChatColor.GOLD + cost + " Key");
                else
                    ItemUtils.addLore(barrier, ChatColor.GRAY + "Costs " + ChatColor.GOLD + cost + " Keys");

                ItemUtils.addLore(barrier, ChatColor.GRAY + "You have " + ChatColor.GOLD + profile.getKeys() + " Keys");
                invMenu.getInventory().setItem(i, barrier);
            }
        }

        invMenu.getInventory().setItem(49, invMenu.createGuiItem(Material.CLAY_BALL,
                ChatColor.GREEN + "Back", 1,
                ChatColor.GRAY + "Go back to Ability Type selection"));

        Inventory inv = Bukkit.createInventory(invMenu, invMenu.getSize(), invMenu.getTitle());

        for (int i = 0; i < invMenu.getSize(); i++) {
            ItemStack item = invMenu.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                inv.setItem(i, item.clone());
            }
        }

        player.openInventory(inv);
    }

    private void handleCategoryMenuClick(InventoryMenu invMenu, Player player, int slot, boolean rightClick) {

        if (slot == 49) {
            player.closeInventory();
            abilityMenu.openInventory(player);
            return;
        }


        ItemStack clicked = invMenu.getInventory().getItem(slot);
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            PlayerProfile profile = PlayerProfile.getProfile(player);
            String itemName = itemCreator.getItemConfigName(ItemUtils.getDisplayName(clicked));
            if (rightClick) {
                if (profile.unlockItem(itemName)) {
                    player.sendMessage(ChatColor.GREEN + "Successfully unlocked " + ItemUtils.getDisplayName(clicked));
                    invMenu.openInventory(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to unlock " + ItemUtils.getDisplayName(clicked));

                }
            }
            return;
        }

        manageLoadoutMenuClick(player, clicked);

    }

    private InventoryMenu getLoadoutMenu(Player player, int loadoutNumber) {
        PlayerProfile profile = PlayerProfile.getProfile(player);
        selectedLoadout.put(player, loadoutNumber);
        InventoryMenu loadoutMenu = new InventoryMenu("Loadout #" + (loadoutNumber + 1), 27) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Inventory inv = event.getClickedInventory();
                if (inv == null) return;
                if (inv.getHolder() != this) return;

                event.setCancelled(true);

                ItemStack item = event.getCurrentItem();

                if (item == null) {
                    return;
                }

                int slot = event.getRawSlot();

                selectedSlot.put(player, slot);

                if (event.getRawSlot() >= 0 && event.getRawSlot() < 4) {
                    player.closeInventory();
                    abilityMenu.openInventory(player);
                } else if (event.getRawSlot() > 4 && event.getRawSlot() < 9) {
                    player.closeInventory();
                    toolMenu.openInventory(player);
                } else if (event.getRawSlot() == 4) {
                    player.closeInventory();
                    weaponMenu.openInventory(player);
                } else if (event.getRawSlot() == 22) {
                    event.getWhoClicked().closeInventory();
                    CastleSiege.loadoutMenu.openInventory(player);
                }
            }
        };

        loadoutMenu.getInventory().setItem(22, loadoutMenu.createGuiItem(Material.CLAY_BALL,
                ChatColor.GREEN + "Back", 1,
                ChatColor.GRAY + "Go back to Loadout selection"));

        Loadout loadout = profile.getLoadout(loadoutNumber);

        String[] loadoutItems = loadout.getItemNames();


        for (int i = 0; i < loadoutItems.length; i++) {
            ItemStack item = itemCreator.getItem(loadoutItems[i]);
            if(item != null) {
                loadoutMenu.getInventory().setItem(i, itemCreator.getItem(loadoutItems[i]));
            } else {
                loadoutMenu.getInventory().setItem(i, loadoutMenu.createGuiItem(Material.BARRIER, ChatColor.RED + "Empty Slot",
                        1, ChatColor.GOLD + "[Right-Click] " + ChatColor.GRAY + "to equip something!"));
            }
        }

        return loadoutMenu;
    }

    private void manageLoadoutMenuClick(Player player, ItemStack clicked) {
        PlayerProfile playerProfile = PlayerProfile.getProfile(player);
        String newItem = itemCreator.getItemConfigName(ItemUtils.getDisplayName(clicked));

        if (!selectedSlot.containsKey(player) || selectedSlot.get(player) == null)
            return;

        int loadoutNumber = selectedLoadout.get(player);
        int slot = selectedSlot.get(player);

        Loadout loadout = playerProfile.getLoadout(loadoutNumber);

        if (loadout.hasItem(newItem)) {
            player.sendMessage(ChatColor.RED + "That Loadout already contains that item! " +
                    ChatColor.GRAY + "[" + ItemUtils.getDisplayName(clicked) + ChatColor.GRAY + "]");
            return;
        }

        String currentItem = loadout.getItemNames()[slot];

        if (itemCreator.isType(currentItem, "SHIELD") && !itemCreator.isType(newItem, "SHIELD")) {
            if (loadout.hasAbility("shield-bash")) {
                player.sendMessage(ChatColor.RED + "You must unequip " + ChatColor.GRAY + "[" +
                        ItemUtils.getDisplayName(CastleSiege.getItemCreator().getItem("shield-bash"))
                        + ChatColor.GRAY + "] " + ChatColor.RED + "before you can remove this!");
                return;
            }
        } else if (!itemCreator.isType(newItem, "ARROW") && itemCreator.isType(currentItem, "ARROW")
                && itemCreator.isType(loadout.getWeaponName(), "RANGED")) {
            player.sendMessage(ChatColor.RED + "You must unequip " + ChatColor.GRAY + "[" + ItemUtils.getDisplayName(itemCreator.getItem(loadout.getWeaponName()))
                    + ChatColor.GRAY + "] " + ChatColor.RED + "before you can remove this!");
            return;
        } else if (itemCreator.isType(newItem, "RANGED")) {
            if (!loadout.hasItemOfType("ARROW")) {
                player.sendMessage(ChatColor.RED + "You must first equip a type of Arrow before you can equip this!");
                return;
            }
        } else if (newItem.equals("shield-bash")) {
            if (!loadout.hasItemOfType("SHIELD")) {
                player.sendMessage(ChatColor.RED + "You must have a Shield to equip this!");
                return;
            }
        } else if(itemCreator.isType(newItem, "ARROW") && loadout.hasItemOfType("ARROW") &&
                !itemCreator.isType(currentItem, "ARROW")) {
            player.sendMessage(ChatColor.RED + "Your Loadout already contains a type of Arrow! Replace the type you are currently using.");
            return;
        }


        if (loadout.setItem(slot, newItem)) {
            player.sendMessage(ChatColor.GREEN + "Successfully updated " + ChatColor.DARK_GREEN + "Loadout #" + (loadoutNumber + 1));
            playerProfile.saveLoadouts();
        } else {
            player.sendMessage(ChatColor.RED + "Failed to update your Loadout");
        }

        selectedSlot.put(player, null);
        player.closeInventory();

        if (selectedLoadout.get(player) != null) {
            InventoryMenu selectedLoadoutMenu = getLoadoutMenu(player, selectedLoadout.get(player));

            player.closeInventory();
            selectedLoadoutMenu.openInventory(player);
        } else {
            player.closeInventory();
            loadoutMenu.openInventory(player);
        }
    }

    public void removeGame(Game game) {
        runningGames.remove(game);
    }

    public void newGame(Game game) {
        if (!runningGames.contains(game)) {
            runningGames.add(game);
            List<HumanEntity> humans = new ArrayList<>(gameMenu.getInventory().getViewers());
            for (HumanEntity viewer : new ArrayList<>(humans)) {
                viewer.closeInventory();
                gameMenu.openInventory((Player) viewer);
            }
        }
    }

    public Game getGame(Player player) {
        for (Game g : runningGames) {
            if (g.getPlayers().contains(player))
                return g;
        }

        return null;
    }


    public List<Game> getGames() {
        return runningGames;
    }

    public static ItemCreator getItemCreator() {
        return itemCreator;
    }

    public static DataFile getDataFile() {
        return config;
    }

    public static CastleSiege getPlugin() {
        return (CastleSiege) Bukkit.getServer().getPluginManager().getPlugin("SiegeMasters");
    }

    public static void log(String message, Level level) {
        Bukkit.getServer().getLogger().log(level, "[CastleSiege] " + message);
    }

    public static Location getSpawn() {
        return new Location(Bukkit.getWorld("spawn"), -0.5, 65.0, 0.5, 0, -10);
    }

    public static Location getCreativeSpawn() {
        return new Location(Bukkit.getWorld("creative"), -30.5, 53.1, -22.5, -135, 0);
    }

}
