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

package api.scripting.coding.env.internal.util.world.render.world.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimationData;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSAnimatedObjectI;
import javagems3d.graphics.objects.IAnimated;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneAnimatedObject", description = "Wrapper for animated scene object.")
public class JSSceneAnimatedObject implements JSAnimatedObjectI {
    @JSHideFromDoc
    private final IAnimated animated;

    @JSCodingConstructor(description = "Wraps animated object.", paramNames = {"animated"})
    public JSSceneAnimatedObject(IAnimated animated) {
        this.animated = animated;
    }

    @Override
    public JSAnimationData getAnimationData() {
        return new JSAnimationData(this.animated.getAnimationData());
    }

    @Override
    public JSAnimationData setAnimationByID(int id) {
        return new JSAnimationData(this.animated.setAnimationByID(id));
    }

    @Override
    public void setAnimationData(@NotNull JSAnimationData animationData) {
        this.animated.setAnimationData(animationData.getJavaAnimationData());
    }

    @JSCodingFunctionOrMethod(description = "Returns animation speed multiplier.")
    public float getSpeedMultiplier() {
        return this.animated.animationSpeedMultiplier();
    }

    @Override
    @JSHideFromDoc
    public IAnimated getJavaAnimated() {
        return this.animated;
    }
}