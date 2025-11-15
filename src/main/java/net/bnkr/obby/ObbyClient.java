package net.bnkr.obby;

import net.bnkr.obby.autoclicker.HumanizedClicker;
import net.bnkr.obby.autoclicker.KeybindHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObbyClient implements ClientModInitializer {
    public static final String MOD_ID = "obbyclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static HumanizedClicker clicker;

    @Override
    public void onInitializeClient() {
        LOGGER.info("ObbyClient initializing...");

        clicker = new HumanizedClicker(MinecraftClient.getInstance());
        KeybindHandler.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (clicker != null) clicker.tick();
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (clicker != null && clicker.getCpsDisplay() != null) {
                clicker.getCpsDisplay().render(context);
            }
        });

        LOGGER.info("ObbyClient initialized!");
    }

    public static HumanizedClicker getClicker() {
        return clicker;
    }
}