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
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSPhysicalDynamicBody", description = "Dynamic physics body with full runtime control over movement and forces.")
public class JSPhysicalDynamicBody implements JSWorldItemI {
    @JSCodingField(description = "Underlying dynamic body (Java side)")
    private final JGemsDynamicBody dynamicBody;

    @JSHideFromDoc
    public JSPhysicalDynamicBody(JGemsDynamicBody dynamicBody) {
        this.dynamicBody = dynamicBody;
    }

    @JSCodingConstructor(description = "Create dynamic body with full parameters", paramNames = {"colliderConstructor", "world", "pos", "rot", "scale", "itemName"})
    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        this.dynamicBody = new JGemsDynamicBody(colliderConstructor.getJavaConstructor(), world.getJavaPhysicsWorld(), pos, rot, scale, itemName);
    }

    @JSCodingConstructor(description = "Create dynamic body with position and rotation", paramNames = {"colliderConstructor", "world", "pos", "rot", "itemName"})
    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(colliderConstructor, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    @JSCodingConstructor(description = "Create dynamic body with position only", paramNames = {"colliderConstructor", "world", "pos", "itemName"})
    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(colliderConstructor, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingConstructor(description = "Create dynamic body with default position and rotation", paramNames = {"colliderConstructor", "world", "itemName"})
    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, String itemName) {
        this(colliderConstructor, world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingFunctionOrMethod(description = "Get position", paramNames = {})
    public JSVector3f getPosition() {
        Vector3f v = dynamicBody.getPosition();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPosition(JSVector3f pos) {
        dynamicBody.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation", paramNames = {})
    public JSVector3f getRotation() {
        Vector3f v = dynamicBody.getRotation();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rot"})
    public void setRotation(JSVector3f rot) {
        dynamicBody.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling", paramNames = {})
    public JSVector3f getScaling() {
        Vector3f v = dynamicBody.getScaling();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling", paramNames = {"scale"})
    public void setScaling(JSVector3f scale) {
        dynamicBody.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get linear velocity", paramNames = {})
    public JSVector3f getVelocity() {
        com.jme3.math.Vector3f v = dynamicBody.getPhysicsRigidBody().getLinearVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set linear velocity", paramNames = {"vel"})
    public void setVelocity(JSVector3f vel) {
        dynamicBody.getPhysicsRigidBody().setLinearVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Add linear velocity", paramNames = {"vel"})
    public void addVelocity(JSVector3f vel) {
        dynamicBody.getPhysicsRigidBody().addLinearVelocity(vel.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Apply impulse instantly", paramNames = {"impulse"})
    public void applyImpulse(JSVector3f impulse) {
        dynamicBody.getPhysicsRigidBody().applyCentralImpulse(DynamicsUtils.convertV3F_JME(impulse.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply continuous force", paramNames = {"force"})
    public void applyForce(JSVector3f force) {
        dynamicBody.getPhysicsRigidBody().applyCentralForce(DynamicsUtils.convertV3F_JME(force.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Set physics material", paramNames = {"material"})
    public void setMaterial(JSPhysMaterial material) {
        dynamicBody.setMaterial(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Set collision group", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) arr[i] = types[i].getJavaType();
        dynamicBody.setCollisionGroup(arr);
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) arr[i] = types[i].getJavaType();
        dynamicBody.setCollideWithGroups(arr);
    }

    @JSCodingFunctionOrMethod(description = "Get entity state", paramNames = {})
    public JSEntityState getState() {
        return new JSEntityState(dynamicBody.getEntityState());
    }

    @JSCodingFunctionOrMethod(description = "Destroy body", paramNames = {})
    public void destroy() {
        dynamicBody.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Get item name", paramNames = {})
    public String getName() {
        return dynamicBody.getItemName();
    }

    @JSCodingFunctionOrMethod(description = "Get item id", paramNames = {})
    public int getId() {
        return dynamicBody.getItemId();
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)", paramNames = {})
    @JSHideFromDoc
    public JGemsDynamicBody getJavaDynamicBody() {
        return dynamicBody;
    }

    @JSCodingFunctionOrMethod(description = "Real java object", paramNames = {})
    @Override
    public WorldItem getJavaWorldObject() {
        return dynamicBody;
    }
}