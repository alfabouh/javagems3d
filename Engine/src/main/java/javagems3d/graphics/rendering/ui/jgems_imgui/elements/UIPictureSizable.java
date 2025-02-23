/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.JGemsHelper;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.pose.Pose2D;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;

import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIPictureSizable extends UIElement {
    protected final ImageBasedTexture iImageSample;
    private final Vector2i position;
    private final Vector2i size;
    protected Model2D imageModel;

    public UIPictureSizable(@NotNull ImageBasedTexture iImageSample, @NotNull Vector2i position, @NotNull Vector2i size, float zValue) {
        super(JGemsResourceManager.globalShaderAssets.gui_image, zValue);
        this.iImageSample = iImageSample;
        this.position = position;
        this.size = size;
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.imageModel.getPose().setPosition(new Vector2f(this.getPosition()));
        this.imageModel.getPose().setScale(new Vector2f(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.beginShading();
        shaderManager.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.imageModel, JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        shaderManager.performUniformTexture(new UniformString("texture_sampler"), this.iImageSample);
        JGemsHelper.RENDERING.renderModel2D(this.imageModel, GL46.GL_TRIANGLES);
        shaderManager.endShading();
    }

    @Override
    public void build() {
        this.imageModel = this.constructModel();
    }

    @Override
    public void clear() {
        this.imageModel.clear();
    }

    protected Model2D constructModel() {
        return MeshHelper.generatePlane2DModel(new Vector2f(0.0f), new Vector2f(this.getSize()), this.getZValue());
    }

    public @NotNull Vector2i getPosition() {
        return this.position;
    }

    @Override
    public @NotNull Vector2i getSize() {
        return new Vector2i((int) (this.size.x * this.getScaling().x), (int) (this.size.y * this.getScaling().y));
    }

    @Override
    public int calcUIHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.iImageSample.hashCode();
        result = prime * result + this.getSize().hashCode();
        result = prime * result + this.getPosition().hashCode();
        return result;
    }
}
