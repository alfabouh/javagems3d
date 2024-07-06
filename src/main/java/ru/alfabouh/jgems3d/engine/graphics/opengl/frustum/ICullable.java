package ru.alfabouh.jgems3d.engine.graphics.opengl.frustum;

import org.joml.Vector3f;

public interface ICullable {
    boolean canBeCulled();

    RenderSphere calcRenderSphere();

    class RenderSphere {
        private final float radius;
        private final Vector3f center;

        public RenderSphere(float radius, Vector3f center) {
            this.radius = radius;
            this.center = center;
        }

        public float getRadius() {
            return this.radius;
        }

        public Vector3f getCenter() {
            return this.center;
        }
    }
}
