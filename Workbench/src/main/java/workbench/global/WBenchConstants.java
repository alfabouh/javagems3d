package workbench.global;

import javagems3d.JGems3D;

public abstract class WBenchConstants {
    public static float CAM_SENS = 0.001f;
    public static float FOV = (float) Math.toRadians(60.0f);
    public static float Z_NEAR = 0.1f;
    public static float Z_FAR = (float) JGems3D.MAP_MAX_SIZE * 8.0f;
}
