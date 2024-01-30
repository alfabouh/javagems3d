package ru.BouH.engine.render.frustum;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class RenderABB {
    private final Vector3f abbMin;
    private final Vector3f abbMax;

    public RenderABB() {
        this.abbMin = new Vector3f(0);
        this.abbMax = new Vector3f(0);
    }

    public void setAbbForm(Vector3d center, Vector3d size) {
        double d1 = size.x / 2.0f;
        double d2 = size.y / 2.0f;
        double d3 = size.z / 2.0f;
        this.abbMin.set((float) (center.x - d1), (float) (center.y - d2), (float) (center.z - d3));
        this.abbMax.set((float) (center.x + d1), (float) (center.y + d2), (float) (center.z + d3));
    }

    public Vector3f getAbbMin() {
        return this.abbMin;
    }

    public Vector3f getAbbMax() {
        return this.abbMax;
    }
}
