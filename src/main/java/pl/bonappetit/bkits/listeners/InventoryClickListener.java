package pl.bonappetit.bkits.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import pl.bonappetit.bkits.BKits;
import pl.bonappetit.bkits.basic.Kit;
import pl.bonappetit.bkits.basic.User;
import pl.bonappetit.bkits.managers.KitManager;
import pl.bonappetit.bkits.managers.UserManager;
import pl.bonappetit.bkits.utils.Gui;
import pl.bonappetit.bkits.utils.ParseItemStack;
import pl.bonappetit.bkits.utils.SignMenuFactory;
import pl.bonappetit.bkits.utils.Utils;

import java.util.*;

public class InventoryClickListener implements Listener {

    public static Map<Player, List<String>> playerKitCreate = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        final User u = UserManager.getUser(p.getUniqueId());
        Configuration config = BKits.getPlugin().getConfig();
        final int slot = e.getSlot();
        if (e.getView().getTitle().contains(Utils.fixColor(config.getString("mgui.edit_kit_name").replace(" [KIT_NAME]", "")))) {
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(Utils.fixColor("&7#"))) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
                return;
            }
            if (slot == 52) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
                Gui.openKitManageMenu(p);
                return;
            }
            if (slot == 46) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
                final List<String> itemList = new ArrayList<>();
                for (int slota = 0; slota < p.getOpenInventory().getTopInventory().getSize(); ++slota) {
                    if (slota != 52 && slota != 46) {
                        final ItemStack item = p.getOpenInventory().getTopInventory().getItem(slota);
                        if (item != null && !item.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                            itemList.add(ParseItemStack.toString(item));
                        }
                    }
                }
                final Kit kit2 = KitManager.getKit(e.getView().getTitle());
                config.set("kits." + kit2.getId() + ".items", itemList);
                p.closeInventory();
                KitManager.reload();
                return;
            }
        }
        if (e.getView().getTitle().contains(Utils.fixColor(config.getString("gui.open.name").replace(" [KIT_NAME]", "")))) {
            if (e.getCurrentItem() == null) return;
            e.setCancelled(true);
            if (slot == 46) {
                Gui.openKitMenu(p);
            }
            final Kit kit = KitManager.getKit(e.getView().getTitle());
            if (kit == null) {
                return;
            }
            if (slot == 52) {
                if (!p.hasPermission(kit.getPermission())) {
                    Utils.sendMessage(p, config.getString("manage.messages.no_perm"));
                    p.closeInventory();
                } else if (u.getKit(kit.getId()) > System.currentTimeMillis()) {
                    Utils.sendMessage(p, config.getString("manage.messages.no_perm").replace("[TIME]", Utils.secondsToString(u.getKit(kit.getId()))));
                    p.closeInventory();
                } else {
                    p.closeInventory();
                    if (!p.isOp()) {
                        u.updateKit(kit.getId(), Utils.parseDateDiff(kit.getTime(), true));
                    }
                    Utils.sendMessage(p, config.getString("manage.messages.get_kit").replace("[KIT_NAME]", ChatColor.stripColor(kit.getName())));
                    for (final ItemStack is : kit.getItems()) {
                        Utils.give(p, is);
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equalsIgnoreCase(Utils.fixColor(config.getString("gui.name")))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            final Kit kit = KitManager.getKitBySlot(slot);
            if (kit == null) {
                return;
            }
            final boolean isKits = config.getBoolean("enable_kits");
            if (!isKits) {
                Utils.sendMessage(p, config.getString("manage.messages.kits_off"));
                return;
            }
            if (!kit.isEnable()) {
                Utils.sendMessage(p, config.getString("manage.messages.kits_off"));
                return;
            }
            p.closeInventory();
            Gui.openKitMenu(p, kit);
            return;
        }
        if (e.getView().getTitle().equalsIgnoreCase(Utils.fixColor(config.getString("mgui.name")))) {
            if (e.getCurrentItem() == null) return;
            e.setCancelled(true);
            if (slot == 39 && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.fixColor(config.getString("mgui.new_kit")))) {
                p.closeInventory();
                openCreateKitMenu(p, "name");
            }
            if (slot == 40 && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.fixColor(config.getString("mgui.availability_title")))) {
                boolean currentValue = config.getBoolean("enable_kits");
                boolean newValue = !currentValue;
                config.set("enable_kits", newValue);
                BKits.getPlugin().saveConfig();
                BKits.getPlugin().reloadConfig();
                Gui.openKitManageMenu(p);
                String status = BKits.getPlugin().getConfig().getString("manage.messages.statusoff");
                if (newValue) {
                    status = BKits.getPlugin().getConfig().getString("manage.messages.statuson");
                }
                for (final Player players : Bukkit.getOnlinePlayers()) {
                    Utils.sendTitleMessage(players, "", config.getString("manage.messages.change_allows").replace("[STATUS]", status).replace("[PLAYER]", p.getName()), 20, 25, 20);
                    Utils.sendMessage(players, " ");
                    Utils.sendMessage(players, config.getString("manage.messages.change_allows").replace("[STATUS]", status).replace("[PLAYER]", p.getName()));
                    Utils.sendMessage(players, " ");
                }
            }
            final Kit kit = KitManager.getKitBySlot(slot);
            if (kit == null) {
                return;
            }
            if (e.getClick().isRightClick() && !e.getClick().isShiftClick()) {
                p.closeInventory();
                openSignEditTime(p, kit);
                return;
            }
            if (e.getClick().isLeftClick() && !e.getClick().isShiftClick()) {
                p.closeInventory();
                Gui.openInvKitEdit(p, kit);
                return;
            }
            if (e.getClick().isLeftClick() && e.getClick().isShiftClick()) {
                Gui.openKitCreateKitMenu(p, kit.getName());
                return;
            }
            if (e.getClick().equals(ClickType.MIDDLE)) {
                config.set("kits." + kit.getId() + ".enable", !kit.isEnable());
                KitManager.reload();
                kit.setEnable(!kit.isEnable());
                Gui.openKitManageMenu(p);
                return;
            }
            if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                config.set("kits." + kit.getId(), null);
                KitManager.getKits().remove(kit);
                KitManager.reload();
                Gui.openKitManageMenu(p);
                return;
            }
        }
        if (e.getView().getTitle().equalsIgnoreCase(Utils.fixColor(config.getString("mgui.create_name")))) {
            if (e.getCurrentItem() == null) return;
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.fixColor(config.getString("mgui.free_slot")))) {
                if (!playerKitCreate.containsKey(p)) {
                    String kit_slot_name = Utils.fixColor(e.getCurrentItem().getItemMeta().getLore().get(0));
                    Kit kit = KitManager.getKit(kit_slot_name);
                    if (kit == null) return;
                    config.set("kits." + kit.getId() + ".slot", e.getSlot());
                    KitManager.reload();
                    p.closeInventory();
                } else {
                    List<String> kitData = playerKitCreate.get(p);
                    if (kitData == null || kitData.size() < 3) return;
                    String kitName = kitData.get(0);
                    String kitPerm = kitData.get(1);
                    String kitVisual = kitData.get(2);
                    String kitItem = kitData.get(3);
                    String kitTime = kitData.get(4);
                    List<String> defaultItems = Collections.singletonList("type=IRON_PICKAXE amount=1 DIG_SPEED=3 DURABILITY=3");
                    config.set("kits." + kitName + ".name", kitVisual);
                    config.set("kits." + kitName + ".item", "type="+kitItem+" amount=1");
                    config.set("kits." + kitName + ".permission", kitPerm);
                    config.set("kits." + kitName + ".time", kitTime);
                    config.set("kits." + kitName + ".enable", false);
                    config.set("kits." + kitName + ".slot", e.getSlot());
                    config.set("kits." + kitName + ".items", defaultItems);
                    KitManager.reload();
                    p.closeInventory();
                    playerKitCreate.remove(p);
                    Utils.sendMessage(p, config.getString("manage.messages.create"));
                }
            }
        }
    }

    public void openSignEditTime(Player p, Kit kit) {
        Configuration config = BKits.getPlugin().getConfig();
        SignMenuFactory factory = new SignMenuFactory(BKits.getPlugin());
        SignMenuFactory.Menu menu = factory.newMenu(Arrays.asList("",config.getString("mgui.sign.first_line_create_time"), config.getString("mgui.sign.second_line"), config.getString("mgui.sign.third_line")))
                .reopenIfFail(true)
                .response((player, strings) -> {
                    String input = strings[0].trim();
                    if (input.isEmpty()) {
                        Utils.sendMessage(p, BKits.getPlugin().getConfig().getString("manage.messages.invalid_new_kit_time"));
                        return false;
                    } else if (input.equalsIgnoreCase("cancel")) {
                        return true;
                    } else {
                        config.set("kits." + kit.getId() + ".time", input);
                        KitManager.reload();
                        return true;
                    }
                });
        menu.open(p);
    }

    public void openCreateKitMenu(Player p, String menuType) {
        Configuration config = BKits.getPlugin().getConfig();
        SignMenuFactory factory = new SignMenuFactory(BKits.getPlugin());
        Map<String, String> menuConfigKeys = new HashMap<>();
        menuConfigKeys.put("name", "mgui.sign.first_line_create_name");
        menuConfigKeys.put("perm", "mgui.sign.first_line_create_perm");
        menuConfigKeys.put("visual", "mgui.sign.first_line_create_visu");
        menuConfigKeys.put("item", "mgui.sign.first_line_create_item");
        menuConfigKeys.put("time", "mgui.sign.first_line_create_time");
        SignMenuFactory.Menu menu = factory.newMenu(Arrays.asList(
                        "",
                        config.getString(menuConfigKeys.get(menuType)),
                        config.getString("mgui.sign.second_line"),
                        config.getString("mgui.sign.third_line")
                ))
                .reopenIfFail(true)
                .response((player, strings) -> {
                    String input = strings[0].trim();

                    if (input.isEmpty()) {
                        Utils.sendMessage(p, config.getString("manage.messages.invalid_new_kit_" + menuType));
                        return false;
                    }
                    if ("cancel".equalsIgnoreCase(input)) {
                        playerKitCreate.remove(p);
                        return true;
                    }
                    if ("item".equals(menuType) && Material.matchMaterial(input) == null) {
                        Utils.sendMessage(p, config.getString("manage.messages.invalid_item_name"));
                        return false;
                    }
                    if ("name".equals(menuType) && KitManager.getKitById(input) != null) {
                        Utils.sendMessage(p, config.getString("manage.messages.invalid_new_kit_name"));
                        return false;
                    }
                    playerKitCreate.computeIfAbsent(p, k -> new ArrayList<>()).add(input);
                    nextStep(p, menuType);
                    return true;
                });

        menu.open(p);
    }

    private void nextStep(Player p, String currentMenuType) {
        switch (currentMenuType) {
            case "name":
                openCreateKitMenu(p, "perm");
                break;
            case "perm":
                openCreateKitMenu(p, "visual");
                break;
            case "visual":
                openCreateKitMenu(p, "item");
                break;
            case "item":
                openCreateKitMenu(p, "time");
                break;
            case "time":
                Gui.openKitCreateKitMenu(p, playerKitCreate.get(p).get(0));
                break;
            default:
                throw new IllegalArgumentException("Invalid menu type: " + currentMenuType);
        }
    }
}
