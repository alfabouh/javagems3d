/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.scripting.coding.env.internal.util.world.render.table.fabrics;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultDirectRenderFabric;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSDirectRenderFabric", description = "Wrapper for DirectRenderFabric. Allows scripts to implement custom render factories and manage resources.")
public class JSDirectRenderFabric implements JSRenderFabricI {
    @JSCodingField(description = "Underlying Java DirectRenderFabric object")
    protected final DirectRenderFabric fabric;

    @JSCodingConstructor(description = "Constructs the JS wrapper for an existing DirectRenderFabric instance", paramNames = {"fabric"})
    public JSDirectRenderFabric(@NotNull DirectRenderFabric fabric) {
        this.fabric = fabric;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java DirectRenderFabric", paramNames = {})
    public DirectRenderFabric getJavaFabric() {
        return this.fabric;
    }

    @JSCodingFunctionOrMethod(description = "Allocate necessary GPU resources for a scene object", paramNames = {"jsSceneObject"})
    public void createResources(JSSceneObjectI jsSceneObject) {
        this.fabric.createResources(jsSceneObject.getJavaSceneObject());
    }

    @JSCodingFunctionOrMethod(description = "Release GPU resources of a scene object", paramNames = {"jsSceneObject"})
    public void destroyResources(JSSceneObjectI jsSceneObject) {
        this.fabric.destroyResources(jsSceneObject.getJavaSceneObject());
    }

    //TODO
    //@JSCodingFunctionOrMethod(description = "Render all meshes of a 3D model at a given layer using the specified shader and renderer", paramNames = {"jsRenderer", "jsShader", "jsModel", "layer"})
    //public void renderMeshList3D(JSOpenGLRenderer jsRenderer, JSShader jsShader, JSModel3D jsModel, float discardAlphaLevel, int layer) {
    //    for (MeshNode3D<RenderMesh> meshNode3D : DefaultDirectRenderFabric.getNodes(jsModel.getJavaModel3D().<MeshStructure3D<RenderMesh>>getMeshStructureCast().getNodes(layer), jsModel.getJavaModel3D().getPose(), jsRenderer.getJavaRenderer(), (MeshGroup) jsModel.getMesh().getJavaMeshStructure3D())) {
    //        JGemsHelper.render().performDefaultModelMaterialOnShader(jsRenderer.getJavaRenderer().getWorld().getEnvironment(), jsShader.getJavaShaderManager(), meshNode3D.getMaterial(), discardAlphaLevel);
    //        JGemsHelper.render().renderMeshNode(meshNode3D.getMeshData());
    //    }
    //}

    @JSHideFromDoc
    @Override
    public IRenderFabric getJavaRenderFabric() {
        return this.fabric;
    }
}