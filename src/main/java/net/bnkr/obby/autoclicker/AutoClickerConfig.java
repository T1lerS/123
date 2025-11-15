package net.bnkr.obby.autoclicker;

public class AutoClickerConfig {
    private static AutoClickerConfig INSTANCE;

    // Core toggles
    public boolean enabled = false;
    public boolean humanizedTiming = true;
    public boolean humanizedPattern = true;
    public boolean onlyWhileHolding = true;

    // CPS settings
    public double targetCPS = 13.0;
    public double minCPS = 9.0;
    public double maxCPS = 16.0;

    // Humanization parameters
    public double missChance = 0.018;
    public double jitterChance = 0.08;
    public double jitterFast = 0.70;
    public double jitterSlow = 1.20;
    public double variationStd = 0.07;
    public double variationClamp = 0.18;

    // Slow drift over time
    public boolean cpsDriftEnabled = true;
    public long cpsDriftPeriodMs = 50000;
    public double cpsDriftStep = 1.0;

    // GUI state (optional)
    public boolean cpsDisplayEnabled = false;
    public int cpsDisplayX = 10;
    public int cpsDisplayY = 10;

    private AutoClickerConfig() {}

    public static synchronized AutoClickerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoClickerConfig();
        }
        return INSTANCE;
    }

    // ✅ Setter so ConfigManager can replace the singleton
    public static synchronized void setInstance(AutoClickerConfig newInstance) {
        if (newInstance != null) {
            INSTANCE = newInstance;
        }
    }
}
