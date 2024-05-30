package ru.alfabouh.engine.render.scene.gui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.gui.elements.base.UIElement;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.textures.IImageSample;
import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.system.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;

public class UIPictureSizable extends UIElement {
    protected final IImageSample iImageSample;
    protected final Model<Format2D> imageModel;
    private final Vector2i position;
    private final Vector2i size;

    public UIPictureSizable(@NotNull IImageSample iImageSample, @NotNull Vector2i position, @NotNull Vector2i size, float zValue) {
        super(ResourceManager.shaderAssets.gui_image, zValue);
        this.iImageSample = iImageSample;
        this.position = position;
        this.size = size;

        this.imageModel = this.constructModel();
    }

    @Override
    public void render(double partialTicks) {
        this.imageModel.getFormat().setPosition(new Vector2d(this.getPosition()));
        this.imageModel.getFormat().setScale(new Vector2d(this.getScaling()));
        ShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performProjectionMatrix2d(this.imageModel);
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.iImageSample.bindTexture();
        shaderManager.performUniform("texture_sampler", 0);
        Scene.renderModel(this.imageModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
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
