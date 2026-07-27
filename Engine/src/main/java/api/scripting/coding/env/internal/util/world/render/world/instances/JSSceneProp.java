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
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimationData;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import api.scripting.coding.env.internal.util.world.render.data.JSMeshStructureConstructor;
import api.scripting.coding.env.internal.util.world.render.data.JSPropRenderData;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithModelI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSScenePropI;
import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneProp", description = "Base wrapper for SceneProp, handling visibility, death state, and model access.")
public abstract class JSSceneProp implements JSScenePropI, JSSceneObjectWithModelI, JSSceneObjectWithLightsI {
    @JSHideFromDoc
    protected final SceneProp prop;

    @JSCodingConstructor(description = "Wrap existing SceneProp", paramNames = {"prop"})
    public JSSceneProp(SceneProp prop) {
        this.prop = prop;
    }

    @JSCodingConstructor(description = "Create SceneProp from name, world, and render data", paramNames = {"name", "world", "renderData"})
    public JSSceneProp(String name, JSSceneWorld world, JSPropRenderData renderData) {
        this.prop = new SceneWorldProp(name, world.getJavaSceneWorld(), renderData.getJavaPropRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneProp")
    public SceneProp getJavaSceneProp() {
        return this.prop;
    }

    @JSCodingFunctionOrMethod(description = "Get the name of the prop")
    public String getName() {
        return this.prop.getName();
    }

    @JSCodingFunctionOrMethod(description = "Set visibility of the prop", paramNames = {"visible"})
    public void setVisible(boolean visible) {
        this.prop.setVisible(visible);
    }

    @JSCodingFunctionOrMethod(description = "Check if prop is visible")
    public boolean isVisible() {
        return this.prop.canBeRendered();
    }

    @JSCodingFunctionOrMethod(description = "Mark prop as dead")
    public void setDead() {
        this.prop.setDead();
    }

    @JSCodingFunctionOrMethod(description = "Check if prop is dead")
    public boolean isDead() {
        return this.prop.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Get model of this prop")
    @Override
    public JSModel3D getModel() {
        return this.prop.hasModel() ? new JSModel3D(this.prop.getModel()) : null;
    }

    @JSCodingFunctionOrMethod(description = "Get model constructor of this prop, JSMeshStructureConstructor<Void>")
    public JSMeshStructureConstructor<Void> getPropModelConstructor() {
        IModelConstructor<Void, ? extends IMesh> javaConstructor = this.prop.getPropModelConstructor();
        if (javaConstructor == null) return null;
        return t -> (JSMeshStructure3D) () -> javaConstructor.constructMeshDataGroup(null);
    }

    @JSCodingFunctionOrMethod(description = "Get world of this prop")
    public JSSceneWorld getWorld() {
        return new JSSceneWorld((SceneWorld) this.prop.getWorld());
    }

    @Override
    public JSAnimationData getAnimationData() {
        return new JSAnimationData(this.prop.getAnimationData());
    }

    @JSCodingFunctionOrMethod(description = "Set animation by ID")
    public JSAnimationData setAnimationByID(int id) {
        AnimationData data = this.prop.setAnimationByID(id);
        return data != null ? new JSAnimationData(data) : null;
    }

    @Override
    public void setAnimationData(@NotNull JSAnimationData animationData) {
        this.prop.setAnimationData(animationData.getJavaAnimationData());
    }

    @JSHideFromDoc
    @Override
    public SceneObject getJavaSceneObject() {
        return this.prop;
    }

    @Override
    public IModeled getJavaModeledObject() {
        return this.prop;
    }

    @Override
    public IObjectWithLights getJavaLightedObject() {
        return this.prop;
    }

    @JSCodingFunctionOrMethod(description = "Check if prop can be rendered")
    public boolean canBeRendered() {
        return this.prop.canBeRendered();
    }
}