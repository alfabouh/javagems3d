package ru.BouH.engine.render.scene.gui.ui;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.Scene;

public class ImageUI implements BasicUI {
    private TextureSample image;
    private Vector3f position;
    private Vector2f size;
    private boolean isVisible;
    private Model<Format2D> imageModel;

    public ImageUI(@NotNull TextureSample image) {
        this(image, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(0.0f));
    }

    public ImageUI(@NotNull TextureSample image, Vector3f position, Vector2f size) {
        this.image = image;
        this.position = position;
        this.size = size;
        this.isVisible = true;
        this.constructModel();
    }

    protected void constructModel() {
        this.clear();
        this.imageModel = MeshHelper.generatePlane2DModel(new Vector2f(this.getPosition().x, this.getPosition().y), this.getSize().add(this.getPosition().x, this.getPosition().y), (int) this.getPosition().z);
    }

    @Override
    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        ShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performProjectionMatrix2d(this.getImageModel());
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.getImage().bindTexture();
        shaderManager.performUniform("texture_sampler", 0);
        Scene.renderModel(this.getImageModel(), GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }

    @Override
    public void clear() {
        if (this.getImageModel() != null) {
            this.getImageModel().clean();
        }
    }

    public Model<Format2D> getImageModel() {
        return this.imageModel;
    }

    public TextureSample getImage() {
        return this.image;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setSize(Vector2f size) {
        this.size = size;
        this.constructModel();
    }

    public void setImage(TextureSample image) {
        this.image = image;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector2f getSize() {
        return new Vector2f(this.size);
    }

    @Override
    public boolean isVisible() {
        return this.isVisible && this.getImageModel() != null && this.getSize().length() > 0;
    }

    @Override
    public ShaderManager getCurrentShader() {
        return ResourceManager.shaderAssets.gui_image;
    }
}
