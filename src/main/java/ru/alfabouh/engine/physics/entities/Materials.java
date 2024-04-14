package ru.alfabouh.engine.physics.entities;

import org.joml.Vector3d;

@SuppressWarnings("all")
public class Materials {
    public static MaterialProperties defaultMaterial = new MaterialProperties("defaultMaterial");
    public static MaterialProperties grassGround = new MaterialProperties("grassGround").setFriction(5.0d).setRestitution(0.0d);
    public static MaterialProperties brickCube = new MaterialProperties("brickCube").setFriction(7.5d).setRestitution(0.0d);
    public static MaterialProperties liquid = new MaterialProperties("liquid").setFriction(0.0d).setRestitution(0.5d);
    public static MaterialProperties particle = new MaterialProperties("particle").setFriction(1.0d).setRestitution(0.1d);

    public static class MaterialProperties {
        public static final double DEFAULT_FRICTION_X = 1.0d;
        public static final double DEFAULT_FRICTION_Y = 1.0d;
        public static final double DEFAULT_FRICTION_Z = 1.0d;
        public static final double DEFAULT_FRICTION = 1.0d;
        public static final double DEFAULT_LINEAR_DAMPING = 0.5d;
        public static final double DEFAULT_ANGULAR_DAMPING = 0.5d;
        public static final double DEFAULT_RESTITUTION = 0.0d;

        private final String materialName;
        private Vector3d frictionAxes;
        private double friction;
        private double l_damping;
        private double a_damping;
        private double restitution;

        public MaterialProperties(String materialName) {
            this.materialName = materialName;
            this.frictionAxes = new Vector3d(MaterialProperties.DEFAULT_FRICTION_X, MaterialProperties.DEFAULT_FRICTION_Y, MaterialProperties.DEFAULT_FRICTION_Z);
            this.l_damping = MaterialProperties.DEFAULT_LINEAR_DAMPING;
            this.a_damping = MaterialProperties.DEFAULT_ANGULAR_DAMPING;
            this.friction = MaterialProperties.DEFAULT_FRICTION;
            this.restitution = MaterialProperties.DEFAULT_RESTITUTION;
        }

        public String getMaterialName() {
            return this.materialName;
        }

        public double getFriction() {
            return this.friction;
        }

        private MaterialProperties setFriction(double friction) {
            this.friction = friction;
            return this;
        }

        public double getRestitution() {
            return this.restitution;
        }

        private MaterialProperties setRestitution(double restitution) {
            this.restitution = restitution;
            return this;
        }

        public Vector3d getFrictionAxes() {
            return this.frictionAxes;
        }

        private MaterialProperties setFrictionAxes(Vector3d frictionAxes) {
            this.frictionAxes = frictionAxes;
            return this;
        }

        public double getA_damping() {
            return this.a_damping;
        }

        private MaterialProperties setA_damping(double a_damping) {
            this.a_damping = a_damping;
            return this;
        }

        public double getL_damping() {
            return this.l_damping;
        }

        private MaterialProperties setL_damping(double l_damping) {
            this.l_damping = l_damping;
            return this;
        }
    }
}
