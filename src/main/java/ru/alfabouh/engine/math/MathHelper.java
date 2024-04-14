package ru.alfabouh.engine.math;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class MathHelper {
    private static final int BF_SIN_BITS = 12;
    private static final int BF_SIN_MASK;
    private static final int BF_SIN_COUNT;
    private static final float BF_radFull;
    private static final float BF_radToIndex;
    private static final float BF_degFull;
    private static final float BF_degToIndex;
    private static final float[] BF_sin;
    private static final float[] BF_cos;

    static {
        BF_SIN_MASK = ~(-1 << BF_SIN_BITS);
        BF_SIN_COUNT = BF_SIN_MASK + 1;
        BF_radFull = 6.2831855F;
        BF_degFull = 360.0F;
        BF_radToIndex = (float) BF_SIN_COUNT / BF_radFull;
        BF_degToIndex = (float) BF_SIN_COUNT / BF_degFull;
        BF_sin = new float[BF_SIN_COUNT];
        BF_cos = new float[BF_SIN_COUNT];
        int i;
        for (i = 0; i < BF_SIN_COUNT; ++i) {
            BF_sin[i] = Math.sin(((float) i + 0.5F) / (float) BF_SIN_COUNT * BF_radFull);
            BF_cos[i] = Math.cos(((float) i + 0.5F) / (float) BF_SIN_COUNT * BF_radFull);
        }
        for (i = 0; i < 360; i += 90) {
            BF_sin[(int) ((float) i * BF_degToIndex) & BF_SIN_MASK] = (float) Math.sin((double) i * Math.PI / 180.0);
            BF_cos[(int) ((float) i * BF_degToIndex) & BF_SIN_MASK] = (float) Math.cos((double) i * Math.PI / 180.0);
        }
    }

    public static btVector3 convert(Vector3d vector3d) {
        return new btVector3(vector3d.x, vector3d.y, vector3d.z);
    }

    public static Vector3d convert(btVector3 vector3d) {
        return new Vector3d(vector3d.getX(), vector3d.getY(), vector3d.getZ());
    }

    public static float sin(double rad) {
        return BF_sin[(int) (rad * BF_radToIndex) & BF_SIN_MASK];
    }

    public static float cos(double rad) {
        return BF_cos[(int) (rad * BF_radToIndex) & BF_SIN_MASK];
    }

    public static int clamp(int d1, int d2, int d3) {
        return d1 < d2 ? d2 : (int) MathHelper.min(d1, d3);
    }

    public static float clamp(float d1, float d2, float d3) {
        return d1 < d2 ? d2 : MathHelper.min(d1, d3);
    }

    public static double clamp(double d1, double d2, double d3) {
        return d1 < d2 ? d2 : MathHelper.min(d1, d3);
    }

    public static float min(float d1, float d2) {
        return Math.min(d1, d2);
    }

    public static float max(float d1, float d2) {
        return Math.max(d1, d2);
    }

    public static double min(double d1, double d2) {
        return Math.min(d1, d2);
    }

    public static double max(double d1, double d2) {
        return Math.max(d1, d2);
    }

    public static Vector3f convertV3DV3F(Vector3d vector3d) {
        return new Vector3f((float) vector3d.x, (float) vector3d.y, (float) vector3d.z);
    }

    public static Vector3d convertV3FV3D(Vector3f vector3d) {
        return new Vector3d(vector3d.x, vector3d.y, vector3d.z);
    }

    public static Vector3d calcLookVector(Vector3d rotations) {
        double x = java.lang.Math.toRadians(rotations.x);
        double y = java.lang.Math.toRadians(rotations.y);
        double lX = MathHelper.sin(y) * MathHelper.cos(x);
        double lY = -MathHelper.sin(x);
        double lZ = -MathHelper.cos(y) * MathHelper.cos(x);
        return new Vector3d(lX, lY, lZ);
    }
}
