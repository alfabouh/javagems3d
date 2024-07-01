package ru.alfabouh.jgems3d.engine.physics.objects.materials;

import org.joml.Vector3f;

@SuppressWarnings("all")
public class Materials {
    public static MaterialProperties defaultMaterial = new MaterialProperties("defaultMaterial");
    public static MaterialProperties grassGround = new MaterialProperties("grassGround").setFriction(5.0f).setRestitution(0.0f);
    public static MaterialProperties brickCube = new MaterialProperties("brickCube").setFriction(7.5f).setRestitution(0.0f);
    public static MaterialProperties liquid = new MaterialProperties("liquid").setFriction(0.0f).setRestitution(0.5f);
    public static MaterialProperties particle = new MaterialProperties("particle").setFriction(1.0f).setRestitution(0.1f);

    public static class MaterialProperties {
        public static final float DEFAULT_FRICTION_X = 1.0f;
        public static final float DEFAULT_FRICTION_Y = 1.0f;
        public static final float DEFAULT_FRICTION_Z = 1.0f;
        public static final float DEFAULT_FRICTION = 1.0f;
        public static final float DEFAULT_LINEAR_DAMPING = 0.5f;
        public static final float DEFAULT_ANGULAR_DAMPING = 0.5f;
        public static final float DEFAULT_RESTITUTION = 0.0f;

        private final String materialName;
        private Vector3f frictionAxes;
        private float friction;
        private float l_damping;
        private float a_damping;
        private float restitution;

        public MaterialProperties(String materialName) {
            this.materialName = materialName;
            this.frictionAxes = new Vector3f(MaterialProperties.DEFAULT_FRICTION_X, MaterialProperties.DEFAULT_FRICTION_Y, MaterialProperties.DEFAULT_FRICTION_Z);
            this.l_damping = MaterialProperties.DEFAULT_LINEAR_DAMPING;
            this.a_damping = MaterialProperties.DEFAULT_ANGULAR_DAMPING;
            this.friction = MaterialProperties.DEFAULT_FRICTION;
            this.restitution = MaterialProperties.DEFAULT_RESTITUTION;
        }

        public String getMaterialName() {
            return this.materialName;
        }

        public float getFriction() {
            return this.friction;
        }

        private MaterialProperties setFriction(float friction) {
            this.friction = friction;
            return this;
        }

        public float getRestitution() {
            return this.restitution;
        }

        private MaterialProperties setRestitution(float restitution) {
            this.restitution = restitution;
            return this;
        }

        public Vector3f getFrictionAxes() {
            return this.frictionAxes;
        }

        private MaterialProperties setFrictionAxes(Vector3f frictionAxes) {
            this.frictionAxes = frictionAxes;
            return this;
        }

        public float getA_damping() {
            return this.a_damping;
        }

        private MaterialProperties setA_damping(float a_damping) {
            this.a_damping = a_damping;
            return this;
        }

        public float getL_damping() {
            return this.l_damping;
        }

        private MaterialProperties setL_damping(float l_damping) {
            this.l_damping = l_damping;
            return this;
        }
    }
}
