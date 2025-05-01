package maze;

public class ProceduralSettings {

    // === MAZE SETTINGS ===
    public static final float MAZE_TOP_HEIGHT_MIN = 0.2f;
    public static final float MAZE_TOP_HEIGHT_MAX = 0.5f;
    public static final float MAZE_BOTTOM_HEIGHT_MIN = 0.2f;
    public static final float MAZE_BOTTOM_HEIGHT_MAX = 0.5f;
    public static final float MAZE_HEIGHT_THRESHOLD_FOR_SPAWN = 0.6f;

    // === CORAL SPAWN SETTINGS ===
    public static final float CORAL_SPAWN_INTERVAL = 2.0f;
    public static final float CORAL_WIDTH_MIN = 0.05f;
    public static final float CORAL_WIDTH_MAX = 0.15f;
    public static final float CORAL_HEIGHT_MIN = 0.1f;
    public static final float CORAL_HEIGHT_MAX = 0.3f;
    public static final float CORAL_Y_MIN = 0.0f;
    public static final float CORAL_Y_MAX = 0.7f;

    public static final float CORAL_SPEED_BASE = 0.5f;
    public static final float CORAL_SPEED_VARIATION = 1.0f;

    // === USER-TUNABLE SETTINGS ===
    private static float coralScale = 1.0f;

    public static float getCoralScale() {
        return coralScale;
    }

    public static void setCoralScale(float scale) {
        coralScale = scale;
    }

    // === Coral Type Frequency Control ===
    private static float coralTrashRatio = 0.5f; // 0.0 to 1.0 → 0.5 = 50% coral, 50% trash

    public static float getCoralTrashRatio() {
        return coralTrashRatio;
    }

    public static void setCoralTrashRatio(float ratio) {
        coralTrashRatio = Math.max(0.0f, Math.min(1.0f, ratio)); // Clamp between 0 and 1
    }
}
