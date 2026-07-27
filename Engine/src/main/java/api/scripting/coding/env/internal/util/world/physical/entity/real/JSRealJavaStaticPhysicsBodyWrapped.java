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

package api.scripting.coding.env.internal.util.world.physical.entity.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSColliderConstructor;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSRealJavaStaticPhysicsBodyWrapped", description = "Base class for creating custom static physics bodies in Java with JS exposure.")
public abstract class JSRealJavaStaticPhysicsBodyWrapped extends JGemsStaticBody implements JSWorldItemI {

    @JSCodingConstructor(description = "Create static body with full transform", paramNames = {"colliderConstructor", "world", "position", "rotation", "scale", "itemName"})
    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(colliderConstructor.getJavaConstructor(), world.getJavaPhysicsWorld(), pos, rot, scale, itemName);
    }

    @JSCodingConstructor(description = "Create static body with position and rotation, default scale (1,1,1)", paramNames = {"colliderConstructor", "world", "position", "rotation", "itemName"})
    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(colliderConstructor, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    @JSCodingConstructor(description = "Create static body with position only, default rotation (0,0,0) and scale (1,1,1)", paramNames = {"colliderConstructor", "world", "position", "itemName"})
    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(colliderConstructor, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingConstructor(description = "Create static body with defaults: position (0,0,0), rotation (0,0,0), scale (1,1,1)", paramNames = {"colliderConstructor", "world", "itemName"})
    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, String itemName) {
        this(colliderConstructor, world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingFunctionOrMethod(description = "Called every tick (override in JS)", paramNames = {"world"})
    protected void onTick(JSPhysicsWorld world) {
    }

    @Override
    protected void onTick(IWorld iWorld) {
        super.onTick(iWorld);

        if (iWorld instanceof PhysicsWorld physicsWorld) {
            this.onTick(new JSPhysicsWorld(physicsWorld));
        }
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public JGemsStaticBody getJavaBody() {
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public WorldItem getJavaWorldObject() {
        return this;
    }
}