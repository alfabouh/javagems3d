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
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimationData;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneEntityI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithModelI;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneEntity", description = "Wrapper for SceneEntity. Handles model, animation, rendering, and lights.")
public class JSSceneEntity implements JSSceneEntityI, JSSceneObjectWithModelI, JSSceneObjectWithLightsI {
    @JSHideFromDoc
    private final SceneEntity entity;

    @JSCodingConstructor(description = "Wrap existing SceneEntity")
    public JSSceneEntity(SceneEntity entity) {
        this.entity = entity;
    }

    @JSCodingConstructor(description = "Create SceneEntity from world, worldItem, and renderData")
    public JSSceneEntity(JSSceneWorld world, JSWorldItem worldItem, EntityRenderData renderData) {
        this.entity = new SceneEntity(world.getJavaSceneWorld(), worldItem.getJavaWorldObject(), renderData) {};
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneEntity")
    public SceneEntity getJavaSceneEntity() {
        return this.entity;
    }

    @JSCodingFunctionOrMethod(description = "Check if entity is under user control")
    public boolean isUnderUserControl() {
        return this.entity.isEntityUnderUserControl();
    }

    @JSCodingFunctionOrMethod(description = "Get associated world")
    public JSSceneWorld getWorld() {
        return new JSSceneWorld(this.entity.getWorld());
    }

    @JSCodingFunctionOrMethod(description = "Get associated WorldItem")
    public JSWorldItem getWorldItem() {
        return new JSWorldItem(this.entity.getWorldItem());
    }

    @JSCodingFunctionOrMethod(description = "Set visibility of the entity")
    public void setVisible(boolean visible) {
        this.entity.setVisible(visible);
    }

    @JSCodingFunctionOrMethod(description = "Mark entity as dead")
    public void setDead() {
        this.entity.setDead();
    }

    @JSCodingFunctionOrMethod(description = "Check if entity is dead")
    public boolean isDead() {
        return this.entity.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Get entity render position")
    public JSVector3f getRenderPosition() {
        return new JSVector3f(this.entity.getRenderPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get entity render rotation")
    public JSVector3f getRenderRotation() {
        return new JSVector3f(this.entity.getRenderRotation());
    }

    @JSCodingFunctionOrMethod(description = "Refresh interpolating state (position and rotation)")
    public void refreshInterpolatingState() {
        this.entity.refreshInterpolatingState();
    }

    @JSCodingFunctionOrMethod(description = "Update model translation according to entity position, rotation and scaling")
    public void updateModelTranslation() {
        this.entity.updateModelTranslation();
    }

    @JSCodingFunctionOrMethod(description = "Update entity render position using physics sync ticks")
    public void updateRenderPos(float physicsSyncTicks) {
        this.entity.updateRenderPos(physicsSyncTicks);
    }

    @Override
    public JSAnimationData getAnimationData() {
        return new JSAnimationData(this.entity.getAnimationData());
    }

    @JSCodingFunctionOrMethod(description = "Set animation by ID")
    public JSAnimationData setAnimationByID(int id) {
        AnimationData data = this.entity.setAnimationByID(id);
        return data != null ? new JSAnimationData(data) : null;
    }

    @Override
    public void setAnimationData(@NotNull JSAnimationData animationData) {
        this.entity.setAnimationData(animationData.getJavaAnimationData());
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying model")
    @Override
    public JSModel3D getModel() {
        return this.entity.hasModel() ? new JSModel3D(this.entity.getModel()) : null;
    }

    @JSCodingFunctionOrMethod(description = "Get java object")
    @Override
    public SceneObject getJavaSceneObject() {
        return this.entity;
    }

    @JSCodingFunctionOrMethod(description = "Get java object")
    @Override
    public IModeled getJavaModeledObject() {
        return this.entity;
    }

    @JSCodingFunctionOrMethod(description = "Get java object")
    @Override
    public IObjectWithLights getJavaLightedObject() {
        return this.entity;
    }

    @JSCodingFunctionOrMethod(description = "Check if entity can be rendered")
    public boolean canBeRendered() {
        return this.entity.canBeRendered();
    }

    @JSCodingFunctionOrMethod(description = "Get scaling vector of the entity")
    public JSVector3f getScaling() {
        return new JSVector3f(this.entity.getScaling());
    }


    @Override
    public IAnimated getJavaAnimated() {
        return null;
    }
}