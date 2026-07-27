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
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.models.pose.Pose3D;

@JSCodingClass(binding = "JSModelPose2D", description = "Represents a 2D model pose with position, rotation, and scale.")
public class JSModelPose2D {
    private final Pose2D pose;

    @JSCodingConstructor(description = "Create empty pose")
    public JSModelPose2D() {
        this.pose = new Pose2D();
    }

    @JSCodingConstructor(description = "Create pose from position", paramNames = {"position"})
    public JSModelPose2D(JSVector2f position) {
        this.pose = new Pose2D(position.getJavaVector2f());
    }

    @JSCodingConstructor(description = "Create pose from position and rotation", paramNames = {"position", "rotation"})
    public JSModelPose2D(JSVector2f position, float rotation) {
        this.pose = new Pose2D(position.getJavaVector2f(), rotation);
    }

    @JSCodingConstructor(description = "Create full pose", paramNames = {"position", "rotation", "scale"})
    public JSModelPose2D(JSVector2f position, float rotation, JSVector2f scale) {
        this.pose = new Pose2D(position.getJavaVector2f(), rotation, scale.getJavaVector2f());
    }

    @JSCodingConstructor(description = "Wrap existing Pose2D", paramNames = {"pose"})
    public JSModelPose2D(Pose2D pose) {
        this.pose = pose;
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector2f getPosition() {
        return new JSVector2f(pose.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public float getRotation() {
        return pose.getRotation();
    }

    @JSCodingFunctionOrMethod(description = "Get scale")
    public JSVector2f getScale() {
        return new JSVector2f(pose.getScale());
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"position"})
    public JSModelPose2D setPosition(JSVector2f position) {
        pose.setPosition(position.getJavaVector2f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set rotation (radians)", paramNames = {"rotation"})
    public JSModelPose2D setRotation(float rotation) {
        pose.setRotation(rotation);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set scale", paramNames = {"scale"})
    public JSModelPose2D setScale(JSVector2f scale) {
        pose.setScale(scale.getJavaVector2f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy pose")
    public JSModelPose2D copy() {
        return new JSModelPose2D(this.pose.copy());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Pose2D")
    public Pose2D getJavaPose2D() {
        return this.pose;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "Pose2D[pos=" + pose.getPosition() + ", rot=" + pose.getRotation() + ", scale=" + pose.getScale() + "]";
    }
}