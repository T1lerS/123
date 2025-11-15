package net.bnkr.obby;

import net.bnkr.obby.autoclicker.HumanizedClicker;
import net.bnkr.obby.autoclicker.KeybindHandler;   // ✅ corrected import
import net.bnkr.obby.gui.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObbyClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("obbyclient");
    private static HumanizedClicker clicker;

    @Override
    public void onInitializeClient() {
        LOGGER.info("ObbyClient initializing...");

        // Load config at startup
        ConfigManager.load();

        // Register keybinds
        KeybindHandler.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (clicker == null && client != null) {
                clicker = new HumanizedClicker(client);
            }
            if (clicker != null) {
                clicker.tick();
            }
        });

        LOGGER.info("ObbyClient initialized!");
    }
}
