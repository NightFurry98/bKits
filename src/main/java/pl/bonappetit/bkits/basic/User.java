package pl.bonappetit.bkits.basic;

import lombok.Data;
import org.bukkit.entity.Player;
import pl.bonappetit.bkits.database.MySQL;
import pl.bonappetit.bkits.managers.KitManager;
import pl.bonappetit.bkits.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public @Data class User {

    private UUID uuid;
    private Map<String, Long> kits;

    public User(Player p) {
        this.uuid = p.getUniqueId();
        this.kits = new LinkedHashMap<>();
        for (final Kit kit : KitManager.getKits()) {
            this.getKits().put(kit.getId(), 0L);
        }
        MySQL.update("INSERT INTO `bonappetit_kits`(`id`, `uuid`, `kits`) VALUES (NULL, '" + this.getUuid() + "', '" + Utils.mapLongToString(this.getKits()) + "');");
    }

    public User(final ResultSet rs) {
        try {
            this.uuid = UUID.fromString(rs.getString("uuid"));
            this.kits = Utils.mapLongFromString(rs.getString("kits"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        for (Kit kit : KitManager.getKits()) {
            if (!this.kits.containsKey(kit.getId())) {
                this.kits.put(kit.getId(), 0L);
            }
        }
        this.kits.keySet().removeIf(kitId ->
                KitManager.getKits().stream().noneMatch(kit -> kit.getId().equals(kitId))
        );
    }

    public void save() {
        for (Kit kit : KitManager.getKits()) {
            if (!this.kits.containsKey(kit.getId())) {
                this.kits.put(kit.getId(), 0L);
            }
        }
        this.kits.keySet().removeIf(kitId ->
                KitManager.getKits().stream().noneMatch(kit -> kit.getId().equals(kitId))
        );

        MySQL.update("UPDATE `bonappetit_kits` SET `kits` = '"
                + Utils.mapLongToString(this.getKits()) + "' WHERE `uuid` = '" + this.getUuid() + "';");
    }

    public void updateKit(final String id, final long l) {
        this.getKits().put(id, l);
    }

    public long getKit(final String id) {
        return this.getKits().get(id);
    }
}
