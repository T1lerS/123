package net.bnkr.obby.autoclicker;

public class AutoClickerConfig {
    private static AutoClickerConfig INSTANCE;

    public boolean enabled = false;
    public boolean humanizedTiming = true;
    public boolean humanizedPattern = true;
    public boolean onlyWhileHolding = true;
    public double targetCPS = 10.0;
    public boolean cpsDisplayEnabled = true;
    public int cpsDisplayX = 10;
    public int cpsDisplayY = 10;

    private AutoClickerConfig() {}

    public static AutoClickerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoClickerConfig();
        }
        return INSTANCE;
    }
}