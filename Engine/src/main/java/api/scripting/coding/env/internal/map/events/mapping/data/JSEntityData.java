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

package api.scripting.coding.env.internal.map.events.mapping.data;

import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

@JSCodingClass(binding = "JSEntityData", description = "Wrapper for JGemsEntityData, representing entity model path and render data.")
public class JSEntityData {

    @JSHideFromDoc
    private final JGemsEntityData entityData;

    @JSCodingConstructor(description = "Wrap existing JGemsEntityData.", paramNames = {"entityData"})
    public JSEntityData(JGemsEntityData entityData) {
        this.entityData = entityData;
    }

    @JSCodingConstructor(description = "Create JSEntityData with path and render data.", paramNames = {"pathToModel", "entityRenderData"})
    public JSEntityData(JSPath pathToModel, JSEntityRenderData entityRenderData) {
        this.entityData = new JGemsEntityData(
            pathToModel != null ? new JGemsPathSource(pathToModel.getJavaPath(), ISource.Source.OUTSIDE_JAR) : null,
            entityRenderData.getJavaEntityRenderData()
        );
    }

    @JSCodingConstructor(description = "Create JSEntityData with only path.", paramNames = {"pathToModel"})
    public JSEntityData(JSPath pathToModel) {
        this.entityData = new JGemsEntityData(
            pathToModel != null ? new JGemsPathSource(pathToModel.getJavaPath(), ISource.Source.OUTSIDE_JAR) : null
        );
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java JGemsEntityData object.")
    @JSHideFromDoc
    public JGemsEntityData getJava() {
        return this.entityData;
    }

    @JSCodingFunctionOrMethod(description = "Get the entity model path.")
    public JSPath getPathToModel() {
        return this.entityData.pathToModel() != null ? new JSPath(this.entityData.pathToModel().getPath()) : null;
    }

    @JSCodingFunctionOrMethod(description = "Get the entity render data.")
    public JSEntityRenderData getEntityRenderData() {
        return this.entityData.entityRenderData() != null ? new JSEntityRenderData(this.entityData.entityRenderData()) : null;
    }
}