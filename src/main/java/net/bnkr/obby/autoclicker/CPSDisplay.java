package net.bnkr.obby.autoclicker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

public class CPSDisplay {
    private final MinecraftClient client;
    private final AutoClickerConfig config;
    private final List<Long> recentClicks = new ArrayList<>();

    public CPSDisplay(MinecraftClient client) {
        this.client = client;
        this.config = AutoClickerConfig.getInstance();
    }

    public void registerClick() {
        long now = System.currentTimeMillis();
        recentClicks.add(now);
        recentClicks.removeIf(time -> now - time > 1000);
    }

    public void render(DrawContext context) {
        if (!config.cpsDisplayEnabled) return;

        int x = config.cpsDisplayX;
        int y = config.cpsDisplayY;
        double cps = recentClicks.size();

        // Background
        context.fill(x - 2, y - 2, x + 82, y + 32, 0x80000000);
        context.fill(x, y, x + 80, y + 30, 0xCC000000);

        // CPS text
        String cpsText = String.format("%.1f CPS", cps);
        context.drawTextWithShadow(client.textRenderer, cpsText, x + 5, y + 10, getColor(cps));
    }

    private int getColor(double cps) {
        if (cps < 5) return 0xFFFFFF;
        if (cps < 10) return 0x55FF55;
        if (cps < 15) return 0xFFFF55;
        if (cps < 20) return 0xFFAA00;
        return 0xFF5555;
    }
}