package io.github.zaxarner.minecraft.castlesiege.player;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.ItemCreator;
import io.github.zaxarner.minecraft.castlesiege.player.ability.Ability;
import io.github.zaxarner.minecraft.castlesiege.player.loadout.Loadout;
import io.github.zaxarner.minecraft.castlesiege.tasks.BroadcastTask;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.ItemUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JamesCZ98 on 7/30/2019.
 */
public class PlayerProfile {

    public static HashMap<Player, PlayerProfile> profiles = new HashMap<>();

    private final Player player;
    private final DataFile dataFile;


    private Loadout[] loadouts = new Loadout[9];

    private Loadout equippedLoadout;

    public PlayerProfile(Player player) {
        this.player = player;

        this.dataFile = new DataFile(player.getUniqueId().toString() + ".yml", "users", false);

        dataFile.getConfig().set("name", player.getName());
        dataFile.setIfNull("exp", 0);
        dataFile.setIfNull("level", 0);
        dataFile.setIfNull("exp-boost-duration", 0);
        dataFile.setIfNull("keys", 0);

        dataFile.setIfNull("loadouts.unlocked-loadouts", 2);
        dataFile.setIfNull("loadouts.current-loadout", 0);

        dataFile.setIfNull("loadouts.unlocked-items", new ArrayList<String>());

        // region: Populate Default Loadouts
        // Default Loadout0
        dataFile.setIfNull("loadouts.loadout-0.ability-0", "arcane-fire");
        dataFile.setIfNull("loadouts.loadout-0.ability-1", "shield-bash");
        dataFile.setIfNull("loadouts.loadout-0.ability-2", "warp");
        dataFile.setIfNull("loadouts.loadout-0.ability-3", "endurance");
        dataFile.setIfNull("loadouts.loadout-0.weapon", "shortsword");
        dataFile.setIfNull("loadouts.loadout-0.tool-0", "hatchet");
        dataFile.setIfNull("loadouts.loadout-0.tool-1", "building-supplies");
        dataFile.setIfNull("loadouts.loadout-0.tool-2", "siege-ladder");
        dataFile.setIfNull("loadouts.loadout-0.tool-3", "shield");

        // Default Loadout1
        dataFile.setIfNull("loadouts.loadout-1.ability-0", "ricochet");
        dataFile.setIfNull("loadouts.loadout-1.ability-1", "disengage");
        dataFile.setIfNull("loadouts.loadout-1.ability-2", "arcane-fire");
        dataFile.setIfNull("loadouts.loadout-1.ability-3", "zephyr");
        dataFile.setIfNull("loadouts.loadout-1.weapon", "bow");
        dataFile.setIfNull("loadouts.loadout-1.tool-0", "hatchet");
        dataFile.setIfNull("loadouts.loadout-1.tool-1", "building-supplies");
        dataFile.setIfNull("loadouts.loadout-1.tool-2", "siege-ladder");
        dataFile.setIfNull("loadouts.loadout-1.tool-3", "arrows");
        //endregion

        dataFile.save();

        refreshLoadouts();
    }


    public static int getRequiredExpForLevel(int level) {
        return (int) Math.floor(80 * (Math.pow(2, level / 7)));
    }

    public static void registerProfile(Player player, PlayerProfile profile) {
        profiles.put(player, profile);
    }

    public static void unregisterProfile(Player player) {
        PlayerProfile playerProfile = getProfile(player);
        playerProfile.saveLoadouts();
        profiles.remove(player);
    }

    public static PlayerProfile getProfile(Player player) {
        return PlayerProfile.profiles.get(player);
    }

    public Player getPlayer() {
        return player;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public int getLevel() {
        return dataFile.getConfig().getInt("level");
    }

    public void setLevel(int level) {
        dataFile.getConfig().set("level", level);
        dataFile.save();
    }

    public void levelUp() {
        setExp(0);
        setLevel(getLevel() + 1);

        giveKeys(1);

        if (getLevel() % 5 == 0) {
            BroadcastTask.broadcast(ChatColor.GOLD + player.getName() + ChatColor.GRAY + " has leveled up to " + ChatColor.GOLD + getLevel() + "!");
        } else {
            EffectUtils.playSound(Sound.ENTITY_PLAYER_LEVELUP, player, 1f, 1.25f);
            EffectUtils.playSound(Sound.BLOCK_ANVIL_LAND, player, 1f, .5f);
            player.sendMessage(ChatColor.GOLD + "You " + ChatColor.GRAY + "have leveled up to " + ChatColor.GOLD + getLevel() + "!");
        }
    }

    public int getExp() {
        return dataFile.getConfig().getInt("exp");
    }

    public void setExp(int exp) {
        dataFile.getConfig().set("exp", exp);
        dataFile.save();
    }

    public void giveExp(int amount, boolean applyBonus) {

        if (applyBonus && getExpBoostDuration() > 0) {
            amount = (int) Math.ceil(amount * 1.5);
        }

        if (getExp() + amount > getRequiredExpForLevel(getLevel())) {
            int remainingExp = (getExp() + amount) - getRequiredExpForLevel(getLevel());

            if (PlayerUtils.recentExpGained.containsKey(player)) {
                int recentExp = PlayerUtils.recentExpGained.get(player);
                PlayerUtils.recentExpGained.put(player, recentExp + (amount - remainingExp));
            } else {
                PlayerUtils.recentExpGained.put(player, (amount - remainingExp));
            }

            levelUp();
            giveExp(remainingExp, false);


        } else {
            setExp(getExp() + amount);

            if (PlayerUtils.recentExpGained.containsKey(player)) {
                int recentExp = PlayerUtils.recentExpGained.get(player);
                PlayerUtils.recentExpGained.put(player, recentExp + amount);
            } else {
                PlayerUtils.recentExpGained.put(player, amount);
            }
        }
    }

    public int getKeys() {
        return dataFile.getConfig().getInt("keys");
    }

    public void giveKeys(int amount) {
        dataFile.getConfig().set("keys", getKeys() + amount);
        dataFile.save();
    }

    public boolean removeKeys(int amount) {
        if (getKeys() >= amount) {
            dataFile.getConfig().set("keys", getKeys() - amount);
            dataFile.save();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasItemUnlocked(String item) {
        ItemCreator itemCreator = CastleSiege.getItemCreator();

        if (itemCreator.getStat(item, "keys") == null || itemCreator.getStat(item, "keys").intValue() <= 0) {
            return true;
        } else {
            List<String> unlocked = dataFile.getConfig().getStringList("loadouts.unlocked-items");
            return unlocked.contains(item);
        }
    }

    public boolean unlockLoadout() {

        if (getUnlockedLoadouts() >= 9)
            return false;

        if (removeKeys(5)) {
            Loadout newLoadout = new Loadout();
            newLoadout.setItemNames(loadouts[0].getItemNames());
            newLoadout.refreshLoadout();

            loadouts[getUnlockedLoadouts()] = newLoadout;

            saveLoadouts();
            refreshLoadouts();

            dataFile.getConfig().set("loadouts.unlocked-loadouts", getUnlockedLoadouts() + 1);
            dataFile.save();

            return true;
        }
        return false;
    }

    public boolean unlockItem(String item) {
        ItemCreator itemCreator = CastleSiege.getItemCreator();

        if (hasItemUnlocked(item))
            return false;

        int requiredKeys = itemCreator.getStat(item, "keys").intValue();

        if (removeKeys(requiredKeys)) {
            List<String> items = dataFile.getConfig().getStringList("loadouts.unlocked-items");
            items.add(item);
            dataFile.getConfig().set("loadouts.unlocked-items", items);
            dataFile.save();
            return true;
        }

        return false;
    }

    public int getExpBoostDuration() {
        return dataFile.getConfig().getInt("exp-boost-duration");
    }

    public void addExpBoostDuration(int seconds) {
        dataFile.getConfig().set("exp-boost-duration", getExpBoostDuration() + seconds);
        dataFile.save();
    }

    public void removeExpBoostDuration(int seconds) {
        int current = getExpBoostDuration();

        if (current - seconds < 0)
            dataFile.getConfig().set("exp-boost-duration", 0);
        else
            dataFile.getConfig().set("exp-boost-duration", current - seconds);

        dataFile.save();
    }

    public Loadout getLoadout(int number) {
        if (number >= 0 && number < 9) {
            return loadouts[number];
        }
        return null;
    }

    public void saveLoadouts() {
        for (int i = 0; i < 9; i++) {
            Loadout loadout = loadouts[i];
            if (loadout == null) {
                continue;
            }

            for (int a = 0; a < loadout.getAbilityNames().length; a++) {
                dataFile.getConfig().set("loadouts.loadout-" + i + ".ability-" + a, loadout.getAbilityNames()[a]);
            }

            dataFile.getConfig().set("loadouts.loadout-" + i + ".weapon", loadout.getWeaponName());

            for (int t = 0; t < loadout.getToolNames().length; t++) {
                dataFile.getConfig().set("loadouts.loadout-" + i + ".tool-" + t, loadout.getToolNames()[t]);
            }
        }
        dataFile.save();
    }

    public void refreshLoadouts() {
        for (int i = 0; i < 9; i++) {
            ConfigurationSection section = dataFile.getConfig().getConfigurationSection("loadouts.loadout-" + i);
            if (section != null) {
                loadouts[i] = new Loadout(section);
            } else {
                loadouts[i] = null;
            }
        }

        equippedLoadout = getLoadout(getCurrentLoadout());
    }

    public Loadout getEquippedLoadout() {
        return equippedLoadout;
    }

    public void setEquippedLoadout(Loadout loadout) {

        equippedLoadout = new Loadout();

        equippedLoadout.setItemNames(loadout.getItemNames());
        equippedLoadout.refreshLoadout();

    }

    public int getCurrentLoadout() {
        return dataFile.getConfig().getInt("loadouts.current-loadout");
    }

    public boolean setCurrentLoadout(int loadoutNumber) {
        if (loadoutNumber >= getUnlockedLoadouts()) {
            return false;
        }

        dataFile.getConfig().set("loadouts.current-loadout", loadoutNumber);
        dataFile.save();
        return true;
    }

    public int getUnlockedLoadouts() {
        return dataFile.getConfig().getInt("loadouts.unlocked-loadouts");
    }

}