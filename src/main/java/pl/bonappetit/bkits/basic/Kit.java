package pl.bonappetit.bkits.basic;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public @Data class Kit {

    private String id;
    private String name;
    private ItemStack item;
    private String permission;
    private String time;
    private List<ItemStack> items;
    private int slot;
    private boolean enable;

    public Kit(final String id, final String name, final ItemStack item, final String permission, final String time, final List<ItemStack> items, final int slot, final boolean enable) {
        this.setId(id);
        this.setName(name);
        this.setItem(item);
        this.setPermission(permission);
        this.setTime(time);
        this.setItems(items);
        this.setSlot(slot);
        this.setEnable(enable);
    }

    public String getId() {
        return this.id;
    }
}
