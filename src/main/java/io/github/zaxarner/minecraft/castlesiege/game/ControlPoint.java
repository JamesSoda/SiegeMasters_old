package io.github.zaxarner.minecraft.castlesiege.game;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.AbilityUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.EffectUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.MathUtils;
import io.github.zaxarner.minecraft.castlesiege.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by JamesCZ98 on 12/24/2019.
 */
public class ControlPoint {

    private final ControlGame game;
    private final String name;
    private final Location center;
    private final int radius;
    private final int displayRadius;
    private final int captureProgressDropOff;

    private final BossBar bossBar;

    private Team controllingTeam;
    private Team capturingTeam;

    private int captureProgress = 0;

    private List<Vector> particleOffsets;

    public ControlPoint(ControlGame game, String name, Location center, int radius, int displayRadius, int captureProgressDropOff, Team controllingTeam, Team capturingTeam) {
        this.game = game;
        this.name = name;
        this.center = center;
        this.radius = radius;
        this.displayRadius = displayRadius;
        this.captureProgressDropOff = captureProgressDropOff;

        this.controllingTeam = controllingTeam;
        this.capturingTeam = capturingTeam;

        particleOffsets = WorldUtils.getHollowCircleOffsets(radius + 1, 2);

        bossBar = Bukkit.createBossBar(name, BarColor.WHITE, BarStyle.SOLID);
    }


    public void updateBossBars() {

        BarColor controllerBarColor = getBarColor(game.getTeamChatColor(controllingTeam));
        BarColor capturingBarColor = getBarColor(game.getTeamChatColor(capturingTeam));


        if (capturingTeam != null) {
            bossBar.setTitle(ChatColor.GOLD + "The " + capturingTeam.getDisplayName() + ChatColor.GOLD + " are capturing the " + name);
            bossBar.setColor(capturingBarColor);
        } else {
            bossBar.setColor(controllerBarColor);
            bossBar.setProgress(1.0);
            if (controllingTeam == null) {
                bossBar.setTitle(ChatColor.GOLD + "No one controls the " + name);
            } else {
                bossBar.setTitle(ChatColor.GOLD + "The " + controllingTeam.getDisplayName() + ChatColor.GOLD + " control the " + name);
            }
        }

        List<Player> displayPlayers = AbilityUtils.getNearbyPlayers(center, displayRadius);

        bossBar.removeAll();
        if (displayRadius == -1) {
            displayPlayers = game.getPlayers();
        }

        for (Player p : displayPlayers) {
            bossBar.addPlayer(p);
        }
    }


    public void handleTick() {

        for (Vector v : particleOffsets) {
            Location loc = center.clone().add(v);
            EffectUtils.displayDustParticle(loc, 1, game.getTeamColor(controllingTeam), 4f);
        }


        if (captureProgress == 0)
            capturingTeam = null;

        int attackers = getAttackers();
        int defenders = getDefenders();

        if (controllingTeam == null) {
            if (capturingTeam == null) {
                if (attackers > defenders && defenders == 0) {
                    capturingTeam = game.getAttackerTeam();
                } else if (defenders > attackers && attackers == 0) {
                    capturingTeam = game.getDefenderTeam();
                }
            }
        } else {
            if(controllingTeam == game.getAttackerTeam()) {
                if(defenders > 0 && attackers <= 0)
                    capturingTeam = game.getDefenderTeam();
            } else if(controllingTeam == game.getDefenderTeam()) {
                if(attackers > 0 && defenders <= 0)
                    capturingTeam = game.getAttackerTeam();
            }
        }


        if(capturingTeam != null) {

            int others = 0;
            int capturers = 0;

            if(capturingTeam == game.getAttackerTeam()) {
                others = defenders;
                capturers = attackers;
            } else if(capturingTeam == game.getDefenderTeam()) {
                others = attackers;
                capturers = defenders;
            }

            if (capturers > 0 && others <= 0) {

                captureProgress += captureProgressDropOff;


                float pitch = 0.5f + (captureProgress / 67f);
                EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, getCenter(), 2f, pitch);

            } else if (capturers <= 0) {
                if (captureProgress > 0) {

                    captureProgress -= captureProgressDropOff;

                    if (captureProgress < 0)
                        captureProgress = 0;

                    float pitch = 0.5f + (captureProgress / 67f);
                    EffectUtils.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, getCenter(), 2f, pitch);
                }
            }

            if (captureProgress >= 100) {
                capture();
            }
        }


        bossBar.setProgress(captureProgress / 100.0);
        updateBossBars();


    }

    public void capture() {
        controllingTeam = capturingTeam;
        capturingTeam = null;
        captureProgress = 0;
        EffectUtils.playSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, getCenter(), 1f, 1.5f);
    }


    private int getAttackers() {
        List<Player> capturingPlayers = AbilityUtils.getNearbyPlayers(center, radius);

        int attackers = 0;

        for (Player p : capturingPlayers) {
            if (game.getTeam(p) == game.getAttackerTeam()) {
                attackers++;
            }
        }
        return attackers;
    }

    private int getDefenders() {
        List<Player> capturingPlayers = AbilityUtils.getNearbyPlayers(center, radius);

        int defenders = 0;

        for (Player p : capturingPlayers) {
            if (game.getTeam(p) == game.getDefenderTeam()) {
                defenders++;
            }
        }
        return defenders;
    }

    public String getName() {
        return name;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    private BarColor getBarColor(ChatColor color) {
        switch (color) {
            case YELLOW:
                return BarColor.YELLOW;
            case DARK_PURPLE:
                return BarColor.PURPLE;
        }

        return BarColor.WHITE;
    }

    public Location getCenter() {
        return center;
    }

    public Team getControllingTeam() {
        return controllingTeam;
    }

    public Team getCapturingTeam() {
        return capturingTeam;
    }
}