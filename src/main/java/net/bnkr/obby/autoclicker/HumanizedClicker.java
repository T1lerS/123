package net.bnkr.obby.autoclicker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Hand;

import java.util.Random;

public class HumanizedClicker {
    private final MinecraftClient client;
    private final AutoClickerConfig config;
    private final Random random;

    private long lastClickMs = 0;
    private long nextClickDelayMs = 0;

    // Drift tracking
    private long lastDriftUpdateMs = 0;
    private double driftTargetCPS;

    public HumanizedClicker(MinecraftClient client) {
        this.client = client;
        this.config = AutoClickerConfig.getInstance();
        this.random = new Random();
        this.driftTargetCPS = config.targetCPS;
    }

    public void tick() {
        if (!config.enabled || client.player == null || client.currentScreen != null) {
            return;
        }
        if (config.onlyWhileHolding && !client.options.attackKey.isPressed()) {
            return;
        }
        if (client.interactionManager == null) {
            return;
        }

        long nowMs = System.nanoTime() / 1_000_000;

        // Slow CPS drift
        if (config.cpsDriftEnabled && (nowMs - lastDriftUpdateMs) >= config.cpsDriftPeriodMs) {
            lastDriftUpdateMs = nowMs;
            double step = random.nextBoolean() ? config.cpsDriftStep : -config.cpsDriftStep;
            driftTargetCPS = clamp(config.minCPS, config.maxCPS, driftTargetCPS + step);
        }

        // Recalculate delay if needed
        if (nowMs - lastClickMs >= nextClickDelayMs) {
            performClick();
            lastClickMs = nowMs;
            nextClickDelayMs = calculateNextDelayMs();
        }
    }

    private void performClick() {
        // Miss chance to simulate imperfect human input
        if (config.humanizedPattern && random.nextDouble() < config.missChance) {
            return;
        }

        HitResult target = client.crosshairTarget;
        if (target != null && target.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) target;
            client.interactionManager.attackEntity(client.player, entityHit.getEntity());
            client.player.swingHand(Hand.MAIN_HAND);
        } else if (target != null && target.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) target;
            client.interactionManager.attackBlock(blockHit.getBlockPos(), blockHit.getSide());
            client.player.swingHand(Hand.MAIN_HAND);
        } else {
            client.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private long calculateNextDelayMs() {
        double cps = config.humanizedTiming ? driftTargetCPS : config.targetCPS;
        cps = clamp(config.minCPS, config.maxCPS, cps);

        long baseDelay = (long) (1000.0 / cps);
        if (!config.humanizedTiming) {
            return Math.max(10, baseDelay);
        }

        // Gaussian variation (clamped)
        double variation = random.nextGaussian() * config.variationStd;
        variation = clamp(-config.variationClamp, config.variationClamp, variation);
        long variedDelay = (long) (baseDelay * (1.0 + variation));

        // Occasional jitter burst/slow
        if (random.nextDouble() < config.jitterChance) {
            double factor = random.nextBoolean() ? config.jitterFast : config.jitterSlow;
            variedDelay = (long) (variedDelay * factor);
        }

        // Micro jitter within ±5ms to break rhythmic patterns
        int microJitter = random.nextInt(11) - 5; // [-5..+5]
        variedDelay += microJitter;

        return Math.max(15, variedDelay);
    }

    private double clamp(double min, double max, double v) {
        return Math.max(min, Math.min(max, v));
    }
}
