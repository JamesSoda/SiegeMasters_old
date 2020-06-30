package io.github.zaxarner.minecraft.castlesiege.utils;

import io.github.zaxarner.minecraft.castlesiege.Attribute;
import io.github.zaxarner.minecraft.castlesiege.AttributeModifier;
import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.ItemCreator;
import io.github.zaxarner.minecraft.castlesiege.game.DeathmatchGame;
import io.github.zaxarner.minecraft.castlesiege.game.Game;
import io.github.zaxarner.minecraft.castlesiege.menu.ActionItem;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.player.ability.Ability;
import io.github.zaxarner.minecraft.castlesiege.player.loadout.Loadout;
import io.github.zaxarner.minecraft.castlesiege.tasks.MagickTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by JamesCZ98 on 8/5/2019.
 */
public class PlayerUtils {
    public final static HashMap<Material, Double> itemDamage = new HashMap<>();
    public final static HashMap<Material, Double> itemSpeed = new HashMap<>();

    private static HashMap<Player, Player> latestDamager = new HashMap<>();
    private static HashMap<Player, Long> latestDamageTime = new HashMap<>();

    private static HashMap<Player, List<Player>> recentDamagers = new HashMap<>();
    private static HashMap<Player, List<Player>> recentHealers = new HashMap<>();

    public static HashMap<Player, Integer> killsMap = new HashMap<>();
    public static HashMap<Player, Integer> deathsMap = new HashMap<>();
    public static HashMap<Player, Integer> assistsMap = new HashMap<>();
    public static HashMap<Player, Integer> killStreakMap = new HashMap<>();

    private static HashMap<Player, List<Integer>> cancelTasks = new HashMap<>();

    public static HashMap<Player, Integer> recentExpGained = new HashMap<>();

    public static HashMap<Player, List<AttributeModifier>> currentModifiers = new HashMap<>();

    public static void initializeWeaponFields() {

        itemSpeed.put(Material.WOODEN_SWORD, 1.6);
        itemSpeed.put(Material.STONE_SWORD, 1.6);
        itemSpeed.put(Material.IRON_SWORD, 1.6);
        itemSpeed.put(Material.GOLDEN_SWORD, 1.6);
        itemSpeed.put(Material.DIAMOND_SWORD, 1.6);

        itemSpeed.put(Material.WOODEN_AXE, .8);
        itemSpeed.put(Material.STONE_AXE, .8);
        itemSpeed.put(Material.IRON_AXE, .9);
        itemSpeed.put(Material.GOLDEN_AXE, 1.0);
        itemSpeed.put(Material.DIAMOND_AXE, 1.0);

        itemSpeed.put(Material.WOODEN_HOE, 1.0);
        itemSpeed.put(Material.STONE_HOE, 2.0);
        itemSpeed.put(Material.IRON_HOE, 3.0);
        itemSpeed.put(Material.GOLDEN_HOE, 1.0);
        itemSpeed.put(Material.DIAMOND_HOE, 4.0);

        itemDamage.put(Material.WOODEN_SWORD, 4.0);
        itemDamage.put(Material.STONE_SWORD, 5.0);
        itemDamage.put(Material.IRON_SWORD, 6.0);
        itemDamage.put(Material.GOLDEN_SWORD, 4.0);
        itemDamage.put(Material.DIAMOND_SWORD, 7.0);

        itemDamage.put(Material.WOODEN_AXE, 7.0);
        itemDamage.put(Material.STONE_AXE, 9.0);
        itemDamage.put(Material.IRON_AXE, 9.0);
        itemDamage.put(Material.GOLDEN_AXE, 7.0);
        itemDamage.put(Material.DIAMOND_AXE, 9.0);

        itemDamage.put(Material.WOODEN_HOE, 1.0);
        itemDamage.put(Material.STONE_HOE, 1.0);
        itemDamage.put(Material.IRON_HOE, 1.0);
        itemDamage.put(Material.GOLDEN_HOE, 1.0);
        itemDamage.put(Material.DIAMOND_HOE, 1.0);

        itemDamage.put(Material.WOODEN_PICKAXE, 2.0);
        itemDamage.put(Material.STONE_PICKAXE, 3.0);
        itemDamage.put(Material.IRON_PICKAXE, 4.0);
        itemDamage.put(Material.GOLDEN_PICKAXE, 2.0);
        itemDamage.put(Material.DIAMOND_PICKAXE, 5.0);

        itemDamage.put(Material.WOODEN_SHOVEL, 2.5);
        itemDamage.put(Material.STONE_SHOVEL, 3.5);
        itemDamage.put(Material.IRON_SHOVEL, 4.5);
        itemDamage.put(Material.GOLDEN_SHOVEL, 2.5);
        itemDamage.put(Material.DIAMOND_SHOVEL, 5.5);

        itemDamage.put(Material.TRIDENT, 9.0);
    }

    public static boolean equipLoadout(Player player) {
        cancelTasks(player);

        Game game = CastleSiege.getPlugin().getGame(player);

        if (game == null || game.isNotPlaying(player))
            return false;

        PlayerProfile playerProfile = PlayerProfile.getProfile(player);
        Loadout loadout = playerProfile.getLoadout(playerProfile.getCurrentLoadout());
        playerProfile.setEquippedLoadout(loadout);

        player.getInventory().clear();
        AbilityUtils.addStatusEffect(player, "spawn-muted", 3);

        currentModifiers.put(player, new ArrayList<>());

        MagickTask.resetMagick(player);
        player.setFoodLevel(0);

        ItemCreator itemCreator = CastleSiege.getItemCreator();

        String[] itemNames = loadout.getItemNames();
        String weaponName = loadout.getWeaponName();

        if (loadout.hasItemOfType("SHIELD") && itemCreator.isType(weaponName, "ONE-HANDED")) {
            player.getInventory().setItemInOffHand(itemCreator.getItem("shield"));
        }

        for (int i = 0; i < 9; i++) {
            ItemStack item = itemCreator.getItem(itemNames[i]);
            if (item != null && itemCreator.isType(ItemUtils.getDisplayName(item), "PASSIVE")) {
                item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 0);
                ItemUtils.addItemFlag(item, ItemFlag.HIDE_ENCHANTS);
            }

            player.getInventory().setItem(i, item);
        }

        Color color = game.getTeamColor(player);
        player.getInventory().setHelmet(ItemUtils.getLeatherArmor(Material.LEATHER_HELMET, color));
        player.getInventory().setChestplate(ItemUtils.getLeatherArmor(Material.LEATHER_CHESTPLATE, color));
        player.getInventory().setLeggings(ItemUtils.getLeatherArmor(Material.LEATHER_LEGGINGS, color));
        player.getInventory().setBoots(ItemUtils.getLeatherArmor(Material.LEATHER_BOOTS, color));
        player.updateInventory();

        AttributeInstance armor = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ARMOR);
        if (armor != null)
            armor.setBaseValue(-7);

        updateAttackSpeed(player);
        updateMoveSpeed(player);
        updateMaxHealth(player);

        player.getInventory().setHeldItemSlot(4);

        return true;
    }

    public static void updateMaxHealth(Player player) {

        PlayerProfile playerProfile = PlayerProfile.getProfile(player);

        if (playerProfile == null) {
            AttributeInstance maxHealthAttribute = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);


            if (maxHealthAttribute != null) {
                maxHealthAttribute.setBaseValue(20.0);
            }
            return;
        }
        Loadout loadout = playerProfile.getEquippedLoadout();

        List<AttributeModifier> modifiers = loadout.getAttributeModifiers();
        if (currentModifiers.get(player) != null)
            modifiers.addAll(currentModifiers.get(player));

        double maxHealth = 20.0;

        for (AttributeModifier mod : modifiers) {
            if (mod.getAttribute() == Attribute.MAX_HEALTH) {
                maxHealth += mod.getValue();
            }
        }

        AttributeInstance maxHealthAttribute = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);


        if (maxHealthAttribute != null) {
            maxHealthAttribute.setBaseValue(maxHealth);
        }

        player.setHealth(getMaxHealth(player));
    }

    public static double getMaxHealth(Player player) {

        AttributeInstance maxHealthAttribute = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);

        if (maxHealthAttribute != null) {
            return maxHealthAttribute.getBaseValue();
        }

        return 20.0;
    }

    public static void updateMoveSpeed(Player player) {
        PlayerProfile playerProfile = PlayerProfile.getProfile(player);

        if (CastleSiege.getPlugin().getGame(player) == null) {
            player.setWalkSpeed(.2f);
            return;
        }

        Loadout loadout = playerProfile.getEquippedLoadout();


        List<AttributeModifier> modifiers = loadout.getAttributeModifiers();
        modifiers.addAll(currentModifiers.get(player));

        double moveSpeed = .2f;

        for (AttributeModifier mod : modifiers) {
            if (mod.getAttribute() == Attribute.MOVE_SPEED) {
                moveSpeed += mod.getValue();
            }
        }

        if (moveSpeed <= 0)
            moveSpeed = .01;

        player.setWalkSpeed((float) moveSpeed);

    }

    public static void updateAttackSpeed(Player player) {
        AttributeInstance playerAttackSpeedAttribute = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED);
        if (playerAttackSpeedAttribute != null) {
            playerAttackSpeedAttribute.setBaseValue(4.0);
        }

        ItemStack weapon = player.getInventory().getItem(4);


        ItemCreator itemCreator = CastleSiege.getItemCreator();
        if (weapon != null && weapon.getType() != Material.AIR && itemCreator.isType(itemCreator.getItemConfigName(ItemUtils.getDisplayName(weapon)), "WEAPON")) {
            ItemMeta weaponMeta = weapon.getItemMeta();

            if (weaponMeta != null) {

                PlayerProfile playerProfile = PlayerProfile.getProfile(player);

                Loadout loadout = playerProfile.getEquippedLoadout();
                List<AttributeModifier> modifiers = loadout.getAttributeModifiers();
                if (currentModifiers.get(player) != null)
                    modifiers.addAll(currentModifiers.get(player));


                double attackSpeedModifier = 0.0;

                for (AttributeModifier mod : modifiers) {
                    if (mod.getAttribute() == Attribute.ATTACK_SPEED) {
                        attackSpeedModifier += mod.getValue();
                    }
                }

                weaponMeta.removeAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED);
                weaponMeta.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED,
                        new org.bukkit.attribute.AttributeModifier(ItemUtils.getDisplayName(weapon),
                                attackSpeedModifier, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));

                weapon.setItemMeta(weaponMeta);
            }
        }
    }

    public static void updateLatestAttacker(Player damager, Player damagee) {
        latestDamager.put(damagee, damager);
        latestDamageTime.put(damagee, System.currentTimeMillis());

        List<Player> recents = new ArrayList<>();
        if (recentDamagers.get(damagee) != null)
            recents = recentDamagers.get(damagee);

        recents.add(damager);
        recentDamagers.put(damagee, recents);
        Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {

            List<Player> recents1 = new ArrayList<>();
            if (recentDamagers.get(damagee) != null)
                recents1 = recentDamagers.get(damagee);

            recents1.remove(damager);
            recentDamagers.put(damagee, recents1);
        }, 10 * 20L);
    }

    public static Player getRecentAttacker(Player player) {
        Player damager = latestDamager.get(player);
        Long time = latestDamageTime.get(player);

        if (damager == null || time == null) {
            return null;
        }

        if ((System.currentTimeMillis() - time) > 10 * 1000L) {
            return null;
        }

        return damager;
    }

    public static void addCancelTask(Player player, int taskId) {
        List<Integer> tasks = cancelTasks.get(player);
        if (tasks == null)
            tasks = new ArrayList<>();

        tasks.add(taskId);
        cancelTasks.put(player, tasks);
    }

    public static boolean hasCancelTask(Player player, int taskId) {
        List<Integer> tasks = cancelTasks.get(player);
        if (tasks != null) {
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                int task = (Integer) iterator.next();
                if (task == taskId) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void cancelTask(Player player, int taskId) {
        List<Integer> tasks = cancelTasks.get(player);
        if (tasks != null) {
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                int task = (Integer) iterator.next();
                if (task == taskId) {
                    Bukkit.getScheduler().cancelTask(task);
                    iterator.remove();
                }
            }
        }
    }

    public static void cancelTasks(Player player) {
        List<Integer> tasks = cancelTasks.get(player);
        if (tasks != null) {
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                int task = (Integer) iterator.next();
                Bukkit.getScheduler().cancelTask(task);
                iterator.remove();
            }
        }
    }

    private static List<Vector> particleOffsets = WorldUtils.getHollowSphereOffsets(1, 30);

    public static void healPlayer(Player player, int amount, Player healer) {
        int finAmount = amount;
        if (player.getHealth() + amount > getMaxHealth(player)) {
            finAmount = (int) getMaxHealth(player) - (int) player.getHealth();
            player.setHealth(getMaxHealth(player));
        } else
            player.setHealth(player.getHealth() + amount);

        if (finAmount != 0) {
            EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_COW_BELL, player, 0.5f, 2f);
            EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_BELL, player, 0.5f, 2f);

            for (int i = 0; i < finAmount; i++) {

                EffectUtils.displayParticle(Particle.HEART, player.getEyeLocation().add(particleOffsets.get(MathUtils.ranNumber(0, particleOffsets.size() - 1))), 1, 0f);
            }
        }


        if (healer != null) {


            if (healer != player) {


                PlayerProfile healerProfile = PlayerProfile.getProfile(healer);

                healerProfile.giveExp(amount, true);


                List<Player> recents = new ArrayList<>();
                if (recentHealers.get(player) != null)
                    recents = recentHealers.get(player);

                recents.add(healer);
                recentHealers.put(player, recents);
                Bukkit.getScheduler().runTaskLaterAsynchronously(CastleSiege.getPlugin(), () -> {

                    List<Player> recents1 = new ArrayList<>();
                    if (recentHealers.get(player) != null)
                        recents1 = recentHealers.get(player);

                    recents1.remove(healer);
                    recentHealers.put(player, recents1);
                }, 10 * 20L);
            }
        }
    }

    public static void damagePlayer(Player player, int amount, Player damager, EntityDamageByEntityEvent.DamageCause cause) {
        if (CastleSiege.getPlugin().getGame(player).areTeammates(player, damager))
            return;

        EffectUtils.playSound(Sound.ENTITY_GENERIC_HURT, player, 1f, 1.1f);

        updateLatestAttacker(damager, player);


        PlayerProfile damagerProfile = PlayerProfile.getProfile(damager);

        damagerProfile.giveExp(amount, true);

        Ability defiance = Ability.byName("Defiance");
        if (defiance != null && AbilityUtils.hasStatusEffect(player, "Defiance")
                && !AbilityUtils.hasStatusEffect(damager, "Defiance")) {
            PlayerUtils.damagePlayer(damager, (int) (amount * (defiance.getStat("percent").doubleValue() / 100.0)),
                    player, EntityDamageEvent.DamageCause.THORNS);

            amount -= (amount * (defiance.getStat("percent").doubleValue() / 100.0));
        }

        Ability bastion = Ability.byName("Bastion");
        if(bastion != null) {
            for (Player p : AbilityUtils.getNearbyPlayers(player.getLocation(), bastion.getStat("range").doubleValue())) {
                if(p != player && PlayerUtils.isSameTeam(p, player) && AbilityUtils.hasStatusEffect(p, "bastion") && !AbilityUtils.hasStatusEffect(player, "bastion")) {
                    damagePlayer(p, amount, damager, cause);

                    Location loc1 = player.getLocation().add(0.0, 1.0, 0.0);
                    Location loc2 = p.getLocation().add(0.0, 1.0, 0.0);
                    Vector mainVector = loc2.toVector().subtract(loc1.toVector());
                    final Vector[] vector = {mainVector.clone().normalize().multiply(0.1)};
                    Vector cloneable = vector[0].clone();

                    new BukkitRunnable() {

                        int count = 0;
                        @Override
                        public void run() {
                            Location loc = loc1.clone().add(vector[0].clone());
                            EffectUtils.displayDustParticle(loc, 1, Color.AQUA, 2f);

                            vector[0] = vector[0].add(cloneable.clone());
                            if(mainVector.length() < vector[0].length()) {
                                this.cancel();
                            }
                            count++;
                        }

                    }.runTaskTimerAsynchronously(CastleSiege.getPlugin(), 0L, 2L);

                    return;
                }
            }
        }

        if (player.getAbsorptionAmount() > 0) {
            double absorption = player.getAbsorptionAmount();
            if (amount - absorption <= 0) {
                player.setAbsorptionAmount(absorption - amount);
                return;
            } else {
                player.setAbsorptionAmount(0.0);
                amount -= absorption;
            }
        }

        if (amount >= player.getHealth()) {

            if(AbilityUtils.hasStatusEffect(player, "mark-of-undying")) {
                AbilityUtils.removeStatusEffect(player, "mark-of-undying");
                EffectUtils.playSound(Sound.ITEM_TOTEM_USE, player.getLocation(), 1f, 2f);
                EffectUtils.displayDustParticle(player.getLocation().add(0.0, 0.25, 0.0), 1, Color.GREEN, 5f);
                return;
            }

            playerDeath(player, cause);
        } else {
            player.setHealth(player.getHealth() - amount);
        }
    }

    public static void playerDeath(Player player, EntityDamageEvent.DamageCause cause) {
        Player killer = PlayerUtils.getRecentAttacker(player);
        cancelTasks(player);

        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.updateInventory();

        Game game = CastleSiege.getPlugin().getGame(player);

        if (game == null) {
            player.teleport(CastleSiege.getSpawn());
            return;
        }

        if (killer != null) {

            int kills = 0;
            if (killsMap.get(killer) != null)
                kills = killsMap.get(killer);

            killsMap.put(killer, kills + 1);

            int killStreak = 0;
            if (killStreakMap.get(killer) != null)
                killStreak = killStreakMap.get(killer);

            killStreak++;

            killStreakMap.put(killer, killStreak);

            List<Player> healers = new ArrayList<>();
            if (recentHealers.get(killer) != null)
                healers = recentHealers.get(killer);

            List<Player> damagers = new ArrayList<>();
            if (recentDamagers.get(player) != null)
                damagers = recentDamagers.get(player);


            for (Player h : healers) {
                if (h != killer) {
                    int assists = 0;
                    if (assistsMap.get(h) != null)
                        assists = assistsMap.get(h);

                    assistsMap.put(h, assists + 1);
                }
            }

            for (Player h : damagers) {
                if (h != killer) {
                    int assists = 0;
                    if (assistsMap.get(h) != null)
                        assists = assistsMap.get(h);

                    assistsMap.put(h, assists + 1);
                }
            }


            if (game instanceof DeathmatchGame) {
                if (game.getAttackers().contains(killer))
                    game.addAttackerScore();
                else if (game.getDefenders().contains(killer))
                    game.addDefenderScore();
            }


            switch (cause) {
                case FALL:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was thrown to their death by " + game.getTeamChatColor(killer) + killer.getName(), false);
                    break;
                case ENTITY_EXPLOSION:
                case BLOCK_EXPLOSION:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was blown up by " + game.getTeamChatColor(killer) + killer.getName(), false);
                    break;
                case FIRE:
                case FIRE_TICK:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was set ablaze by " + game.getTeamChatColor(killer) + killer.getName(), false);
                    break;
                case MAGIC:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was magicked by " + game.getTeamChatColor(killer) + killer.getName(), false);
                    break;
                case PROJECTILE:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was shot by " + game.getTeamChatColor(killer) + killer.getName(), false);
                    break;
                default:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was slain by " + game.getTeamChatColor(killer) + killer.getName(), false);
            }

            if (killStreak == 3) {
                game.broadcast(game.getTeamChatColor(killer) + killer.getName() + ChatColor.GRAY + " is on a " + ChatColor.DARK_AQUA + killStreak +
                        ChatColor.GRAY + " Killstreak!", false);
            } else if (killStreak > 3 && (killStreak % 5) == 0) {
                game.broadcast(game.getTeamChatColor(killer) + killer.getName() + ChatColor.GRAY + " is on a " + ChatColor.DARK_AQUA + killStreak +
                        ChatColor.GRAY + " Killstreak!", false);
            }

            updateActionBar(killer);


        } else {
            switch (cause) {
                case FALL:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " fell to their death.", false);
                    break;
                case ENTITY_EXPLOSION:
                case BLOCK_EXPLOSION:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was blown up.", false);
                    break;
                case FIRE:
                case FIRE_TICK:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was set ablaze.", false);
                    break;
                case MAGIC:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " was magicked to death", false);
                    break;
                default:
                    game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY +
                            " died.", false);
            }
        }


        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));

        EffectUtils.playSound(Sound.ENTITY_EVOKER_DEATH, player.getLocation(), 1f, .8f);

        player.setHealth(getMaxHealth(player));
        player.setFireTicks(0);
        player.setVelocity(new Vector(0.0, 1.25, 0.0));

        if (killStreakMap.get(player) != null && killStreakMap.get(player) >= 3) {
            game.broadcast(game.getTeamChatColor(player) + player.getName() + ChatColor.GRAY + "'s Killstreak of " + ChatColor.DARK_AQUA + killStreakMap.get(player) +
                    ChatColor.GRAY + " has ended!", false);
        }

        killStreakMap.put(player, 0);

        int deaths = 0;
        if (deathsMap.get(player) != null)
            deaths = deathsMap.get(player);

        deathsMap.put(player, deaths + 1);

        updateActionBar(player);

        if(game.getRespawnTime() >= 0) {
            addCancelTask(player, Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
                game.teleportToSpawn(player);
                player.setGameMode(GameMode.SURVIVAL);

                Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {

                    PlayerUtils.equipLoadout(player);
                }, 5L);
            }, game.getRespawnTime() * 20L).getTaskId());
        }
    }

    public static void addAttributeModifier(Player player, final AttributeModifier modifier, int duration) {

        currentModifiers.get(player).add(modifier);

        updateMoveSpeed(player);
        updateAttackSpeed(player);

        Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
            currentModifiers.get(player).removeIf(o -> o == modifier);

            Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
                updateMoveSpeed(player);
                updateAttackSpeed(player);
            }, 5L);

        }, 20L * duration);
    }

    public static void addPotionEffect(Player player, PotionEffectType type, int duration, int amplifier, boolean hideParticles) {
        player.addPotionEffect(new PotionEffect(type, duration * 20, amplifier, hideParticles, hideParticles, true), true);
    }

    public static void updateActionBar(Player player) {

        if (CastleSiege.getPlugin().getGame(player) != null) {

            int kills = 0;
            if (killsMap.get(player) != null)
                kills = killsMap.get(player);

            int deaths = 0;
            if (deathsMap.get(player) != null)
                deaths = deathsMap.get(player);

            int assists = 0;
            if (assistsMap.get(player) != null)
                assists = assistsMap.get(player);

            PlayerProfile profile = PlayerProfile.getProfile(player);


            String kdaMessage = ChatColor.AQUA + "K/D/A: " + ChatColor.DARK_AQUA + kills + ChatColor.AQUA + "/"
                    + ChatColor.DARK_AQUA + deaths + ChatColor.AQUA + "/"
                    + ChatColor.DARK_AQUA + assists;

            String expMessage = ChatColor.GOLD + "Exp: +" + recentExpGained.get(player);

            String expBoostMessage = ChatColor.AQUA + "Exp Boost: " + ChatColor.DARK_AQUA + StringUtils.formatTime(profile.getExpBoostDuration());

            if (recentExpGained.get(player) > 0 && profile.getExpBoostDuration() > 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format("%-30s%-12s%s", kdaMessage, expMessage, expBoostMessage)));
            } else if (recentExpGained.get(player) > 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format("%-30s%-12s%s", kdaMessage, expMessage, "")));
            } else if (profile.getExpBoostDuration() > 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format("%-30s%-12s%s", kdaMessage, "", expBoostMessage)));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format("%-30s%-12s%s", kdaMessage, "", "")));
            }

            /*
            if(recentExpGained.containsKey(player) && recentExpGained.get(player) > 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        ChatColor.AQUA + "K/D/A: " + ChatColor.DARK_AQUA + kills + ChatColor.AQUA + "/"
                        + ChatColor.DARK_AQUA + deaths + ChatColor.AQUA + "/"
                        + ChatColor.DARK_AQUA + assists +
                        "      " + ChatColor.GOLD + "Exp: +" + recentExpGained.get(player)));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.AQUA + "K/D/A: " + ChatColor.DARK_AQUA + kills + ChatColor.AQUA + "/"
                        + ChatColor.DARK_AQUA + deaths + ChatColor.AQUA + "/"
                        + ChatColor.DARK_AQUA + assists +
                        "             "));
            }
            */


        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        }
    }

    public static void getLobbyInventory(Player player) {
        player.getInventory().clear();

        player.setGameMode(GameMode.SURVIVAL);
        ActionItem.lobbyItems.forEach((key, value) -> {
            player.getInventory().setItem(key, value);
        });

        player.getInventory().setHeldItemSlot(5);
        player.setFoodLevel(20);


    }

    public static boolean isSameTeam(Player player, Player player1) {

        if (player == player1)
            return true;

        return CastleSiege.getPlugin().getGame(player).areTeammates(player, player1);
    }

    public static boolean isGrounded(Player player) {
        return player.isOnGround() || player.getLocation().subtract(0.0, 1.1, 0.0).getBlock().getType() != Material.AIR;
    }
}