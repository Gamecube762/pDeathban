package com.github.Gamecube762.pDeathBan.Commands;

import com.github.Gamecube762.pDeathBan.Group;
import com.github.Gamecube762.pDeathBan.Main;
import com.github.Gamecube762.pDeathBan.TimeScale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Gamecube762 on 3/29/2015.
 */
public class DeathBan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("banish")) {
                if (args.length < 1) {
                    sender.sendMessage("/%s ban <Player> [group]");
                    return true;
                }

                Player t = Bukkit.getPlayer(args[1]);

                if (t == null) {
                    sender.sendMessage(args[1] + " is not online!");
                    return true;
                }

                Group group;
                if (args.length > 1) {
                    Group a = Main.getGroup(args[2]);
                    if (a == null) {
                        sender.sendMessage("Cannot find group " + args[2]);
                        return true;
                    }
                    group = a;
                } else
                    group = Main.getPlayersGroup(t);

                Long a = System.currentTimeMillis() + TimeScale.scale(group.getTime(), group.getScale());
                Main.getInstance().getConfig().getConfigurationSection("Players." + t.getUniqueId()).set("BanTime", a);

                t.kickPlayer(String.format("You have been banned for %s %s", group.getTime(), group.getScale()));
                Main.getInstance().saveConfig();

            } else if (args[0].equalsIgnoreCase("unban")) {
                if (args.length < 1) {
                    sender.sendMessage("/%s unban <player>");
                    return true;
                }

                ConfigurationSection cs = Main.getInstance().getConfig().getConfigurationSection("Players");
                for (String s : cs.getKeys(false))
                    if (cs.getString(s + ".name").equalsIgnoreCase(args[1])) {
                        cs.set(s + "BanTime", 0);
                        sender.sendMessage("Unbanned " + args[1]);
                        return true;
                    }

                sender.sendMessage("Could not find " + args[1]);
                Main.getInstance().saveConfig();
            }
        }

        return false;
    }

}
