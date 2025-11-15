package net.bnkr.obby.autoclicker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import java.util.Random;

public class HumanizedClicker {
    private final MinecraftClient client;
    private final AutoClickerConfig config;
    private final Random random;
    private final CPSDisplay cpsDisplay;

    private long lastClickTime = 0;
    private long nextClickDelay = 0;

    public HumanizedClicker(MinecraftClient client) {
        this.client = client;
        this.config = AutoClickerConfig.getInstance();
        this.random = new Random();
        this.cpsDisplay = new CPSDisplay(client);
    }

    public void tick() {
        if (!config.enabled || client.player == null || client.currentScreen != null) {
            return;
        }

        if (config.onlyWhileHolding && !client.options.attackKey.isPressed()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= nextClickDelay) {
            performClick();
            lastClickTime = currentTime;
            nextClickDelay = calculateNextDelay();
        }
    }

    private void performClick() {
        if (config.humanizedPattern && random.nextDouble() < 0.0175) {
            return; // 1.75% miss chance
        }

        cpsDisplay.registerClick();

        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            net.minecraft.util.hit.EntityHitResult entityHit = (net.minecraft.util.hit.EntityHitResult) client.crosshairTarget;
            client.interactionManager.attackEntity(client.player, entityHit.getEntity());
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        } else if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            net.minecraft.util.hit.BlockHitResult blockHit = (net.minecraft.util.hit.BlockHitResult) client.crosshairTarget;
            client.interactionManager.attackBlock(blockHit.getBlockPos(), blockHit.getSide());
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        } else {
            client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }

    private long calculateNextDelay() {
        long baseDelay = (long) (1000.0 / config.targetCPS);

        if (!config.humanizedTiming) {
            return baseDelay;
        }

        double variation = (random.nextGaussian() * 0.5) * 0.15;
        variation = Math.max(-0.15, Math.min(0.15, variation));
        long variedDelay = (long) (baseDelay * (1 + variation));

        if (random.nextDouble() < 0.05) {
            variedDelay = (long) (variedDelay * (random.nextBoolean() ? 0.7 : 1.15));
        }

        return Math.max(10, variedDelay);
    }

    public CPSDisplay getCpsDisplay() {
        return cpsDisplay;
    }
}