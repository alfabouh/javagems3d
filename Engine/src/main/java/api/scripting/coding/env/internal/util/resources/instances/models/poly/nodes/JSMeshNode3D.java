package api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.JSMaterial;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JSCodingClass(binding = "JSMeshNode3D", description = "...")
public class JSMeshNode3D implements JSMeshNodeI {
    @JSHideFromDoc private final MeshNode3D<?> meshNode3D;

    @JSHideFromDoc
    public JSMeshNode3D(MeshNode3D<?> meshNode3D) {
        this.meshNode3D = meshNode3D;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean hasTransparency() {
        return this.meshNode3D.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"material"})
    public void setMaterial(@NotNull JSMaterial material) {
        this.meshNode3D.setMaterial(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSMaterial getMaterial() {
        return new JSMaterial(this.meshNode3D.getMaterial());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clear() {
        this.meshNode3D.clear();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public MeshNode3D<?> getJavaMeshNode3D() {
        return this.meshNode3D;
    }

    @JSHideFromDoc
    @Override
    public MeshNode<?> meshNode() {
        return this.meshNode3D;
    }
}
