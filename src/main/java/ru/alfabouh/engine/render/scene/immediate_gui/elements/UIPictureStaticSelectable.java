package ru.alfabouh.engine.render.scene.immediate_gui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.textures.IImageSample;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;

public class UIPictureStaticSelectable extends UIPictureStatic {
    private boolean selected;

    public UIPictureStaticSelectable(@NotNull IImageSample iImageSample, @NotNull Vector2i position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(iImageSample, position, textureXY, textureWH, zValue);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public ShaderManager getCurrentShader() {
        return ResourceManager.shaderAssets.gui_image_selectable;
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
        this.getCurrentShader().performUniform("selected", this.isSelected());
        Scene.renderModel(this.imageModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }
}
