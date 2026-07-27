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

package api.scripting.coding.env.internal.util.resources.instances.models;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.misc.JSRequiresClearResources;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshBuffer;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshGroup;
import api.scripting.coding.env.internal.util.resources.instances.models.pose.JSModelPose3D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

@JSCodingClass(binding = "JSModel3D", description = "3D model with pose and mesh structure, supporting copy and mesh retrieval. " +
        "If a model is created every frame, call clear() after use to free resources.")
public class JSModel3D implements JSRequiresClearResources {
    @JSHideFromDoc
    private final Model3D model3D;

    @JSCodingConstructor(description = "Create model", paramNames = {"pose", "mesh"})
    public JSModel3D(JSModelPose3D pose, JSMeshStructure3D mesh) {
        this.model3D = new Model3D(pose.getJavaPose3D(), mesh != null ? mesh.getJavaMeshStructure3D() : null);
    }

    @JSCodingConstructor(description = "Copy model", paramNames = {"model"})
    public JSModel3D(JSModel3D model) {
        this.model3D = new Model3D(model.model3D);
    }

    @JSCodingConstructor(description = "Copy model with new pose", paramNames = {"model", "pose"})
    public JSModel3D(JSModel3D model, JSModelPose3D pose) {
        this.model3D = new Model3D(model.model3D, pose.getJavaPose3D());
    }

    @JSCodingConstructor(description = "Wrap existing model", paramNames = {"model"})
    public JSModel3D(Model3D model) {
        this.model3D = model;
    }

    @JSCodingFunctionOrMethod(description = "Get pose")
    public JSModelPose3D getPose() {
        return new JSModelPose3D(this.model3D.getPose());
    }

    @JSCodingFunctionOrMethod(description = "Get mesh structure")
    public JSMeshStructure3D getMesh() {
        MeshStructure3D<? extends IMesh> mesh = this.model3D.getMeshStructure();
        if (mesh instanceof MeshBuffer mb) return new JSMeshBuffer(mb);
        if (mesh instanceof MeshGroup mg) return new JSMeshGroup(mg);
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Copy model")
    public JSModel3D copy() {
        return new JSModel3D(this);
    }

    @JSCodingFunctionOrMethod(description = "Clear model resources. Use this if the model is created each frame.")
    public void clear() {
        this.model3D.clear();
    }

    @JSHideFromDoc
    public Model3D getJavaModel3D() {
        return this.model3D;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "Model3D[pose=" + this.model3D.getPose() +
                ", mesh=" + this.model3D.getMeshStructure() + "]";
    }
}