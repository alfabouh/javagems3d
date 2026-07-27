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
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.objects.entities.world.SceneWorldEntity;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneWorldEntity", description = "Wrapper for SceneWorldEntity objects, representing entities in the scene.")
public class JSSceneWorldEntity extends JSSceneEntity {

    @JSCodingConstructor(description = "Create a JSSceneWorldEntity instance", paramNames = {"sceneWorld", "worldItem", "renderData"})
    public JSSceneWorldEntity(@NotNull JSSceneWorld sceneWorld, @NotNull JSWorldItem worldItem, @NotNull JSEntityRenderData renderData) {
        super(new SceneWorldEntity(sceneWorld.getJavaSceneWorld(), worldItem.getJavaWorldObject(), renderData.getJavaEntityRenderData()));
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java SceneWorldEntity object (unsafe, internal use)")
    public SceneWorldEntity getJavaSceneWorldEntity() {
        return (SceneWorldEntity) this.getJavaSceneEntity();
    }
}