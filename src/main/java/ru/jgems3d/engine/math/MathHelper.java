package ru.jgems3d.engine.math;

import org.joml.Math;
import org.joml.Vector3f;

public class MathHelper {
    public static float lerp(float a, float b, float f)
    {
        return a + f * (b - a);
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

    public static Vector3f convertV3DV3F(Vector3f vector3f) {
        return new Vector3f(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f convertV3FV3D(Vector3f vector3f) {
        return new Vector3f(vector3f);
    }

    public static Vector3f calcLookVector(Vector3f rotations) {
        float x = rotations.x;
        float y = rotations.y;
        float lX = Math.sin(y) * Math.cos(x);
        float lY = -Math.sin(x);
        float lZ = -Math.cos(y) * Math.cos(x);
        return new Vector3f(lX, lY, lZ);
    }
}
