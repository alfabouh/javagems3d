package ru.jgems3d.engine.physics.entities.properties.material;

public class PhysMaterial {
    public static final float NO_MASS = 0.0f;

    public static final float DEFAULT_FRICTION = 10.0f;
    public static final float DEFAULT_L_DAMPING = 0.5f;
    public static final float DEFAULT_A_DAMPING = 0.5f;
    public static final float DEFAULT_MASS_DENSITY = 0.1f;

    public final float friction;
    public final float l_damping;
    public final float a_damping;
    public final float m_density;

    public PhysMaterial(float friction, float l_damping, float a_damping, float m_density) {
        this.friction = friction;
        this.l_damping = l_damping;
        this.a_damping = a_damping;
        this.m_density = m_density;
    }

    public static PhysMaterial createDefaultMaterial() {
        return new PhysMaterial(PhysMaterial.DEFAULT_FRICTION, PhysMaterial.DEFAULT_L_DAMPING, PhysMaterial.DEFAULT_A_DAMPING, PhysMaterial.DEFAULT_MASS_DENSITY);
    }

    public static final class MatList {
        public static final PhysMaterial groundMat = new PhysMaterial(100.0f, PhysMaterial.DEFAULT_L_DAMPING, PhysMaterial.DEFAULT_A_DAMPING, PhysMaterial.NO_MASS);
    }
}
