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

package api.scripting.coding.env.internal.util.world.physical.entity.instances;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSColliderConstructor;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSCollisionType;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSEntityState;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSPhysMaterial;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSPhysicalStaticBody", description = "Static physics body with full runtime control.")
public class JSPhysicalStaticBody implements JSWorldItemI {
    @JSCodingField(description = "Underlying static body (Java side)")
    private final JGemsStaticBody staticBody;

    @JSHideFromDoc
    public JSPhysicalStaticBody(JGemsStaticBody staticBody) {
        this.staticBody = staticBody;
    }

    @JSCodingConstructor(description = "Create static body with collider, world, position, rotation, scale, and name", paramNames = {"colliderConstructor", "world", "pos", "rot", "scale", "itemName"})
    public JSPhysicalStaticBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        this.staticBody = new JGemsStaticBody(colliderConstructor.getJavaConstructor(), world.getJavaPhysicsWorld(), pos, rot, scale, itemName);
    }

    @JSCodingConstructor(description = "Create static body with collider, world, position, rotation, and name (default scale)", paramNames = {"colliderConstructor", "world", "pos", "rot", "itemName"})
    public JSPhysicalStaticBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(colliderConstructor, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    @JSCodingConstructor(description = "Create static body with collider, world, position, and name (default rotation and scale)", paramNames = {"colliderConstructor", "world", "pos", "itemName"})
    public JSPhysicalStaticBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(colliderConstructor, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingConstructor(description = "Create static body with collider, world, and name (default position, rotation and scale)", paramNames = {"colliderConstructor", "world", "itemName"})
    public JSPhysicalStaticBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, String itemName) {
        this(colliderConstructor, world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector3f getPosition() {
        Vector3f v = this.staticBody.getPosition();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPosition(JSVector3f pos) {
        this.staticBody.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public JSVector3f getRotation() {
        Vector3f v = this.staticBody.getRotation();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rot"})
    public void setRotation(JSVector3f rot) {
        this.staticBody.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling")
    public JSVector3f getScaling() {
        Vector3f v = this.staticBody.getScaling();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling", paramNames = {"scale"})
    public void setScaling(JSVector3f scale) {
        this.staticBody.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Set physics material", paramNames = {"material"})
    public void setMaterial(JSPhysMaterial material) {
        this.staticBody.setMaterial(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Set collision group", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) arr[i] = types[i].getJavaType();
        this.staticBody.setCollisionGroup(arr);
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) arr[i] = types[i].getJavaType();
        this.staticBody.setCollideWithGroups(arr);
    }

    @JSCodingFunctionOrMethod(description = "Get entity state")
    public JSEntityState getState() {
        return new JSEntityState(this.staticBody.getEntityState());
    }

    @JSCodingFunctionOrMethod(description = "Destroy body")
    public void destroy() {
        this.staticBody.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Get name")
    public String getName() {
        return this.staticBody.getItemName();
    }

    @JSCodingFunctionOrMethod(description = "Get id")
    public int getId() {
        return this.staticBody.getItemId();
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public JGemsStaticBody getJavaStaticBody() {
        return this.staticBody;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public WorldItem getJavaWorldObject() {
        return this.staticBody;
    }
}