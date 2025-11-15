package net.bnkr.obby.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.bnkr.obby.autoclicker.AutoClickerConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/obbyclient.json");

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                AutoClickerConfig loaded = GSON.fromJson(reader, AutoClickerConfig.class);
                AutoClickerConfig.setInstance(loaded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(AutoClickerConfig.getInstance(), writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
