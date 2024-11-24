package pl.bonappetit.bkits;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.bonappetit.bkits.command.KitCommand;
import pl.bonappetit.bkits.database.MySQL;
import pl.bonappetit.bkits.listeners.InventoryClickListener;
import pl.bonappetit.bkits.listeners.PlayerJoinListener;
import pl.bonappetit.bkits.managers.KitManager;
import pl.bonappetit.bkits.managers.UserManager;
import pl.bonappetit.bkits.utils.SignMenuFactory;

import java.sql.SQLException;

public final class BKits extends JavaPlugin {

    private static BKits plugin;
    private SignMenuFactory signMenuFactory;


    @Override
    public void onEnable() {
        final PluginManager pm = Bukkit.getPluginManager();
        (BKits.plugin = this).saveDefaultConfig();
        MySQL.connect();
        registerDatabase();
        UserManager.loadUsers();
        KitManager.load();
        this.signMenuFactory = new SignMenuFactory(this);

        this.getCommand("kit").setExecutor(new KitCommand());
        pm.registerEvents(new InventoryClickListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        this.getLogger().info(" ");
        this.getLogger().info("<<------------>> [BKITS] <<------------>>");
        this.getLogger().info(" ");
        this.getLogger().info(">> The plugin has been successfully enabled! <<");
        this.getLogger().info(" ");
        this.getLogger().info("<<------------>> [BKITS] <<------------>>");
        this.getLogger().info(" ");
    }

    public void registerDatabase() {
        MySQL.update("CREATE TABLE IF NOT EXISTS `bonappetit_kits` (`id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT, `uuid` char(100) not null, `kits` text not null);");
    }

    public static BKits getPlugin() {
        return BKits.plugin;
    }

    @Override
    public void onDisable() {
        try {
            MySQL.disconnect();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
