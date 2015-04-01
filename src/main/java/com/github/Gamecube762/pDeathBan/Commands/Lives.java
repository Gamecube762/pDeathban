package com.github.Gamecube762.pDeathBan.Commands;

import com.github.Gamecube762.pDeathBan.Main;
import com.github.Gamecube762.pDeathBan.TimeScale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Gamecube762 on 3/29/2015.
 */
public class Lives implements CommandExecutor {

    private HashMap<CommandSender, Long> hold = new HashMap<CommandSender, Long>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        cleanHold();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("send")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Cannot send lives as Console! try /%s Set <Player> <amount>");
                    return true;
                }

                if (!sender.hasPermission("deathban.command.lives.send")) {
                    sender.sendMessage("You don't have permission to send lives!");
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage("/%s send <Player> <amount>");
                    return true;
                }
                int i, a, l = Main.getLives(((Player)sender).getUniqueId());

                if (l == -1) {
                    sender.sendMessage("You cannot send lives as you have unlimited.");
                    return true;
                }

                Player t = Bukkit.getPlayer(args[1]);

                if (t == null) {
                    sender.sendMessage(args[1] + "Is not online!");
                    return true;
                }

                try {
                    i = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(args[2] + " is not a number!");
                    return true;
                }

                a = l - i;

                if (a < 1) {
                    if (hold.containsKey(sender))
                        if (hold.get(sender) >= System.currentTimeMillis()) {
                            hold.remove(sender);
                            Main.setLives(((Player) sender).getUniqueId(), 1);
                            Main.setLives(t.getUniqueId(), Main.getLives(t.getUniqueId()) + l);

                            t.sendMessage(sender.getName() + "Has sent his last" + l + " lives to you!");
                            ((Player) sender).setHealth(0);

                        } else {
                            sender.sendMessage("Warning: sending all your lives will Kill and Banish you! \n Resend the command if you want to continue.");
                            hold.put(sender, TimeScale.add(System.currentTimeMillis(), TimeScale.MINUTES));//hold for a minute
                        }
                    return true;
                }

                Main.setLives(((Player)sender).getUniqueId(), Main.getLives(((Player)sender).getUniqueId()) - i);
                Main.setLives(t.getUniqueId(), Main.getLives(t.getUniqueId()) + i);

                t.sendMessage(sender.getName() + "Has sent" + l + " lives to you!");

            } else if (args[0].equalsIgnoreCase("set")) {
                if (!sender.hasPermission("deathban.command.lives.set")) {
                    sender.sendMessage("You don't have permission to set lives!");
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage("/%s set <Player> <Amount>");
                    return true;
                }

                Player t = Bukkit.getPlayer(args[1]);

                if (t == null) {
                    sender.sendMessage(args[1] + "is not online!");
                    return true;
                }

                int i;
                try {
                    i = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(args[2] + " is not a number!");
                    return true;
                }

                Main.setLives(t.getUniqueId(), i);
                sender.sendMessage("Setting " + t.getName() + "'s lives to " + i);
            }
        } else {
            if (sender instanceof Player)
                sender.sendMessage(Main.getLives(((Player)sender).getUniqueId()) + " lives left.");
        }

        return true;
    }

    private void cleanHold() {
        long a = System.currentTimeMillis();
        Iterator<CommandSender> iter = hold.keySet().iterator();
        while (iter.hasNext())
            if (hold.get(iter.next()) <= a)
                iter.remove();
    }
}
