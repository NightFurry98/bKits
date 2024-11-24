package pl.bonappetit.bkits.managers;

import org.bukkit.entity.Player;
import pl.bonappetit.bkits.basic.User;
import pl.bonappetit.bkits.database.Logger;
import pl.bonappetit.bkits.database.MySQL;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class UserManager {

    private static Map<UUID, User> users;

    public static void createUserData(final Player p) {
        final User u = new User(p);
        UserManager.users.put(p.getUniqueId(), u);
    }

    public static User getUser(final UUID uuid) {
        return UserManager.users.get(uuid);
    }

    public static void loadUsers() {
        try {
            final ResultSet rs = MySQL.query("SELECT * FROM `bonappetit_kits`");
            while (rs.next()) {
                final User u = new User(rs);
                UserManager.users.put(u.getUuid(), u);
            }
            rs.close();
        }
        catch (SQLException e) {
            Logger.info("Can not load players Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    static {
        UserManager.users = new ConcurrentHashMap<>();
    }
}
