package ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.IImageSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

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
    public JGemsShaderManager getCurrentShader() {
        return ResourceManager.shaderAssets.gui_image_selectable;
    }

    @Override
    public void render(double partialTicks) {
        this.imageModel.getFormat().setPosition(new Vector2d(this.getPosition()));
        this.imageModel.getFormat().setScale(new Vector2d(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(this.imageModel);
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.iImageSample.bindTexture();
        shaderManager.performUniform("texture_sampler", 0);
        this.getCurrentShader().performUniform("selected", this.isSelected());
        JGemsSceneUtils.renderModel(this.imageModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }
}
