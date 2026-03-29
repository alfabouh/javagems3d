package api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.JSMaterial;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSMeshNode2D", description = "Wrapper for MeshNode2D providing 2D mesh operations.")
public class JSMeshNode2D implements JSMeshNodeI {
    @JSHideFromDoc
    private final MeshNode2D meshNode2D;

    @JSHideFromDoc
    public JSMeshNode2D(MeshNode2D meshNode2D) {
        this.meshNode2D = meshNode2D;
    }

    @JSCodingFunctionOrMethod(description = "Clear mesh node data")
    public void clear() {
        this.meshNode2D.clear();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java MeshNode2D (unsafe)")
    @JSHideFromDoc
    public MeshNode2D getJavaMeshNode2D() {
        return this.meshNode2D;
    }

    @JSHideFromDoc
    @Override
    public MeshNode<?> meshNode() {
        return this.meshNode2D;
    }
}