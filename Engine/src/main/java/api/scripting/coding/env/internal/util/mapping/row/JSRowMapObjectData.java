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

package api.scripting.coding.env.internal.util.mapping.row;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.JSTagsContainer;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSRenderProperties;
import javagems3d.system.external.mapping.data.templates.RowMapObjectData;

@JSCodingClass(binding = "JSRowMapObjectData", description = "Wrapper for RowMapObjectData representing raw map object data.")
public class JSRowMapObjectData {

    @JSHideFromDoc
    private final RowMapObjectData data;

    @JSCodingConstructor(description = "Wrap existing RowMapObjectData", paramNames = {"data"})
    public JSRowMapObjectData(RowMapObjectData data) {
        this.data = data;
    }

    @JSCodingFunctionOrMethod(description = "Get object ID", paramNames = {})
    public int getId() {
        return this.data.getId();
    }

    @JSCodingFunctionOrMethod(description = "Get object unique string identifier", paramNames = {})
    public String getObjectId() {
        return this.data.getObjectNameId();
    }

    @JSCodingFunctionOrMethod(description = "Get object path", paramNames = {})
    public String getObjectPath() {
        return this.data.getObjectPath();
    }

    @JSCodingFunctionOrMethod(description = "Get object tags container", paramNames = {})
    public JSTagsContainer getTagsContainer() {
        return new JSTagsContainer(this.data.getTagsContainer());
    }

    //TODO
   // @JSCodingFunctionOrMethod(description = "Get object render properties", paramNames = {})
   // public JSRenderProperties getRenderProperties() {
   //     return new JSRenderProperties(this.data.getRenderProperties());
   // }

    @JSCodingFunctionOrMethod(description = "Get object position", paramNames = {})
    public JSVector3f getPosition() {
        return new JSVector3f(this.data.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get object rotation", paramNames = {})
    public JSVector3f getRotation() {
        return new JSVector3f(this.data.getRotation());
    }

    @JSCodingFunctionOrMethod(description = "Get object scaling", paramNames = {})
    public JSVector3f getScaling() {
        return new JSVector3f(this.data.getScaling());
    }

    @JSHideFromDoc
    public RowMapObjectData getJava() {
        return this.data;
    }
}