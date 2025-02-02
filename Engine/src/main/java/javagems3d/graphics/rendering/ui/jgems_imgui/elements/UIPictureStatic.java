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
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;

import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIPictureStatic extends UIElement {
    protected final ImageTexture iImageSample;
    private final Vector2i position;
    private final Vector2i size;
    private final Vector2f textureXY;
    private final Vector2f textureWH;
    protected Model2D imageModel;

    public UIPictureStatic(@NotNull ImageTexture iImageSample, @NotNull Vector2i position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(JGemsResourceManager.globalShaderAssets.gui_image, zValue);
        this.iImageSample = iImageSample;
        this.position = position;
        this.textureXY = textureXY;
        this.textureWH = textureWH;
        this.size = new Vector2i((int) this.textureWH.x, (int) this.textureWH.y);
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.imageModel.getPose().setPosition(new Vector2f(this.getPosition()));
        this.imageModel.getPose().setScale(new Vector2f(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.beginShading();
        shaderManager.getUtils().performOrthographicMatrix(this.imageModel);
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        this.iImageSample.bindTexture();
        shaderManager.performUniform(new UniformString("texture_sampler"),  UniformFunctions.INTEGER(0));
        JGemsHelper.RENDERING.renderModel2D(this.imageModel, GL46.GL_TRIANGLES);
        shaderManager.endShading();
    }

    @Override
    public void buildUI() {
        this.imageModel = this.constructModel(iImageSample.getSize());
    }

    @Override
    public void clearData() {
        this.imageModel.clear();
    }

    protected Model2D constructModel(Vector2i imageSize) {
        Vector2f tMin = new Vector2f(this.textureXY.x / (float) imageSize.x, this.textureXY.y / (float) imageSize.y);
        Vector2f tMax = new Vector2f((this.textureWH.x + this.textureXY.x) / (float) imageSize.x, (this.textureWH.y + this.textureXY.y) / (float) imageSize.y);
        return MeshHelper.generatePlane2DModel(new Vector2f(0.0f), this.getZValue(), tMin, tMax, new Vector2f(this.textureWH).mul(this.getScaling()));
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
        result = prime * result + this.textureXY.hashCode();
        result = prime * result + this.textureWH.hashCode();
        result = prime * result + this.getSize().hashCode();
        result = prime * result + this.getPosition().hashCode();
        return result;
    }
}
