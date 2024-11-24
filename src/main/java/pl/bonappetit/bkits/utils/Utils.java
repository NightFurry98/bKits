package pl.bonappetit.bkits.utils;

import java.util.*;
import java.util.regex.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static String fixColor(String text) {
        final Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");
        for (Matcher matcher = pattern.matcher(text); matcher.find(); matcher = pattern.matcher(text)) {
            final String color = text.substring(matcher.start(), matcher.end());
            text = text.replace("&" + color, ChatColor.of(color) + "");
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> fixColor(final List<String> texts) {
        final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        final List<String> result = new ArrayList<>();
        for (String text : texts) {
            for (Matcher matcher = pattern.matcher(text); matcher.find(); matcher = pattern.matcher(text)) {
                final String color = text.substring(matcher.start(), matcher.end());
                text = text.replace("&" + color, ChatColor.of(color) + "");
            }
            result.add(ChatColor.translateAlternateColorCodes('&', text));
        }
        return result;
    }

    public static boolean sendMessage(final Player p, final String text) {
        p.sendMessage(fixColor(text));
        return true;
    }

    public static String mapLongToString(final Map<String, Long> map) {
        String toReturn = "";
        if (map == null || map.isEmpty() || map.size() == 0) {
            return "lack";
        }
        for (final Map.Entry<String, Long> entry : map.entrySet()) {
            toReturn = toReturn + entry.getKey() + "=" + entry.getValue() + ",";
        }
        toReturn = toReturn.substring(0, toReturn.length() - 1);
        return toReturn;
    }

    public static Map<String, Long> mapLongFromString(final String s) {
        final Map<String, Long> map = new LinkedHashMap<>();
        if (s == null || s.isEmpty() || s.equals("lack")) {
            return map;
        }
        final String[] split;
        final String[] args = split = s.split(",");
        for (final String arg : split) {
            final String[] kv = arg.split("=");
            map.put(kv[0], Long.valueOf(kv[1]));
        }
        return map;
    }

    public static void give(final Player player, final ItemStack itemStack) {
        final HashMap<Integer, ItemStack> playerItemsIneq = player.getInventory().addItem(itemStack);
        if (!playerItemsIneq.isEmpty()) {
            for (final ItemStack leftover : playerItemsIneq.values()) {
                player.getWorld().dropItem(player.getLocation(), leftover);
            }
        }
    }

    public static long parseDateDiff(final String time, final boolean future) {
        try {
            final Pattern timePattern = Pattern.compile(
                    "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" +
                            "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" +
                            "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" +
                            "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" +
                            "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" +
                            "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" +
                            "(?:([0-9]+)\\s*(?:s[a-z]*)?)?"
            );
            final Matcher matcher = timePattern.matcher(time);

            if (!matcher.matches()) {
                return -1L;
            }

            int[] timeUnits = new int[7];
            for (int i = 1; i <= 7; i++) {
                String group = matcher.group(i);
                if (group != null && !group.isEmpty()) {
                    timeUnits[i - 1] = Integer.parseInt(group);
                }
            }

            final Calendar calendar = new GregorianCalendar();
            addTimeToCalendar(calendar, Calendar.YEAR, timeUnits[0], future);
            addTimeToCalendar(calendar, Calendar.MONTH, timeUnits[1], future);
            addTimeToCalendar(calendar, Calendar.WEEK_OF_YEAR, timeUnits[2], future);
            addTimeToCalendar(calendar, Calendar.DAY_OF_YEAR, timeUnits[3], future);
            addTimeToCalendar(calendar, Calendar.HOUR_OF_DAY, timeUnits[4], future);
            addTimeToCalendar(calendar, Calendar.MINUTE, timeUnits[5], future);
            addTimeToCalendar(calendar, Calendar.SECOND, timeUnits[6], future);

            final Calendar max = new GregorianCalendar();
            max.add(Calendar.YEAR, 10);
            return calendar.after(max) ? max.getTimeInMillis() : calendar.getTimeInMillis();
        } catch (Exception e) {
            return -1L;
        }
    }

    private static void addTimeToCalendar(Calendar calendar, int field, int amount, boolean future) {
        if (amount > 0) {
            calendar.add(field, amount * (future ? 1 : -1));
        }
    }

    public static void sendTitleMessage(final Player player, final String title, final String subtitle, final int fadeit, final int stay, final int fadeout) {
        player.sendTitle(fixColor(title), fixColor(subtitle), fadeit, stay, fadeout);
    }

    public static void sendActionBar(final Player player, final String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(fixColor(message)));
    }

    private static final LinkedHashMap<Integer, String> values;

    public static String secondsToString(final long l) {
        int seconds = (int)((l - System.currentTimeMillis()) / 1000L);
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<Integer, String> e : values.entrySet()) {
            final int iDiv = seconds / e.getKey();
            if (iDiv >= 1) {
                final int x = (int)Math.floor(iDiv);
                sb.append(x + e.getValue()).append("");
                seconds -= x * e.getKey();
            }
        }
        return sb.toString();
    }

    static {
        (values = new LinkedHashMap<>(6)).put(31104000, "y");
        values.put(2592000, "msc");
        values.put(86400, "d");
        values.put(3600, "h");
        values.put(60, "min");
        values.put(1, "s");
    }
}
