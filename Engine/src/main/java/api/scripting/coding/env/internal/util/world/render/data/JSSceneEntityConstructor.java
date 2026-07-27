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

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneEntity;
import javagems3d.graphics.objects.rendering.constructors.ISceneEntityConstructor;

@JSCodingClass(binding = "JSSceneEntityConstructor", description = "Functional interface for constructing SceneEntity instances from scripts")
@FunctionalInterface
public interface JSSceneEntityConstructor {
    @JSCodingFunctionOrMethod(description = "Create a SceneEntity from JSSceneWorld, JSWorldItem, and JSEntityRenderData", paramNames = {"world", "worldItem", "entityRenderData"})
    JSSceneEntity create(JSSceneWorld world, JSWorldItem worldItem, JSEntityRenderData entityRenderData);
    @JSHideFromDoc
    default ISceneEntityConstructor toJavaConstructor() {
        return (sceneWorld, worldItem, entityRenderData) -> this.create(new JSSceneWorld(sceneWorld), new JSWorldItem(worldItem), new JSEntityRenderData(entityRenderData)).getJavaSceneEntity();
    }
}