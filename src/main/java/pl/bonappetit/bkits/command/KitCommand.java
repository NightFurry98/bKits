package pl.bonappetit.bkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.bonappetit.bkits.BKits;
import pl.bonappetit.bkits.utils.Gui;
import pl.bonappetit.bkits.utils.Utils;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player p = (Player) sender;

        if (args.length == 0) {
            boolean allow_kits = BKits.getPlugin().getConfig().getBoolean("enable_kits");
            if (allow_kits) {
                Gui.openKitMenu(p);
            } else {
                Utils.sendMessage(p, BKits.getPlugin().getConfig().getString("command.message.disable_kits"));
            }
            return false;
        }

        if (args[0].equalsIgnoreCase("manage")) {
            if (p.hasPermission(BKits.getPlugin().getConfig().getString("command.permission.manage"))) {
                Gui.openKitManageMenu(p);
                return false;
            } else {
                Utils.sendMessage(p, BKits.getPlugin().getConfig().getString("command.message.noperm"));
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (p.hasPermission(BKits.getPlugin().getConfig().getString("command.permission.reload"))) {
                BKits.getPlugin().reloadConfig();
                Utils.sendMessage(p, BKits.getPlugin().getConfig().getString("command.message.reload"));
                return false;
            } else {
                Utils.sendMessage(p, BKits.getPlugin().getConfig().getString("command.message.noperm"));
                return false;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        final Player p = (Player) sender;

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (p.hasPermission(BKits.getPlugin().getConfig().getString("command.permission.manage"))) {
                suggestions.add("manage");
            }
            if (p.hasPermission(BKits.getPlugin().getConfig().getString("command.permission.reload"))) {
                suggestions.add("reload");
            }
        }

        return suggestions;
    }
}
