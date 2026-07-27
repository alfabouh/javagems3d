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

package api.scripting.coding.env.internal.util.world.render.processing;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.render.table.JSRenderTable;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSRenderAttributes", description = "Holds the rendering configuration for a scene object, combining the render table and render properties. Allows scripts to customize rendering attributes.")
public class JSRenderAttributes {
    @JSCodingField(description = "The underlying Java RenderAttributes object")
    protected final RenderAttributes renderAttributes;

    @JSCodingConstructor(description = "Constructs the JS wrapper for RenderAttributes", paramNames = {"renderAttributes"})
    public JSRenderAttributes(@NotNull RenderAttributes renderAttributes) {
        this.renderAttributes = renderAttributes;
    }

    //TODO
   // @JSCodingConstructor(description = "Constructs JSRenderAttributes from JS wrappers")
   // public JSRenderAttributes(@NotNull JSRenderTable renderTableWrapper, @NotNull JSRenderProperties renderPropertiesWrapper) {
   //     this.renderAttributes = new RenderAttributes(renderTableWrapper.getJavaTable(), renderPropertiesWrapper.getJavaProperties());
   // }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java RenderAttributes")
    public RenderAttributes getJavaRenderAttributes() {
        return this.renderAttributes;
    }

    @JSCodingFunctionOrMethod(description = "Gets the RenderTable associated with this RenderAttributes")
    public JSRenderTable getRenderTable() {
        return new JSRenderTable(this.renderAttributes.getRenderTable());
    }

    @JSCodingFunctionOrMethod(description = "Sets the RenderTable for this RenderAttributes", paramNames = {"renderTable"})
    public JSRenderAttributes setRenderTable(@NotNull JSRenderTable renderTable) {
        this.renderAttributes.setRenderTable(renderTable.getJavaTable());
        return this;
    }

    //TODO
   //@JSCodingFunctionOrMethod(description = "Gets the RenderProperties associated with this RenderAttributes")
   //public JSRenderProperties getRenderProperties() {
   //    return new JSRenderProperties((JGemsRenderProperties) this.renderAttributes.getProperties());
   //}

    //TODO
   // @JSCodingFunctionOrMethod(description = "Sets the RenderProperties for this RenderAttributes", paramNames = {"renderProperties"})
   // public JSRenderAttributes setRenderProperties(@NotNull JSRenderProperties renderProperties) {
   //     this.renderAttributes.setRenderProperties(renderProperties.getJavaProperties());
   //     return this;
   // }

    @JSCodingFunctionOrMethod(description = "Creates a copy of this JSRenderAttributes")
    public JSRenderAttributes copy() {
        return new JSRenderAttributes(this.renderAttributes.copy());
    }

    @JSCodingFunctionOrMethod(description = "Gets the culling rules from RenderProperties")
    public CullingRules getCullingRules() {
        return this.renderAttributes.getCullingRules();
    }

    @JSCodingFunctionOrMethod(description = "DefaultPhysTest direct RenderAttributes")
    public static JSRenderAttributes getDefaultDirect() {
        return new JSRenderAttributes(RenderAttributes.getDefaultDirect());
    }

    @JSCodingFunctionOrMethod(description = "DefaultPhysTest indirect RenderAttributes")
    public static JSRenderAttributes getDefaultIndirect() {
        return new JSRenderAttributes(RenderAttributes.getDefaultIndirect());
    }
}