package yetanotherx.bukkitplugin.ModTRS.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import yetanotherx.bukkitplugin.ModTRS.util.ModTRSPermissions;

public class ModTRSCommandSender {

    private ConsoleCommandSender console;
    private Player player;

    public ModTRSCommandSender(CommandSender sender) {
        if (sender instanceof Player) {
            player = (Player) sender;
        } else if (sender instanceof ConsoleCommandSender) {
            console = (ConsoleCommandSender) sender;
        }
    }

    public boolean isValidSender() {
        return console != null || player != null;
    }

    public void sendMessage(String string) {
        if (console != null) {
            console.sendMessage(string);
        } else if (player != null) {
            player.sendMessage(string);
        }
    }

    public boolean hasPerm(String permission, boolean restricted) {
        if (this.console != null) {
            return true;
        }

        return ModTRSPermissions.has(this.player, permission, restricted);

    }

    public boolean hasPerm(String permission) {
        return hasPerm(permission, true);
    }

    public Server getServer() {
        if (console != null) {
            return console.getServer();
        } else if (player != null) {
            return player.getServer();
        }
        return null;
    }

    public String getName() {
        if (console != null) {
            return "console";
        } else if (player != null) {
            return ChatColor.stripColor(player.getName());
        }
        return "";
    }

    public World getWorld() {
        if (console != null) {
            return this.getServer().getWorlds().get(0);
        } else if (player != null) {
            return player.getWorld();
        }
        return null;
    }

    public Location getLocation() {
        if (console != null) {
            return new Location(this.getServer().getWorlds().get(0), 0, 0, 0);
        } else if (player != null) {
            return player.getLocation();
        }
        return null;
    }

    public void teleport(Location location) {
        if (player != null) {
            player.teleport(location);
        }
    }
}
