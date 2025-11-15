package net.bnkr.obby.autoclicker;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    private static KeyBinding toggleKey;
    private static KeyBinding cpsToggleKey;
    private static KeyBinding cpsUpKey;
    private static KeyBinding cpsDownKey;

    public static void register() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.obbyclient.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.obbyclient"
        ));

        cpsToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle CPS Display",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.obbyclient"
        ));

        cpsUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Increase CPS",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL,
                "category.obbyclient"
        ));

        cpsDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Decrease CPS",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "category.obbyclient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Toggle auto clicker on/off
            while (toggleKey.wasPressed()) {
                AutoClickerConfig config = AutoClickerConfig.getInstance();
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

            // Toggle CPS display
            while (cpsToggleKey.wasPressed()) {
                AutoClickerConfig config = AutoClickerConfig.getInstance();
                config.cpsDisplayEnabled = !config.cpsDisplayEnabled;

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§7[CPS Display] " +
                                    (config.cpsDisplayEnabled ? "§aON" : "§cOFF")),
                            true
                    );
                }
            }

            // Increase CPS
            while (cpsUpKey.wasPressed()) {
                AutoClickerConfig config = AutoClickerConfig.getInstance();
                config.targetCPS = Math.min(20.0, config.targetCPS + 1.0);

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§7[CPS] §f" + String.format("%.1f", config.targetCPS)),
                            true
                    );
                }
            }

            // Decrease CPS
            while (cpsDownKey.wasPressed()) {
                AutoClickerConfig config = AutoClickerConfig.getInstance();
                config.targetCPS = Math.max(1.0, config.targetCPS - 1.0);

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§7[CPS] §f" + String.format("%.1f", config.targetCPS)),
                            true
                    );
                }
            }
        });
    }
}