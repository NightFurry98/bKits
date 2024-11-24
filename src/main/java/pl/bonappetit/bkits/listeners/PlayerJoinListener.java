package pl.bonappetit.bkits.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.bonappetit.bkits.basic.User;
import pl.bonappetit.bkits.managers.UserManager;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        User user = UserManager.getUser(p.getUniqueId());
        if (user == null) {
            UserManager.createUserData(p);
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        User user = UserManager.getUser(p.getUniqueId());
        if (user != null) {
            user.save();
        }
    }
}
