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
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.misc.JSRequiresClearResources;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure2D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMesh2D;
import api.scripting.coding.env.internal.util.resources.instances.models.pose.JSModelPose2D;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;
import javagems3d.system.resources.assets.models.mesh.structures.flat.MeshGui;

@JSCodingClass(binding = "JSModel2D", description = "2D model with pose and mesh, supporting transformations and copying. " +
        "If a model is created every frame, call clear() after use to free resources.")
public class JSModel2D implements JSRequiresClearResources {
    @JSHideFromDoc private final Model2D model2D;

    @JSCodingConstructor(description = "Create model", paramNames = {"pose", "mesh"})
    public JSModel2D(JSModelPose2D pose, JSMeshStructure2D mesh) {
        this.model2D = new Model2D(pose.getJavaPose2D(), mesh.getJavaMeshStructure2D());
    }

    @JSCodingConstructor(description = "Copy model", paramNames = {"model"})
    public JSModel2D(JSModel2D model) {
        this.model2D = new Model2D(model.model2D);
    }

    @JSCodingConstructor(description = "Copy model with new pose", paramNames = {"model", "pose"})
    public JSModel2D(JSModel2D model, JSModelPose2D pose) {
        this.model2D = new Model2D(model.model2D, pose.getJavaPose2D());
    }

    @JSCodingConstructor(description = "Wrap existing model", paramNames = {"model"})
    public JSModel2D(Model2D model) {
        this.model2D = model;
    }

    @JSCodingFunctionOrMethod(description = "Get pose")
    public JSModelPose2D getPose() {
        return new JSModelPose2D(this.model2D.getPose());
    }

    @JSCodingFunctionOrMethod(description = "Get mesh")
    public JSMeshStructure2D getMesh() {
        MeshStructure2D mesh = this.model2D.getMeshStructure();
        if (mesh instanceof MeshGui gui) {
            return new JSMesh2D(gui);
        }
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Translate model", paramNames = {"delta"})
    public JSModel2D translate(JSVector2f delta) {
        this.model2D.getPose().getPosition().set(delta.getJavaVector2f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Rotate model", paramNames = {"angle"})
    public JSModel2D rotate(float angle) {
        this.model2D.getPose().setRotation(angle);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Scale model", paramNames = {"scale"})
    public JSModel2D scale(JSVector2f scale) {
        this.model2D.getPose().getScale().set(scale.getJavaVector2f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy model")
    public JSModel2D copy() {
        return new JSModel2D(this);
    }

    @JSCodingFunctionOrMethod(description = "Clear model resources. Use this if the model is created each frame.")
    public void clear() {
        this.model2D.clear();
    }

    @JSHideFromDoc
    public Model2D getJavaModel2D() {
        return this.model2D;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "Model2D[pose=" + this.model2D.getPose() +
                ", mesh=" + this.model2D.getMeshStructure() + "]";
    }
}