package ru.BouH.engine.render.scene.objects.gui.hud;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.fabric.render.RenderGui;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.BouH.engine.render.scene.objects.gui.AbstractGui;

public class GuiPicture extends AbstractGui {
    private static final RenderGui renderGui = new RenderGui();
    private TextureSample textureSample;
    private float width;
    private float height;

    public GuiPicture(@NotNull TextureSample textureSample, ShaderManager shaderManager, int x, int y, float w, float h, int zLevel) {
        super("gui_picture", shaderManager, zLevel);
        this.setPicture(textureSample, x, y, w, h);
    }

    public GuiPicture(@NotNull TextureSample textureSample, ShaderManager shaderManager, int x, int y, float w, float h) {
        this(textureSample, shaderManager, x, y, w, h, 0);
    }

    public GuiPicture(@NotNull TextureSample textureSample, ShaderManager shaderManager, int x, int y, int zLevel) {
        super("gui_picture", shaderManager, zLevel);
        this.setPicture(textureSample, x, y);
    }

    public GuiPicture(@NotNull TextureSample textureSample, ShaderManager shaderManager, int x, int y) {
        this(textureSample, shaderManager, x, y, 0);
    }

    public void setPicture(TextureSample textureSample, int x, int y) {
        this.setPicture(textureSample, x, y, textureSample.getWidth(), textureSample.getHeight());
    }

    public void setPicture(TextureSample textureSample, int x, int y, float w, float h) {
        if (textureSample.isValid()) {
            this.textureSample = textureSample;
            if (this.getModel2DInfo() != null) {
                this.getModel2DInfo().clean();
            }
            this.width = w;
            this.height = h;
            this.setModel2DInfo(MeshHelper.generateVector2DModel(new Vector2d(x, y), new Vector2d(x + w, y + h)));
        }
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public TextureSample getTexture() {
        return this.textureSample;
    }

    @Override
    public IRenderFabric renderFabric() {
        return GuiPicture.renderGui;
    }

    @Override
    public boolean hasRender() {
        return this.getModel2DInfo() != null;
    }

    @Override
    public void performGuiTexture() {
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.getTexture().bindTexture();
    }
}
