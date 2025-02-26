package javagems3d.help;

import org.joml.Math;
import org.joml.Vector3f;

public abstract class JGemsMathHelper {
    public static float interpolate(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static int clamp(int d1, int d2, int d3) {
        return d1 < d2 ? d2 : (int) Math.min(d1, d3);
    }

    public static float clamp(float d1, float d2, float d3) {
        return d1 < d2 ? d2 : Math.min(d1, d3);
    }

    public static double clamp(double d1, double d2, double d3) {
        return d1 < d2 ? d2 : Math.min(d1, d3);
    }

    public static void clampVectorToZeroThreshold(Vector3f in, float threshold) {
        if (in.x > -threshold && in.x < threshold) {
            in.x = 0.0f;
        }
        if (in.y > -threshold && in.y < threshold) {
            in.y = 0.0f;
        }
        if (in.z > -threshold && in.z < threshold) {
            in.z = 0.0f;
        }
    }
}
