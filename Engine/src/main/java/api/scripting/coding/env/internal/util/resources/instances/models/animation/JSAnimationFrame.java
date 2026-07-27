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

package api.scripting.coding.env.internal.util.resources.instances.models.animation;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import org.joml.Matrix4f;

import java.util.Arrays;

@JSCodingClass(binding = "JSAnimationFrame", description = "Wrapper for a single animation frame containing bone matrices and frame offset.")
public class JSAnimationFrame {
    @JSHideFromDoc
    private AnimationFrame animationFrame;

    @JSHideFromDoc
    public JSAnimationFrame(AnimationFrame animationFrame) {
        this.animationFrame = animationFrame;
    }

    @JSCodingFunctionOrMethod(description = "Get bone transformation matrices for this frame")
    public JSMatrix4f[] getBoneMatrices() {
        return Arrays.stream(this.animationFrame.getBoneMatrices()).map(JSMatrix4f::new).toArray(JSMatrix4f[]::new);
    }

    @JSCodingFunctionOrMethod(description = "Set bone transformation matrices for this frame", paramNames = {"boneMatrices"})
    public void setBoneMatrices(JSMatrix4f[] boneMatrices) {
        this.animationFrame.setBoneMatrices(Arrays.stream(boneMatrices).map(JSMatrix4f::getJavaMatrix4f).toArray(Matrix4f[]::new));
    }

    @JSCodingFunctionOrMethod(description = "Get frame offset")
    public int getOffset() {
        return this.animationFrame.getOffset();
    }

    @JSCodingFunctionOrMethod(description = "Set frame offset", paramNames = {"offset"})
    public void setOffset(int offset) {
        this.animationFrame.setOffset(offset);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java animation frame (unsafe)")
    @JSHideFromDoc
    public AnimationFrame getJavaAnimationFrame() {
        return this.animationFrame;
    }
}