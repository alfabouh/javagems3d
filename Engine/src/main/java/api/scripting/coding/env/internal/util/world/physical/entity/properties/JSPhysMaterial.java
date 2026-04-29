package api.scripting.coding.env.internal.util.world.physical.entity.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.physics.entities.properties.material.PhysMaterial;

@JSCodingClass(binding = "JSPhysMaterial", description = "Physical material defining friction, damping and density.")
public record JSPhysMaterial(float friction, float linearDamping, float angularDamping, float density) {

    @JSCodingConstructor(description = "Create material", paramNames = {"friction", "linearDamping", "angularDamping", "density"})
    public JSPhysMaterial(float friction, float linearDamping, float angularDamping, float density) {
        this.friction = friction;
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;
        this.density = density;
    }

    @JSCodingFunctionOrMethod(description = "Create default material")
    public static JSPhysMaterial createDefault() {
        PhysMaterial mat = PhysMaterial.createDefaultMaterial();
        return new JSPhysMaterial(mat.friction(), mat.l_damping(), mat.a_damping(), mat.m_density());
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public PhysMaterial getJavaMaterial() {
        return new PhysMaterial(this.friction, this.linearDamping, this.angularDamping, this.density);
    }
}