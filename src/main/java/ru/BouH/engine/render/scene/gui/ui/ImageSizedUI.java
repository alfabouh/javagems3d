package ru.BouH.engine.render.scene.gui.ui;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.Scene;

public class ImageSizedUI implements BasicUI {
    private TextureSample image;
    private Vector3f position;
    private Vector2f size;
    private boolean isVisible;
    private Model<Format2D> imageModel;
    private boolean normalizeByScreen;
    private ShaderManager shaderManager;

    public ImageSizedUI(@NotNull TextureSample image) {
        this(image, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(0.0f));
    }

    public ImageSizedUI(@NotNull TextureSample image, Vector3f position, Vector2f size) {
        this.image = image;
        this.position = position;
        this.size = size;
        this.isVisible = true;
        this.normalizeByScreen = false;
        this.shaderManager = ResourceManager.shaderAssets.gui_image;
        this.constructModel();
    }

    protected void constructModel() {
        this.clear();
        this.imageModel = MeshHelper.generatePlane2DModel(new Vector2f(0.0f), new Vector2f(this.size), (int) this.getPosition().z);
    }

    protected Vector2f scaling() {
        return this.isNormalizedByScreen() ? BasicUI.getScreenNormalizedScaling() : new Vector2f(1.0f);
    }

    @Override
    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        ShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        Model<Format2D> model = this.getImageModel();
        model.getFormat().setScale(new Vector2d(this.scaling()));
        model.getFormat().setPosition(new Vector2d(this.getPosition().x, this.getPosition().y));
        shaderManager.getUtils().performProjectionMatrix2d(model);
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

    @Override
    public boolean isVisible() {
        return this.isVisible && this.getImageModel() != null && this.getSize().length() > 0;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public ShaderManager getCurrentShader() {
        return this.shaderManager;
    }

    public Model<Format2D> getImageModel() {
        return this.imageModel;
    }

    public TextureSample getImage() {
        return this.image;
    }

    public void setImage(TextureSample image) {
        this.image = image;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector2f getSize() {
        return new Vector2f(this.size).mul(this.scaling());
    }

    public boolean isNormalizedByScreen() {
        return this.normalizeByScreen;
    }

    public void setNormalizeByScreen(boolean normalizeByScreen) {
        this.normalizeByScreen = normalizeByScreen;
    }

    public void setSize(Vector2f size) {
        this.size = size;
        this.constructModel();
    }
}
