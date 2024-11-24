package pl.bonappetit.bkits.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.bonappetit.bkits.BKits;
import pl.bonappetit.bkits.basic.Kit;
import pl.bonappetit.bkits.basic.User;
import pl.bonappetit.bkits.managers.KitManager;
import pl.bonappetit.bkits.managers.UserManager;

import java.util.stream.Collectors;

public class Gui {

    public static void openKitMenu(final Player p) {
        Configuration config = BKits.getPlugin().getConfig();
        final Inventory inv = Bukkit.createInventory(null, 45, Utils.fixColor(config.getString("gui.name")));
        String firstGlassColor = config.getString("gui.glass_color.first") + "_STAINED_GLASS_PANE";
        String secondGlassColor = config.getString("gui.glass_color.second") + "_STAINED_GLASS_PANE";
        final User user = UserManager.getUser(p.getUniqueId());
        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26,27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
        };

        for (int i : borderSlots) {
            String glassMaterialName = (i % 2 == 0) ? firstGlassColor : secondGlassColor;
            Material material = Material.getMaterial(glassMaterialName.toUpperCase());
            if (material == null) {
                continue;
            }
            ItemStack glassPane = new ItemStack(material);
            ItemMeta meta = glassPane.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(Utils.fixColor("&f#"));
                glassPane.setItemMeta(meta);
            }
            inv.setItem(i, glassPane);
        }
        for (final Kit kit : KitManager.getKits()) {
            final ItemBuilder is = new ItemBuilder(kit.getItem().getType())
                    .setTitle(config.getString("gui.kit.name").replace("[KIT_NAME]", ChatColor.stripColor(kit.getName())))
                    .addLores(config.getStringList("gui.kit.lore").stream()
                            .map(lore -> lore
                                    .replace("[HAS_PERMISSION]", p.hasPermission(kit.getPermission()) ? "&a\u2714" : "&c\u2716")
                                    .replace("[CAN_RECEIVE]", user.getKit(kit.getId()) < System.currentTimeMillis()
                                            ? "&a\u2714"
                                            : ("&c\u2716 &8(&7" + Utils.secondsToString(user.getKit(kit.getId())) + "&8)")))
                            .collect(Collectors.toList()));
            inv.setItem(kit.getSlot(), is.build());
        }
        p.openInventory(inv);
    }

    public static void openKitManageMenu(final Player p) {
        Configuration config = BKits.getPlugin().getConfig();
        final Inventory inv = Bukkit.createInventory(null, 54, Utils.fixColor(config.getString("mgui.name")));
        final ItemStack blank = new ItemBuilder(Material.LIGHT_BLUE_BANNER).setAmount(1).setTitle(Utils.fixColor("&7#")).build();
        for (int i = 0; i < inv.getSize(); ++i) {
            if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 45) {
                inv.setItem(i, blank);
            }
        }
        int[] redGlassSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };
        for (int slot : redGlassSlots) {
            inv.setItem(slot, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                    .setAmount(slot).setTitle(Utils.fixColor("&c")) .build());
        }

        for (final Kit kit : KitManager.getKits()) {
            final ItemBuilder is = new ItemBuilder(kit.getItem().getType())
                    .setTitle(config.getString("gui.kit.name").replace("[KIT_NAME]", ChatColor.stripColor(kit.getName())))
                    .addLores(config.getStringList("mgui.info_lore")).addLore(" ").addLore(" &f\u25ba Status: " + (kit.isEnable() ? "&a\u2714" : "&c\u2716"));
            inv.setItem(kit.getSlot(), is.build());
        }
        Material material = Material.RED_DYE;
        if (config.getBoolean("enable_kits")) {
            material = Material.LIME_DYE;
        }
        inv.setItem(40, new ItemBuilder(material).setTitle(config.getString("mgui.availability_title")).addLore(" ").addLore(" &f\u25ba Status: " + (config.getBoolean("enable_kits") ? "&a\u2714" : "&c\u2716")).addLore(" ").build());
        inv.setItem(39, new ItemBuilder(Material.CRIMSON_SIGN).setTitle(config.getString("mgui.new_kit")).addLores(config.getStringList("mgui.new_kit_lore")).build());
        inv.setItem(41, new ItemBuilder(Material.CLOCK).setTitle(config.getString("mgui.clock")).build());


        p.openInventory(inv);
    }

    public static void openKitCreateKitMenu(final Player p, String kitName) {
        Configuration config = BKits.getPlugin().getConfig();
        Bukkit.getScheduler().runTask(BKits.getPlugin(), () -> {
            final Inventory inv = Bukkit.createInventory(null, 45, Utils.fixColor(config.getString("mgui.create_name")));
            final ItemStack blank = new ItemBuilder(Material.LIGHT_GRAY_BANNER).setAmount(1).setTitle(Utils.fixColor("&7#")).build();

            for (int i = 0; i < inv.getSize(); ++i) {
                if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 36) {
                    inv.setItem(i, blank);
                }
            }

            int[] greenGlassSlots = {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };
            for (int slot : greenGlassSlots) {
                inv.setItem(slot, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                        .setAmount(slot).setTitle(config.getString("mgui.free_slot")).addLore(kitName).build());
            }

            for (final Kit kit : KitManager.getKits()) {
                final ItemBuilder is = new ItemBuilder(kit.getItem().getType())
                        .setTitle(config.getString("gui.kit.name").replace("[KIT_NAME]", ChatColor.stripColor(kit.getName())));
                inv.setItem(kit.getSlot(), is.build());
            }
            p.openInventory(inv);
        });
    }

    public static void openKitMenu(final Player player, final Kit kit) {
        final User user = UserManager.getUser(player.getUniqueId());
        Configuration config = BKits.getPlugin().getConfig();
        final Inventory inv = Bukkit.createInventory(null, 54, Utils.fixColor(config.getString("gui.open.name").replace("[KIT_NAME]", ChatColor.stripColor(kit.getName()))));
        final ItemStack blank = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setAmount(1).setTitle("&7#").build();
        for (int i = 0; i < inv.getSize(); ++i) {
            if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 45) {
                inv.setItem(i, blank);
            }
        }
        if (!player.hasPermission(kit.getPermission())) {
            inv.setItem(52, new ItemBuilder(Material.RED_DYE).setTitle(config.getString("gui.open.noperm")).build());
        }
        else if (user.getKit(kit.getId()) > System.currentTimeMillis()) {
            inv.setItem(52, new ItemBuilder(Material.YELLOW_DYE).setTitle(config.getString("gui.open.cooldown").replace("[TIME]", Utils.secondsToString(user.getKit(kit.getId())))).build());
        }
        else {
            inv.setItem(52, new ItemBuilder(Material.LIME_DYE).setTitle(config.getString("gui.open.canopen")).build());
        }
        for (final ItemStack is : kit.getItems()) {
            inv.addItem(is);
        }
        inv.setItem(46, new ItemBuilder(Material.OAK_BUTTON).setTitle(config.getString("gui.open.back")).build());
        player.openInventory(inv);
    }

    public static void openInvKitEdit(final Player player, final Kit kit) {
        Configuration config = BKits.getPlugin().getConfig();
        final Inventory inv = Bukkit.createInventory(null, 54, Utils.fixColor(config.getString("mgui.edit_kit_name").replace("[KIT_NAME]", ChatColor.stripColor(kit.getName()))));
        final ItemStack blank = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setAmount(1).setTitle("&7#").build();
        for (int i = 0; i < inv.getSize(); ++i) {
            if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 45) {
                inv.setItem(i, blank);
            }
        }
        for (final ItemStack is : kit.getItems()) {
            inv.addItem(is);
        }
        inv.setItem(46, new ItemBuilder(Material.LIME_DYE).setTitle(config.getString("mgui.save")).build());
        inv.setItem(52, new ItemBuilder(Material.OAK_BUTTON).setTitle(config.getString("gui.open.back")).build());
        player.openInventory(inv);
    }

}
