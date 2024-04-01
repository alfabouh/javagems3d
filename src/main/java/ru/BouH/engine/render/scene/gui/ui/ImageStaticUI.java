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

public class ImageStaticUI implements BasicUI {
    private TextureSample image;
    private Vector3f position;
    private Vector2f size;
    private Vector2f textureXY;
    private Vector2f textureWH;
    private boolean isVisible;
    private Model<Format2D> imageModel;
    private float scaling;
    private boolean normalizeByScreen;
    private ShaderManager shaderManager;

    public ImageStaticUI(@NotNull TextureSample image) {
        this(image, new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(0.0f), new Vector2f(image.getWidth(), image.getHeight()));
    }

    public ImageStaticUI(@NotNull TextureSample image, Vector3f position, Vector2f textureXY, Vector2f textureWH) {
        this.image = image;
        this.position = position;
        this.isVisible = true;
        this.normalizeByScreen = false;
        this.scaling = 1.0f;
        this.textureWH = textureWH;
        this.textureXY = textureXY;
        this.shaderManager = ResourceManager.shaderAssets.gui_image;
        this.constructModel(textureXY, textureWH);
    }

    protected void constructModel(Vector2f textureXY, Vector2f textureWH) {
        Vector2f normalSize = new Vector2f(textureWH.x, textureWH.y);
        this.size = new Vector2f(normalSize);
        Vector2f imageSize = new Vector2f(image.getWidth(), image.getHeight());
        Vector2f tMin = new Vector2f(textureXY.x, textureXY.y).div(imageSize);
        Vector2f tMax = new Vector2f(textureWH.x + textureXY.x, textureWH.y + textureXY.y).div(imageSize);
        this.clear();
        this.imageModel = MeshHelper.generatePlane2DModel(new Vector2f(0.0f), (int) this.getPosition().z, tMin, tMax, normalSize);
    }

    protected Vector2f scaling() {
        return this.isNormalizedByScreen() ? BasicUI.getScreenNormalizedScaling().mul(this.getScaling()) : new Vector2f(this.getScaling());
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }

    public float getScaling() {
        return this.scaling;
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

    public void setShaderManager(ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
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

    public void setTextureXY(Vector2f textureXY) {
        this.textureXY = textureXY;
        this.constructModel(textureXY, this.getTextureWH());
    }

    public void setTextureWH(Vector2f textureWH) {
        this.textureWH = textureWH;
        this.constructModel(this.getTextureXY(), textureWH);
    }

    public Vector2f getTextureXY() {
        return new Vector2f(this.textureXY);
    }

    public Vector2f getTextureWH() {
        return new Vector2f(this.textureWH);
    }
}
