package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode2D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;
import javagems3d.system.resources.assets.models.mesh.structures.flat.MeshGui;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;

import java.util.List;

@JSCodingClass(binding = "JSMesh2D", description = "2D mesh wrapper over MeshGui")
public class JSMesh2D implements JSCanBeCachedInMemory, JSMeshStructure2D {
    @JSHideFromDoc
    private final MeshGui meshGui;

    @JSCodingConstructor(description = "Create empty mesh")
    public JSMesh2D() {
        this.meshGui = new MeshGui();
    }

    @JSCodingConstructor(description = "Create mesh from render mesh", paramNames = {"renderMesh"})
    public JSMesh2D(RenderMesh renderMesh) {
        this.meshGui = new MeshGui(renderMesh);
    }

    @JSCodingConstructor(description = "Wrap existing MeshGui", paramNames = {"mesh"})
    public JSMesh2D(MeshGui mesh) {
        this.meshGui = mesh;
    }

    @JSCodingFunctionOrMethod(description = "Clear mesh")
    public JSMesh2D clear() {
        this.meshGui.clear();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying MeshGui")
    public MeshGui getJavaMeshGui() {
        return this.meshGui;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSMeshNode2D> getNodes() {
        return this.meshGui.getNodes().stream().map(JSMeshNode2D::new).toList();
    }

    @JSHideFromDoc
    @Override
    public MeshStructure2D getJavaMeshStructure2D() {
        return this.meshGui;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "JSMesh2D[" + meshGui + "]";
    }
}