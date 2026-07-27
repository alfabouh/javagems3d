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

package api.scripting.coding.env.internal.util.resources.instances.models.pose;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.system.resources.assets.models.pose.Pose3D;

@JSCodingClass(binding = "JSModelPose3D", description = "Represents a 3D pose with position, rotation, scale, and view orientation flag.")
public class JSModelPose3D {
    private final Pose3D pose;

    @JSCodingConstructor(description = "Create empty pose")
    public JSModelPose3D() {
        this.pose = new Pose3D();
    }

    @JSCodingConstructor(description = "Create pose from position", paramNames = {"position"})
    public JSModelPose3D(JSVector3f position) {
        this.pose = new Pose3D(position.getJavaVector3f());
    }

    @JSCodingConstructor(description = "Create pose from position and rotation", paramNames = {"position", "rotation"})
    public JSModelPose3D(JSVector3f position, JSVector3f rotation) {
        this.pose = new Pose3D(position.getJavaVector3f(), rotation.getJavaVector3f());
    }

    @JSCodingConstructor(description = "Create full pose", paramNames = {"position", "rotation", "scale"})
    public JSModelPose3D(JSVector3f position, JSVector3f rotation, JSVector3f scale) {
        this.pose = new Pose3D(position.getJavaVector3f(), rotation.getJavaVector3f(), scale.getJavaVector3f());
    }

    @JSCodingConstructor(description = "Wrap existing Pose3D", paramNames = {"pose"})
    public JSModelPose3D(Pose3D pose) {
        this.pose = pose;
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector3f getPosition() {
        return new JSVector3f(this.pose.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public JSVector3f getRotation() {
        return new JSVector3f(this.pose.getRotation());
    }

    @JSCodingFunctionOrMethod(description = "Get scale")
    public JSVector3f getScale() {
        return new JSVector3f(this.pose.getScaling());
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"position"})
    public JSModelPose3D setPosition(JSVector3f position) {
        this.pose.setPosition(position.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rotation"})
    public JSModelPose3D setRotation(JSVector3f rotation) {
        this.pose.setRotation(rotation.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set scale", paramNames = {"scale"})
    public JSModelPose3D setScale(JSVector3f scale) {
        this.pose.setScaling(scale.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Check if oriented to view matrix")
    public boolean isOrientedToView() {
        return this.pose.isOrientedToViewMatrix();
    }

    @JSCodingFunctionOrMethod(description = "Set oriented to view flag", paramNames = {"value"})
    public JSModelPose3D setOrientedToView(boolean value) {
        this.pose.setOrientedToView(value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy pose")
    public JSModelPose3D copy() {
        return new JSModelPose3D(this.pose.copy());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Pose3D")
    public Pose3D getJavaPose3D() {
        return this.pose;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "Pose3D[pos=" + this.pose.getPosition() +
                ", rot=" + this.pose.getRotation() +
                ", scale=" + this.pose.getScaling() + "]";
    }
}