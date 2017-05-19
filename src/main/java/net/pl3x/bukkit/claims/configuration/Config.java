package net.pl3x.bukkit.claims.configuration;

import net.pl3x.bukkit.claims.Pl3xClaims;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean COLOR_LOGS = true;
    public static boolean DEBUG_MODE = false;
    public static String LANGUAGE_FILE = "lang-en.yml";

    public static List<String> ENABLED_WORLDS = new ArrayList<>();

    public static String CLAIM_TOOL_MATERIAL = "STICK";
    public static byte CLAIM_TOOL_DATA = (byte) 0;
    public static String CLAIM_TOOL_NAME = "STICK";
    public static List<String> CLAIM_TOOL_LORE = new ArrayList<>();

    private Config() {
    }

    public static void reload() {
        Pl3xClaims plugin = Pl3xClaims.getPlugin();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");

        ENABLED_WORLDS = config.getStringList("enabled-worlds");

        CLAIM_TOOL_MATERIAL = config.getString("claim-tool.material", "STICK");
        CLAIM_TOOL_DATA = (byte) config.getInt("claim-tool.data", 0);
        CLAIM_TOOL_NAME = ChatColor.translateAlternateColorCodes('&',
                config.getString("claim-tool.name", "Claim Tool"));
        CLAIM_TOOL_LORE.clear();
        config.getStringList("claim-tool.lore").forEach(lore ->
                CLAIM_TOOL_LORE.add(ChatColor.translateAlternateColorCodes('&', lore)));

    }

    public static boolean isWorldEnabled(World world) {
        for (String name : ENABLED_WORLDS) {
            if (name.equalsIgnoreCase(world.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isClaimTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false; // no item
        }
        if (!item.getType().name().equals(Config.CLAIM_TOOL_MATERIAL)) {
            return false; // wrong material
        }
        //noinspection deprecation
        if (item.getData().getData() != Config.CLAIM_TOOL_DATA) {
            return false; // wrong data
        }
        if (Config.CLAIM_TOOL_NAME != null && !Config.CLAIM_TOOL_NAME.equals("")) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            if (!item.getItemMeta().getDisplayName().equals(Config.CLAIM_TOOL_NAME)) {
                return false; // name mismatch
            }
        }
        if (Config.CLAIM_TOOL_LORE != null && !Config.CLAIM_TOOL_LORE.isEmpty()) {
            if (!item.hasItemMeta()) {
                return false; // no item meta
            }
            for (String lore : Config.CLAIM_TOOL_LORE) {
                if (!item.getItemMeta().getLore().contains(lore)) {
                    return false; // name mismatch
                }
            }
        }
        return true;
    }
}