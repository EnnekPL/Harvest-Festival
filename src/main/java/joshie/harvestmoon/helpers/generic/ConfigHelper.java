package joshie.harvestmoon.helpers.generic;

import net.minecraftforge.common.config.Configuration;

public class ConfigHelper {
    private static Configuration config;
    private static String category = "General";

    public static void setConfig(Configuration config) {
        ConfigHelper.config = config;
        ConfigHelper.category = "General";
    }
    
    public static void setCategory(String category) {
        ConfigHelper.category = category;
    }
    
    public static boolean getBoolean(String name, boolean dft) {
        return config.get(category, name, dft).getBoolean(dft);
    }
    
    public static int getInteger(String name, int dft) {
        return config.get(name, name, dft).getInt();
    }
    
    public static long getLong(String name, long dft) {
        return Long.parseLong(config.get(name, name, "" + dft).getString());
    }
}