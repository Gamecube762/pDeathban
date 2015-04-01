package com.github.Gamecube762.pDeathBan;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gamecube762 on 3/29/2015.
 */
public class EventManager implements Listener {

    @EventHandler
    public void onPrePlayerJoin(PlayerPreLoginEvent e) {
        ConfigurationSection cs = Main.getInstance().getConfig().getConfigurationSection("Players");
        UUID uuid = e.getUniqueId();

        long banTime = cs.getLong(uuid.toString() + ".BanTime");
        if (banTime == 0) return;

        if (System.currentTimeMillis() > banTime)
            cs.set(uuid.toString() + ".BanTime", 0);
        else
            e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "You cannot return yet!");
        /*{
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(banTime - System.currentTimeMillis()); long dif = 0;

            e.disallow(
                    PlayerPreLoginEvent.Result.KICK_OTHER,
                    String.format(
                            "You have died and cannot return yet!\n %d Days, %d Hours, %d Minutes, %d Seconds",
                            TimeUnit.MILLISECONDS.toDays(dif),
                            TimeUnit.MILLISECONDS.toHours(dif),
                            TimeUnit.MILLISECONDS.toMinutes(dif),
                            TimeUnit.MILLISECONDS.toSeconds(dif)
                            )
            );
        }*/

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ConfigurationSection cs = Main.getInstance().getConfig().getConfigurationSection("Players");
        UUID uuid = e.getPlayer().getUniqueId();

        if (cs.contains(uuid.toString()))
            Main.setLives(uuid, cs.getInt(uuid.toString() + ".Lives"));
        else
            Main.setLives(uuid, Main.getPlayersGroup(e.getPlayer()).getLives());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Main.removePlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        int lives = Main.getLives(e.getEntity().getUniqueId());
        if (lives == -1) return;

        if (lives != 0) {
            lives -= 1;
            Main.setLives(e.getEntity().getUniqueId(), lives);
            return;
        }

        Group group = Main.getPlayersGroup(e.getEntity());

        if (group.getLives() == -1) {
            Main.setLives(e.getEntity().getUniqueId(), -1);
            return;
        }
            
        Main.setLives(e.getEntity().getUniqueId(), group.getLives());

        long time = System.currentTimeMillis() + TimeScale.scale(group.getTime(), group.getScale());
        Main.getInstance().getConfig().getConfigurationSection("Players").set(e.getEntity().getUniqueId().toString() + ".BanTime", time);

        e.getEntity().kickPlayer(String.format("You have died, you may return in %s %s", group.getTime(), group.getScale()));

        Main.getInstance().saveConfig();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        int l = Main.getLives(e.getPlayer().getUniqueId());

        switch (l) {
            case -1:
                e.getPlayer().sendMessage("You have unlimited lives! Go out there and die as much as you want!");
                break;
            case 0:
                e.getPlayer().sendMessage("Last Life!! Don't waste it!");
                break;
            default:
                e.getPlayer().sendMessage("You have " + l + " lives left!");
                break;
        }

    }


}
