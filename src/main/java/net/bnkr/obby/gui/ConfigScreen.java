package net.bnkr.obby.gui;

import net.bnkr.obby.autoclicker.AutoClickerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final AutoClickerConfig config;

    private SliderWidget cpsSlider;
    private SliderWidget missSlider;
    private SliderWidget jitterSlider;
    private SliderWidget driftPeriodSlider;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("screen.obbyclient.title"));
        this.parent = parent;
        this.config = AutoClickerConfig.getInstance();
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = 40;

        // Enable/disable clicker
        addDrawableChild(ButtonWidget.builder(
                Text.translatable(config.enabled ? "screen.obbyclient.disable" : "screen.obbyclient.enable"),
                button -> {
                    config.enabled = !config.enabled;
                    button.setMessage(Text.translatable(config.enabled ? "screen.obbyclient.disable" : "screen.obbyclient.enable"));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        // Only while holding
        addDrawableChild(ButtonWidget.builder(
                Text.translatable(config.onlyWhileHolding ? "screen.obbyclient.hold_only_on" : "screen.obbyclient.hold_only_off"),
                button -> {
                    config.onlyWhileHolding = !config.onlyWhileHolding;
                    button.setMessage(Text.translatable(config.onlyWhileHolding ? "screen.obbyclient.hold_only_on" : "screen.obbyclient.hold_only_off"));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        // Humanized timing toggle
        addDrawableChild(ButtonWidget.builder(
                Text.translatable(config.humanizedTiming ? "screen.obbyclient.human_timing_on" : "screen.obbyclient.human_timing_off"),
                button -> {
                    config.humanizedTiming = !config.humanizedTiming;
                    button.setMessage(Text.translatable(config.humanizedTiming ? "screen.obbyclient.human_timing_on" : "screen.obbyclient.human_timing_off"));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        // CPS slider
        double cpsNorm = (config.targetCPS - config.minCPS) / (config.maxCPS - config.minCPS);
        cpsSlider = new SliderWidget(centerX - 100, y, 200, 20, Text.translatable("screen.obbyclient.cps"), cpsNorm) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable("screen.obbyclient.cps_value",
                        String.format("%.1f", config.targetCPS)));
            }
            @Override
            protected void applyValue() {
                config.targetCPS = config.minCPS + value * (config.maxCPS - config.minCPS);
            }
        };
        addDrawableChild(cpsSlider);
        y += 30;

        // Miss chance slider
        missSlider = new SliderWidget(centerX - 100, y, 200, 20, Text.translatable("screen.obbyclient.miss"), clamp01(config.missChance)) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable("screen.obbyclient.miss_value",
                        String.format("%.2f", config.missChance)));
            }
            @Override
            protected void applyValue() {
                config.missChance = value;
            }
        };
        addDrawableChild(missSlider);
        y += 30;

        // Jitter chance slider
        jitterSlider = new SliderWidget(centerX - 100, y, 200, 20, Text.translatable("screen.obbyclient.jitter"), clamp01(config.jitterChance)) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable("screen.obbyclient.jitter_value",
                        String.format("%.2f", config.jitterChance)));
            }
            @Override
            protected void applyValue() {
                config.jitterChance = value;
            }
        };
        addDrawableChild(jitterSlider);
        y += 30;

        // Drift period slider
        double driftNorm = (config.cpsDriftPeriodMs - 5_000) / (90_000 - 5_000);
        driftPeriodSlider = new SliderWidget(centerX - 100, y, 200, 20, Text.translatable("screen.obbyclient.drift_period"), clamp01(driftNorm)) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable("screen.obbyclient.drift_period_value",
                        String.format("%.0f", config.cpsDriftPeriodMs / 1000.0)));
            }
            @Override
            protected void applyValue() {
                double seconds = 5 + value * (90 - 5);
                config.cpsDriftPeriodMs = (long)(seconds * 1000.0);
            }
        };
        addDrawableChild(driftPeriodSlider);

        // Done button
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.obbyclient.done"), button -> close())
                .dimensions(centerX - 100, height - 40, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fill with semi-transparent black
        context.fill(0, 0, this.width, this.height, 0xAA000000);

        // Title text
        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable("screen.obbyclient.title"),
                width / 2,
                15,
                0xFFFFFF
        );

        super.render(context, mouseX, mouseY, delta);
    }



    @Override
    public void close() {
        // Save settings when leaving the GUI
        net.bnkr.obby.gui.ConfigManager.save();   // ✅ corrected import path
        MinecraftClient.getInstance().setScreen(parent);
    }


    private double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
