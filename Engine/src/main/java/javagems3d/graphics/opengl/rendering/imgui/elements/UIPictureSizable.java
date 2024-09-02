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

package javagems3d.graphics.opengl.rendering.imgui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.imgui.elements.base.UIElement;
import javagems3d.system.resources.assets.material.samples.base.ITextureSample;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.manager.JGemsResourceManager;

public class UIPictureSizable extends UIElement {
    protected final ITextureSample iImageSample;
    private final Vector2i position;
    private final Vector2i size;
    protected Model<Format2D> imageModel;

    public UIPictureSizable(@NotNull ITextureSample iImageSample, @NotNull Vector2i position, @NotNull Vector2i size, float zValue) {
        super(JGemsResourceManager.globalShaderAssets.gui_image, zValue);
        this.iImageSample = iImageSample;
        this.position = position;
        this.size = size;
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.imageModel.getFormat().setPosition(new Vector2f(this.getPosition()));
        this.imageModel.getFormat().setScale(new Vector2f(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(this.imageModel);
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.iImageSample.bindTexture();
        shaderManager.performUniform(new UniformString("texture_sampler"), 0);
        JGemsSceneUtils.renderModel(this.imageModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }

    @Override
    public void buildUI() {
        this.imageModel = this.constructModel();
    }

    @Override
    public void cleanData() {
        this.imageModel.clean();
    }

    protected Model<Format2D> constructModel() {
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
