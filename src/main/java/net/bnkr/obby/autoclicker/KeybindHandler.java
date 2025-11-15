package net.bnkr.obby.autoclicker;

import net.bnkr.obby.gui.ConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    private static KeyBinding toggleKey;
    private static KeyBinding cpsToggleKey; // kept for compatibility; toggles GUI state
    private static KeyBinding cpsUpKey;
    private static KeyBinding cpsDownKey;
    private static KeyBinding openGuiKey;

    public static void register() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.obbyclient.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.obbyclient"
        ));

        cpsToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.obbyclient.toggle_cps",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.obbyclient"
        ));

        cpsUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.obbyclient.increase_cps",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL,
                "category.obbyclient"
        ));

        cpsDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.obbyclient.decrease_cps",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "category.obbyclient"
        ));

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.obbyclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.obbyclient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AutoClickerConfig config = AutoClickerConfig.getInstance();

            while (toggleKey.wasPressed()) {
                config.enabled = !config.enabled;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§7[Auto Clicker] " +
                                    (config.enabled ? "§aEnabled" : "§cDisabled") +
                                    " §7(§f" + String.format("%.1f", config.targetCPS) + " CPS§7)"),
                            true
                    );
                }
            }

            // Legacy: toggle flag; no HUD anymore
            while (cpsToggleKey.wasPressed()) {
                config.cpsDisplayEnabled = !config.cpsDisplayEnabled;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§7[CPS HUD] " +
                                    (config.cpsDisplayEnabled ? "§aON (legacy flag)" : "§cOFF (legacy flag)")),
                            true
                    );
                }
            }

            while (cpsUpKey.wasPressed()) {
                config.targetCPS = Math.min(config.maxCPS, config.targetCPS + 1.0);
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§7[CPS] §f" + String.format("%.1f", config.targetCPS)),
                            true
                    );
                }
            }

            while (cpsDownKey.wasPressed()) {
                config.targetCPS = Math.max(config.minCPS, config.targetCPS - 1.0);
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§7[CPS] §f" + String.format("%.1f", config.targetCPS)),
                            true
                    );
                }
            }

            while (openGuiKey.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }
        });
    }
}
