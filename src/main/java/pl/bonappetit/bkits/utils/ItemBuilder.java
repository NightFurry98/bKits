package pl.bonappetit.bkits.utils;

import org.bukkit.enchantments.*;
import org.bukkit.potion.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;
import java.util.*;

public class ItemBuilder
{
    private Material mat;
    private int amount;
    private final short data;
    private String title;
    private final List<String> lore;
    private final HashMap<Enchantment, Integer> enchants;
    private Color color;
    private Potion potion;

    public ItemBuilder(final Material mat) {
        this(mat, 1);
    }

    public ItemBuilder(final Material mat, final int amount) {
        this(mat, amount, (short)0);
    }

    public ItemBuilder(final Material mat, final short data) {
        this(mat, 1, data);
    }

    public ItemBuilder(final Material mat, final int amount, final short data) {
        this.title = null;
        this.lore = new ArrayList<>();
        this.enchants = new HashMap<>();
        this.mat = mat;
        this.amount = amount;
        this.data = data;
    }

    public ItemBuilder setType(final Material mat) {
        this.mat = mat;
        return this;
    }

    public ItemBuilder setTitle(final String title) {
        this.title = Utils.fixColor(title);
        return this;
    }

    public ItemBuilder addLores(final List<String> lores) {
        this.lore.addAll(Utils.fixColor(lores));
        return this;
    }

    public ItemBuilder addLore(final String lore) {
        this.lore.add(Utils.fixColor(lore));
        return this;
    }

    public ItemBuilder addEnchantment(final Enchantment enchant, final int level) {
        if (this.enchants.containsKey(enchant)) {
            this.enchants.remove(enchant);
        }
        this.enchants.put(enchant, level);
        return this;
    }

    public ItemBuilder setColor(final Color color) {
        if (!this.mat.name().contains("LEATHER_")) {
            throw new IllegalArgumentException("Can only dye leather armor!");
        }
        this.color = color;
        return this;
    }

    public ItemBuilder setPotion(final Potion potion) {
        if (this.mat != Material.POTION) {
            this.mat = Material.POTION;
        }
        this.potion = potion;
        return this;
    }

    public ItemBuilder setAmount(final int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build() {
        Material mat = this.mat;
        if (mat == null) {
            mat = Material.AIR;
            Bukkit.getLogger().warning("Null material!");
        }
        final ItemStack item = new ItemStack(this.mat, this.amount, this.data);
        final ItemMeta meta = item.getItemMeta();
        if (this.title != null) {
            meta.setDisplayName(this.title);
        }
        if (!this.lore.isEmpty()) {
            meta.setLore((List)this.lore);
        }
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)meta).setColor(this.color);
        }
        item.setItemMeta(meta);
        item.addUnsafeEnchantments((Map)this.enchants);
        if (this.potion != null) {
            this.potion.apply(item);
        }
        return item;
    }

    public ItemBuilder clone() {
        final ItemBuilder newBuilder = new ItemBuilder(this.mat);
        newBuilder.setTitle(this.title);
        for (final String lore : this.lore) {
            newBuilder.addLore(lore);
        }
        for (final Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
            newBuilder.addEnchantment(entry.getKey(), entry.getValue());
        }
        newBuilder.setColor(this.color);
        newBuilder.potion = this.potion;
        return newBuilder;
    }

    public Material getType() {
        return this.mat;
    }

    public String getTitle() {
        return this.title;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean hasEnchantment(final Enchantment enchant) {
        return this.enchants.containsKey(enchant);
    }

    public int getEnchantmentLevel(final Enchantment enchant) {
        return this.enchants.get(enchant);
    }

    public HashMap<Enchantment, Integer> getAllEnchantments() {
        return this.enchants;
    }
}
