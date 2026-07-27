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

package api.scripting.coding.env.internal.util.world.render.table.fabrics;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSPipeline;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultIndirectRenderFabric;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.function.Consumer;

@JSCodingClass(binding = "JSIndirectRealRenderFabric", description = "Wrapper for DefaultIndirectRenderFabric. Allows JS scripts to override methods for advanced customization, " +
        "but indirect rendering is recommended to manage from Java due to its complexity.")
public abstract class JSIndirectRealRenderFabric extends JSIndirectRenderFabric {
    @JSCodingConstructor(description = "Wraps an existing DefaultIndirectRenderFabric instance", paramNames = {"fabric"})
    public JSIndirectRealRenderFabric(@NotNull DefaultIndirectRenderFabric fabric) {
        super(fabric);
    }

    @JSCodingFunctionOrMethod(description = "Fill a FloatBuffer with model matrices. Override to customize behavior.", paramNames = {"pipeline", "jsSceneObject", "defaultMatrix", "matrices", "metaDataHandler"})
    public void onFillBufferWithMatrices(JSPipeline pipeline, JSSceneObjectI jsSceneObject, JSMatrix4f defaultMatrix, FloatBuffer matrices, @Nullable Consumer<JSSceneObjectI> metaDataHandler) {
        this.fabric.onFillBufferWithMatrices(pipeline.getJavaPipeline(), jsSceneObject.getJavaSceneObject(), defaultMatrix.getJavaMatrix4f(), matrices, ArbitraryArguments.pass(metaDataHandler));
    }

    @JSCodingFunctionOrMethod(description = "Fill a ByteBuffer with object properties. Override to customize behavior.", paramNames = {"pipeline", "jsSceneObject", "defaultAttributes", "properties", "metaDataHandler"})
    public void onFillBufferWithProperties(JSPipeline pipeline, JSSceneObjectI jsSceneObject, JSRenderAttributes defaultAttributes, ByteBuffer properties, @Nullable Consumer<JSSceneObjectI> metaDataHandler) {
        this.fabric.onFillBufferWithProperties(
                pipeline.getJavaPipeline(),
                jsSceneObject.getJavaSceneObject(),
                defaultAttributes.getJavaRenderAttributes(),
                properties,
                ArbitraryArguments.pass(metaDataHandler)
        );
    }

    @JSCodingFunctionOrMethod(description = "Allocate necessary GPU resources for a scene object", paramNames = {"jsSceneObject"})
    public void createResources(JSSceneObjectI jsSceneObject) {
        this.fabric.createResources(jsSceneObject.getJavaSceneObject());
    }

    @JSCodingFunctionOrMethod(description = "Release GPU resources of a scene object", paramNames = {"jsSceneObject"})
    public void destroyResources(JSSceneObjectI jsSceneObject) {
        this.fabric.destroyResources(jsSceneObject.getJavaSceneObject());
    }
}