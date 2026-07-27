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

package javagems3d.graphics.objects.rendering.pipeline.fabric.scene;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.GroupedSceneObjectsIndirectRenderer;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.args.ArbitraryArguments;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DefaultIndirectRenderFabric extends IndirectRenderFabric {
    public DefaultIndirectRenderFabric(Stage stage, GroupedSceneObjectsIndirectRenderer.IRenderingFunction renderingFunction) {
        super(stage, renderingFunction);
    }

    @Override
    public void onFillBufferWithMatrices(Pipeline pipeline, IRendered renderedItem, Matrix4f defaultMatrix, FloatBuffer matrices, ArbitraryArguments metaData) {
        matrices.put(defaultMatrix.get(new float[16]));
    }

    @Override
    public void onFillBufferWithProperties(Pipeline pipeline, IRendered renderedItem, RenderAttributes defaultAttributes, ByteBuffer properties, ArbitraryArguments metaData) {
        properties.putFloat(defaultAttributes.getProperties().getFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, JGemsConfig.SYSTEM.MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT));
        properties.putInt(defaultAttributes.getProperties().getInt(JGemsRenderProperties.KEY_GBUFFER_DECAL_LAYER_ID, 0));
    }

    @Override
    public void createResources(IRendered renderedItem) {

    }

    @Override
    public void destroyResources(IRendered renderedItem) {
    }
}