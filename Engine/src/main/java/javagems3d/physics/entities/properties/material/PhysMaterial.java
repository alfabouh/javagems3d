package javagems3d.physics.entities.properties.material;

public record PhysMaterial(float friction, float l_damping, float a_damping, float m_density, float restitution) {

    public static final float NO_MASS = 0.0f;
    public static final float DEFAULT_FRICTION = 10.0f;
    public static final float DEFAULT_L_DAMPING = 0.5f;
    public static final float DEFAULT_A_DAMPING = 0.5f;
    public static final float DEFAULT_MASS_DENSITY = 10.0f;
    public static final float DEFAULT_RESTITUTION = 0.05f;

    public static PhysMaterial createDefaultMaterial() {
        return new PhysMaterial(PhysMaterial.DEFAULT_FRICTION, PhysMaterial.DEFAULT_L_DAMPING, PhysMaterial.DEFAULT_A_DAMPING, PhysMaterial.DEFAULT_MASS_DENSITY, PhysMaterial.DEFAULT_RESTITUTION);
    }

    public static final class MatList {
        public static final PhysMaterial groundMat = new PhysMaterial(100.0f, PhysMaterial.DEFAULT_L_DAMPING, PhysMaterial.DEFAULT_A_DAMPING, PhysMaterial.NO_MASS, PhysMaterial.DEFAULT_RESTITUTION);
    }
}
