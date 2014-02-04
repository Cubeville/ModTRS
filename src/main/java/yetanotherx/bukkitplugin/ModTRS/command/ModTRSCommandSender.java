package yetanotherx.bukkitplugin.ModTRS.command;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ModTRSCommandSender {

    private final CommandSender sender;

    public ModTRSCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    public boolean isValidSender() {
        return sender != null;
    }

    public void sendMessage(String string) {
        sender.sendMessage(string);
    }

    public boolean hasPerm(String permission, boolean restricted) {
        if (restricted) {
            return sender.hasPermission(permission);
        }

        return true;
    }

    public boolean hasPerm(String permission) {
        return hasPerm(permission, true);
    }

    public Server getServer() {
        return sender.getServer();
    }

    public String getName() {
        if (sender instanceof ConsoleCommandSender) {
            return "console";
        } else {
            return sender.getName();
        }
    }

    public World getWorld() {
        if (sender instanceof ConsoleCommandSender) {
            return this.getServer().getWorlds().get(0);
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getWorld();
        } else if (sender instanceof Player) {
            return ((Player) sender).getWorld();
        }

        return null;
    }

    public Location getLocation() {
        if (sender instanceof ConsoleCommandSender) {
            return new Location(this.getServer().getWorlds().get(0), 0, 0, 0);
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getLocation();
        } else if (sender instanceof Player) {
            return ((Player) sender).getLocation();
        }

        return null;
    }

    public void teleport(Location location) {
        if (sender instanceof Player) {
            ((Player) sender).teleport(location);
        }
    }
}
