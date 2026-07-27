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

package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneProp;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneWorldProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSPropRenderData", description = "Wrapper for PropRenderData, handling mesh, model constructor, and render attributes.")
public class JSPropRenderData {
    @JSCodingField(description = "DEFAULT_OBJECT_CONSTRUCTOR")
    public static JSScenePropConstructor DEFAULT_OBJECT_CONSTRUCTOR = (name, world, renderData) -> new JSSceneWorldProp(new SceneWorldProp(name, world.getJavaSceneWorld(), renderData.getJavaPropRenderData())) {};

    @JSHideFromDoc
    private final PropRenderData propRenderData;

    @JSCodingConstructor(description = "Wrap existing PropRenderData", paramNames = {"propRenderData"})
    public JSPropRenderData(PropRenderData propRenderData) {
        this.propRenderData = propRenderData;
    }

    @JSCodingConstructor(description = "Create PropRenderData with optional RenderAttributes", paramNames = {"renderAttributes"})
    public JSPropRenderData(JSRenderAttributes renderAttributes) {
        this.propRenderData = new PropRenderData(renderAttributes != null ? renderAttributes.getJavaRenderAttributes() : null);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java PropRenderData")
    public PropRenderData getJavaPropRenderData() {
        return this.propRenderData;
    }

    @JSCodingFunctionOrMethod(description = "Set RenderAttributes for this prop", paramNames = {"renderAttributes"})
    public JSPropRenderData setObjectRenderSettings(JSRenderAttributes renderAttributes) {
        this.propRenderData.setObjectRenderSettings(renderAttributes != null ? renderAttributes.getJavaRenderAttributes() : null);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set mesh structure for this prop", paramNames = {"meshStructure"})
    public JSPropRenderData setMeshStructure(JSMeshStructure3D meshStructure) {
        this.propRenderData.setMeshStructure(
                meshStructure != null ? meshStructure.getJavaMeshStructure3D() : null
        );
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set model constructor for this prop (JSMeshStructureConstructor<Void>)", paramNames = {"modelConstructor"})
    public JSPropRenderData setPropModelConstructor(JSMeshStructureConstructor<Void> modelConstructor) {
        if (modelConstructor != null) {
            this.propRenderData.setPropModelConstructor(modelConstructor.toJavaConstructor());
        }
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get mesh structure of this prop")
    public JSMeshStructure3D getMeshStructure() {
        MeshStructure3D<?> mesh = this.propRenderData.getMeshDataGroup();
        if (mesh == null) {
            return null;
        }
        return () -> mesh;
    }

    @JSCodingFunctionOrMethod(description = "Get model constructor of this prop (JSMeshStructureConstructor<Void>)")
    public JSMeshStructureConstructor<Void> getPropModelConstructor() {
        IModelConstructor<Void, ? extends IMesh> constructor = this.propRenderData.getPropModelConstructor();
        if (constructor == null) {
            return null;
        }
        return t -> () -> constructor.constructMeshDataGroup(null);
    }

    @JSCodingFunctionOrMethod(description = "Get render attributes of this prop")
    public JSRenderAttributes getObjectRenderAttributes() {
        RenderAttributes attrs = this.propRenderData.getObjectRenderAttributes();
        return attrs != null ? new JSRenderAttributes(attrs) : null;
    }

    @JSHideFromDoc
    public JSPropRenderData copyObject() {
        return new JSPropRenderData(this.propRenderData.copyObject());
    }
}