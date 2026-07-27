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
import api.scripting.coding.env.internal.util.world.render.data.JSPropRenderData;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneWorldProp", description = "Wrapper for SceneWorldProp objects, representing props in the scene.")
public class JSSceneWorldProp extends JSSceneProp {
    @JSHideFromDoc
    public JSSceneWorldProp(SceneWorldProp sceneWorldProp) {
        super(sceneWorldProp);
    }

    @JSCodingConstructor(description = "Create a JSSceneWorldProp instance", paramNames = {"name", "sceneWorld", "propRenderData"})
    public JSSceneWorldProp(@NotNull String name, @NotNull JSSceneWorld sceneWorld, @NotNull JSPropRenderData propRenderData) {
        super(new SceneWorldProp(name, sceneWorld.getJavaSceneWorld(), propRenderData.getJavaPropRenderData()));
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java SceneWorldProp object (unsafe, internal use)")
    public SceneWorldProp getJavaSceneWorldProp() {
        return (SceneWorldProp) this.getJavaSceneProp();
    }

    @JSHideFromDoc
    @Override
    public IAnimated getJavaAnimated() {
        return this.prop;
    }
}