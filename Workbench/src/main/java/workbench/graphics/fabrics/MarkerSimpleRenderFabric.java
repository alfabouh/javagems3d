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

package workbench.graphics.fabrics;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import org.lwjgl.opengl.GL46;
import workbench.graphics.objects.WBenchMarkerObject;

import java.util.function.Consumer;

public class MarkerSimpleRenderFabric extends DirectRenderFabric {
    public MarkerSimpleRenderFabric(Stage stage) {
        super(stage);
    }

    @Override
    public void onPreRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {

    }

    @Override
    public void onRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {
        if (renderedItem instanceof WBenchMarkerObject modeled) {
            WBenchMarkerObject markerObject = (WBenchMarkerObject) renderedItem;
            if (renderedItem.canBeRendered()) {
                Model3D model = modeled.getModel();
                Consumer<Pair<JGemsShaderManager, IRendered>> functionToHandleUniforms = metaData.getterFunc().getObject(0);
                if (functionToHandleUniforms != null) {
                    functionToHandleUniforms.accept(new Pair<>(shaderManager, renderedItem));
                }
                shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
                shaderManager.performModel3DMatrix(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), model);
                shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
                shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC3F(markerObject.getColor()));
                shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.LIGHT), UniformFunctions.BOOLEAN(markerObject.isLighted()));
                JGemsHelper.render().renderModel3D(model, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
            }
        }
    }

    @Override
    public void onPostRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {

    }

    @Override
    public void createResources(IRendered renderedItem) {

    }

    @Override
    public void destroyResources(IRendered renderedItem) {

    }
}
