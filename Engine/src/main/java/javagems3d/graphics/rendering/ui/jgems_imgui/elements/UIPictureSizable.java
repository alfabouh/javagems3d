package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;

import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIPictureSizable extends UIElement {
    protected final ITexture2DProgram texture2DProgram;
    private final Vector2f position;
    private final Vector2f size;
    protected Model2D imageModel;

    public UIPictureSizable(IWindow window, @NotNull ITexture2DProgram texture2DProgram, @NotNull Vector2f position, @NotNull Vector2f size, float zValue) {
        super(window, JGemsResourceManager.globalShaderAssets.gui_image, zValue);
        this.texture2DProgram = texture2DProgram;
        this.position = position;
        this.size = size;
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.imageModel.getPose().setPosition(new Vector2f(this.getPosition()));
        this.imageModel.getPose().setScale(new Vector2f(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.beginShading();
        shaderManager.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX),
                (this.imageModel.getPose()),
                UIElement.getProjection(this.getWindow(), this.GLOBAL_SCALE_FACTOR()));
        shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.texture2DProgram);
        JGemsHelper.render().renderModel2D(this.imageModel, GL46.GL_TRIANGLES);
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
        return MeshHelper.generatePlane2DModel(new Vector2f(0.0f), new Vector2f(this.getOriginalSize()), this.getZValue());
    }

    @Override
    public @NotNull Vector2f getOriginalSize() {
        return new Vector2f(this.size);
    }

    @Override
    public @NotNull Vector2f getPosition() {
        return super.getScaleAffectedUiPos(this.position);
    }

    @Override
    public Vector2f getScaling() {
        return super.getScaleAffectedUiVector(super.getScaling());
    }

    @Override
    public @NotNull Vector2f getScaledSize() {
        return this.getOriginalSize().mul(this.getScaling());
    }

    @Override
    public int calcUIHash() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.texture2DProgram.hashCode();
        result = prime * result + this.getScaledSize().hashCode();
        result = prime * result + this.getPosition().hashCode();
        return result;
    }
}
