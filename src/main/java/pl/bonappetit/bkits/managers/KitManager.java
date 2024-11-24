package pl.bonappetit.bkits.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.configuration.file.*;
import java.util.*;

import pl.bonappetit.bkits.BKits;
import pl.bonappetit.bkits.basic.Kit;
import pl.bonappetit.bkits.basic.User;
import pl.bonappetit.bkits.utils.ParseItemStack;
import pl.bonappetit.bkits.utils.Utils;

public class KitManager
{
    private static List<Kit> kits;
    private static List<Integer> slots;

    public static void load() {
        final FileConfiguration config = BKits.getPlugin().getConfig();
        for (final String id : config.getConfigurationSection("kits").getKeys(false)) {
            final String path = "kits." + id + ".";
            final String name = config.getString(path + "name");
            final ItemStack item = ParseItemStack.parseItem(config.getString(path + "item"));
            final String permission = config.getString(path + "permission");
            final String time = config.getString(path + "time");
            final int slot = config.getInt(path + "slot");
            final boolean enable = config.getBoolean(path + "enable");
            final List<ItemStack> items = new ArrayList<>();
            for (final String s : config.getStringList(path + "items")) {
                items.add(ParseItemStack.parseItem(s));
            }
            getSlots().add(slot);
            getKits().add(new Kit(id, name, item, permission, time, items, slot, enable));
        }
    }

    public static void reload() {
        getKits().clear();
        BKits.getPlugin().saveConfig();
        BKits.getPlugin().reloadConfig();
        load();
        for (final Player players : Bukkit.getOnlinePlayers()) {
            User user = UserManager.getUser(players.getUniqueId());
            if (user != null) {
                user.update();
            }
        }
    }

    public static Kit getKitBySlot(final int slot) {
        for (final Kit kit : getKits()) {
            if (kit.getSlot() == slot) {
                return kit;
            }
        }
        return null;
    }

    public static int generateRandomNumber() {
        final Random random = new Random();
        int randomNumber;
        do {
            randomNumber = random.nextInt(45);
        } while (isNumberInSlots(randomNumber));
        return randomNumber;
    }

    private static boolean isNumberInSlots(final int number) {
        return KitManager.slots.contains(number);
    }

    public static Kit getKit(final String name) {
        for (final Kit kit : getKits()) {
            if (name.contains(Utils.fixColor(kit.getName()))) {
                return kit;
            }
        }
        return null;
    }

    public static Kit getKitById(final String id) {
        for (final Kit kit : getKits()) {
            if (id.equalsIgnoreCase(kit.getId())) {
                return kit;
            }
        }
        return null;
    }


    public static List<Kit> getKits() {
        return KitManager.kits;
    }

    public static List<Integer> getSlots() {
        return KitManager.slots;
    }

    static {
        KitManager.kits = new ArrayList<>();
        KitManager.slots = new ArrayList<>();
    }
}
