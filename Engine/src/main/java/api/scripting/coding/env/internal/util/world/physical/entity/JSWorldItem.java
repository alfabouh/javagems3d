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

package api.scripting.coding.env.internal.util.world.physical.entity;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import javagems3d.physics.world.basic.BasicWorldItem;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSWorldItem", description = "Wrapper for WorldItem objects.")
public class JSWorldItem implements JSWorldItemI {
    protected final WorldItem worldItem;

    @JSHideFromDoc
    public JSWorldItem(WorldItem worldItem) {
        this.worldItem = worldItem;
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with full transform", paramNames = {"world", "position", "rotation", "scaling", "itemName"})
    public JSWorldItem(JSPhysicsWorld world, @NotNull JSVector3f position, @NotNull JSVector3f rotation, @NotNull JSVector3f scaling, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), rotation.getJavaVector3f(), scaling.getJavaVector3f(), itemName);
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with position and rotation", paramNames = {"world", "position", "rotation", "itemName"})
    public JSWorldItem(JSPhysicsWorld world, JSVector3f position, JSVector3f rotation, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), rotation.getJavaVector3f(), itemName);
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with position only", paramNames = {"world", "position", "itemName"})
    public JSWorldItem(JSPhysicsWorld world, JSVector3f position, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), position.getJavaVector3f(), itemName);
    }

    @JSCodingConstructor(description = "Create a BasicWorldItem with world only", paramNames = {"world", "itemName"})
    public JSWorldItem(JSPhysicsWorld world, String itemName) {
        this.worldItem = new BasicWorldItem(world.getJavaPhysicsWorld(), itemName);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java WorldItem")
    @Override
    public WorldItem getJavaWorldObject() {
        return this.worldItem;
    }
}