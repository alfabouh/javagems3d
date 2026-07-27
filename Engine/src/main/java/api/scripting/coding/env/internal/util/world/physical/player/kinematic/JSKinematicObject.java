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

package api.scripting.coding.env.internal.util.world.physical.player.kinematic;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSEntityState;
import javagems3d.physics.entities.kinematic.JGemsKinematicItem;
import javagems3d.physics.world.basic.IWorldObject;

@JSCodingClass(binding = "JSKinematicObject", description = "Wrapper for JGemsKinematicItem")
public class JSKinematicObject implements JSWorldObjectI {

    @JSCodingField(description = "Real kinematic item object")
    private final JGemsKinematicItem item;

    @JSHideFromDoc
    public JSKinematicObject(JGemsKinematicItem item) {
        this.item = item;
    }

    @JSCodingFunctionOrMethod(description = "Get entity state")
    public JSEntityState getEntityState() {
        return new JSEntityState(this.item.getEntityState());
    }

    @JSCodingFunctionOrMethod(description = "Set entity state", paramNames = {"state"})
    public void setEntityState(JSEntityState state) {
        this.item.setEntityState(state.getJavaState());
    }

    @JSCodingFunctionOrMethod(description = "Check if in water")
    public boolean isInWater() {
        return this.item.isInWater();
    }

    @JSCodingFunctionOrMethod(description = "Check if on ground")
    public boolean isOnGround() {
        return this.item.isOnGround();
    }

    @JSCodingFunctionOrMethod(description = "Get current position")
    public JSVector3f getPosition() {
        return new JSVector3f(this.item.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPosition(JSVector3f pos) {
        this.item.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get body velocity")
    public JSVector3f getBodyVelocity() {
        return new JSVector3f(this.item.getBodyVelocity());
    }

    @JSCodingFunctionOrMethod(description = "Set body velocity", paramNames = {"velocity"})
    public void setBodyVelocity(JSVector3f velocity) {
        this.item.setBodyVelocity(velocity.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Add velocity to body", paramNames = {"velocity"})
    public void addBodyVelocity(JSVector3f velocity) {
        this.item.addBodyVelocity(velocity.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get gravity")
    public float getGravity() {
        return this.item.getGravity();
    }

    @JSCodingFunctionOrMethod(description = "Set gravity", paramNames = {"gravity"})
    public void setGravity(float gravity) {
        this.item.setGravity(gravity);
    }

    @JSCodingFunctionOrMethod(description = "Get walk speed")
    public float getWalkSpeed() {
        return this.item.getWalkSpeed();
    }

    @JSCodingFunctionOrMethod(description = "Set walk speed", paramNames = {"speed"})
    public void setWalkSpeed(float speed) {
        this.item.setWalkSpeed(speed);
    }

    @JSCodingFunctionOrMethod(description = "Get jump height")
    public float getJumpHeight() {
        return this.item.getJumpHeight();
    }

    @JSCodingFunctionOrMethod(description = "Set jump height", paramNames = {"height"})
    public void setJumpHeight(float height) {
        this.item.setJumpHeight(height);
    }

    @JSCodingFunctionOrMethod(description = "Check if can jump")
    public boolean canJump() {
        return this.item.canJump();
    }

    @JSCodingFunctionOrMethod(description = "Perform jump")
    public void jump() {
        this.item.jump(this.item.getGravity(), this.item.getJumpHeight());
    }

    @JSCodingFunctionOrMethod(description = "Get linear velocity damping")
    public float getLinearVelDamping() {
        return this.item.getLinearVelDamping();
    }

    @JSCodingFunctionOrMethod(description = "Set linear velocity damping", paramNames = {"damping"})
    public void setLinearVelDamping(float damping) {
        this.item.setLinearVelDamping(damping);
    }

    @JSCodingFunctionOrMethod(description = "Get slope angle")
    public double getSlopeAngle() {
        return this.item.getSlopeAngle();
    }

    @JSCodingFunctionOrMethod(description = "Set slope angle", paramNames = {"angle"})
    public void setSlopeAngle(double angle) {
        this.item.setSlopeAngle(angle);
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public JGemsKinematicItem getJavaItem() {
        return this.item;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public IWorldObject getJavaWorldObject() {
        return this.item;
    }
}