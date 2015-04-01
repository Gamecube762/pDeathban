package com.github.Gamecube762.pDeathBan;

import com.github.Gamecube762.pDeathBan.Commands.DeathBan;
import com.github.Gamecube762.pDeathBan.Commands.Lives;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Gamecube762 on 3/29/2015.
 */
public class Main extends JavaPlugin implements EventListener{

    private static final String defSetLoadFailed = "Failed to load DefaultSetting \"%s\": %s";
    private static final String groupLoadErrorFix = "Problem when loading Group \"%s\": %s can't be %s! Using default %s";
    private static final String groupLoadError = "Problem when loading Group \"%s\": %s";

    private static HashMap<UUID, Integer> lives = new HashMap<UUID, Integer>();
    private static HashMap<String, Group> groups = new HashMap<String, Group>();

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadDefaults();
        loadGroups();

        getCommand("DeathBan").setExecutor(new DeathBan());
        getCommand("Lives").setExecutor(new Lives());

        getServer().getPluginManager().registerEvents(new EventManager(), this);

    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void loadDefaults() {
        int     lives = getConfig().getInt("Settings.DefaultLives"),
                time = getConfig().getInt("Settings.DefaultTime");
        TimeScale scale = TimeScale.fromString(getConfig().getString("Settings.DefaultTimeScale"));

        if (lives == 0) {
            getLogger().severe(String.format(defSetLoadFailed, "Settings.DefaultLives", "Lives can't be 0! Setting to 1"));
            getConfig().set("Settings.DefaultLives", 1);
            lives = 1;
        }
        if (time == 0) {
            getLogger().severe(String.format(defSetLoadFailed, "Settings.DefaultTime", "Time can't be 0! Setting to 1"));
            getConfig().set("Settings.DefaultTime", 1);
            time = 1;
        }
        if (scale == null) {
            getLogger().severe(String.format(defSetLoadFailed, "Settings.DefaultTime", "Time can't be 0! Setting to 1"));
            getConfig().set("Settings.DefaultTimeScale", TimeScale.DAYS);
            scale = TimeScale.DAYS;
        }

        groups.put("_default", new Group(lives, scale, time));
        saveConfig();
    }

    private void loadGroups() {
        ConfigurationSection cs = getConfig().getConfigurationSection("Settings.Groups");
        Group def = getGroup("_default");

        for (String k : cs.getKeys(false)) {
            int     lives = cs.getInt(k + ".Lives"),
                    time = cs.getInt(k + ".Time");
            TimeScale scale = TimeScale.fromString(cs.getString(k + ".TimeScale"));

            if (lives == 0) {
                getLogger().severe(String.format(groupLoadErrorFix, k, "Lives", 0, def.getLives()));
                lives = def.getLives();
            }
            if (scale == null) {
                getLogger().severe(String.format(groupLoadError, k, "TimeScale", "Failed to read TimeScale, Using default " + def.getScale()));
                scale = def.getScale();
            }
            if (time == 0) {
                getLogger().severe(String.format(groupLoadErrorFix, k, "Time", 0, def.getTime()));
                time = def.getTime();
            }

            groups.put(k, new Group(lives, scale, time));
            Bukkit.getPluginManager().addPermission(new Permission("deathban.group." + k, PermissionDefault.FALSE));
        }

        getLogger().info(cs.getKeys(false).size() + " Groups loaded.");
    }


    public static synchronized Group getGroup(String name) {
        return groups.get(name);
    }

    public static synchronized Group getPlayersGroup(Player p) {
        for (String s: groups.keySet())
            if (!s.equals("_default"))
                if (p.hasPermission("deathban.group." + s))
                    return groups.get(s);

        return groups.get("_default");
    }

    public static synchronized int getLives(UUID uuid) {
        return lives.get(uuid);
    }

    public static synchronized void setLives(UUID uuid, int i) {
        lives.put(uuid, i);
    }

    protected static synchronized void removePlayer(Player p) {
        instance.getConfig().set("Players." + p.getUniqueId().toString() + ".Name", p.getName());
        instance.getConfig().set("Players." + p.getUniqueId().toString() + ".Lives", lives.get(p.getUniqueId()));
        instance.saveConfig();
        lives.remove(p.getUniqueId());
    }



    public static synchronized Main getInstance() {
        return instance;
    }
}
