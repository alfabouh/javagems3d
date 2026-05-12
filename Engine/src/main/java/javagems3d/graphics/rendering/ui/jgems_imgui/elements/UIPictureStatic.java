package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;

import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIPictureStatic extends UIElement {
    protected final ITexture2DProgram iImageSample;
    private final Vector2i position;
    private final Vector2i size;
    private final Vector2f textureXY;
    private final Vector2f textureWH;
    protected Model2D imageModel;

    public UIPictureStatic(@NotNull ITexture2DProgram texture2DProgram, @NotNull Vector2i position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(JGemsResourceManager.globalShaderAssets.gui_image, zValue);
        this.iImageSample = texture2DProgram;
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
        shaderManager.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.imageModel, JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.iImageSample);
        JGemsHelper.render().renderModel2D(this.imageModel, GL46.GL_TRIANGLES);
        shaderManager.endShading();
    }

    @Override
    public void build() {
        this.imageModel = this.constructModel(iImageSample.getSize());
    }

    @Override
    public void clear() {
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
    public int calcUIHash() {
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
