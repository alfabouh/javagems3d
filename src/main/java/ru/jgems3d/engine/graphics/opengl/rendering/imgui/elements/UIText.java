package ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.UIElement;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class UIText extends UIElement {
    private final String text;
    private final int hexColor;
    private final Vector2i position;
    private final GuiFont fontTexture;
    private UIText.TextModel textModel;
    private boolean cacheText;

    public UIText(@NotNull String text, @NotNull GuiFont fontTexture, int hexColor, @NotNull Vector2i position, float zValue) {
        super(JGemsResourceManager.globalShaderAssets.gui_text, zValue);
        this.fontTexture = fontTexture;
        this.text = text;
        this.hexColor = hexColor;
        this.position = position;
        this.cacheText = true;
    }

    @Override
    public void render(float frameDeltaTicks) {
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(this.textModel.getModel());
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.getFontTexture().getTexture().bindTexture();
        shaderManager.performUniform(new UniformString("texture_sampler"), 0);
        shaderManager.performUniform(new UniformString("colour"), new Vector4f(ImmediateUI.HEX2RGB(this.hexColor), 1.0f));
        JGemsSceneUtils.renderModel(this.textModel.getModel(), GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }

    @Override
    public void buildUI() {
        if (this.getText() != null && !this.getText().isEmpty()) {
            this.textModel = new UIText.TextModel();
            this.textModel.getModel().getFormat().setPosition(new Vector2f(this.getPosition()));
            this.textModel.getModel().getFormat().setScale(new Vector2f(this.getScaling()));
        }
    }

    @Override
    public void cleanData() {
        if (this.textModel != null) {
            this.textModel.clear();
        }
    }

    public @NotNull Vector2i getPosition() {
        return this.position;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public @NotNull Vector2i getSize() {
        return new Vector2i((int) (this.textModel.width * this.getScaling().x), (int) (this.textModel.height * this.getScaling().y));
    }

    @Override
    public int calcUIHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.hexColor;
        result = prime * result + this.getPosition().hashCode();
        result = prime * result + this.getSize().hashCode();
        if (this.isCacheText()) {
            result = prime * result + this.text.hashCode();
        }
        result = prime * result + this.getFontTexture().hashCode();
        return result;
    }

    public GuiFont getFontTexture() {
        return this.fontTexture;
    }

    public boolean isCacheText() {
        return this.cacheText;
    }

    public void setCacheText(boolean cacheText) {
        this.cacheText = cacheText;
    }

    public class TextModel {
        private final Model<Format2D> model;
        private float width;
        private float height;

        public TextModel() {
            this.model = this.buildModel();
        }

        private Model<Format2D> buildModel() {
            Mesh mesh = new Mesh();
            char[] chars = UIText.this.getText().toCharArray();
            float z = UIText.this.getZValue();
            this.height = UIText.this.fontTexture.getHeight();

            float startX = 0.0f;
            for (int i = 0; i < chars.length; i++) {
                GuiFont.CharInfo charInfo = UIText.this.fontTexture.getCharInfo(chars[i]);
                mesh.pushPosition(startX);
                mesh.pushPosition(0.0f);
                mesh.pushPosition(z);
                mesh.pushTextureCoordinate((float) charInfo.getStartX() / (float) UIText.this.fontTexture.getWidth());
                mesh.pushTextureCoordinate(0.0f);
                mesh.pushIndex(i * 4);

                mesh.pushPosition(startX);
                mesh.pushPosition(this.getHeight());
                mesh.pushPosition(z);
                mesh.pushTextureCoordinate((float) charInfo.getStartX() / (float) UIText.this.fontTexture.getWidth());
                mesh.pushTextureCoordinate(1.0f);
                mesh.pushIndex(i * 4 + 1);

                mesh.pushPosition(startX + charInfo.getWidth());
                mesh.pushPosition(this.getHeight());
                mesh.pushPosition(z);
                mesh.pushTextureCoordinate((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) UIText.this.fontTexture.getWidth());
                mesh.pushTextureCoordinate(1.0f);
                mesh.pushIndex(i * 4 + 2);

                mesh.pushPosition(startX + charInfo.getWidth());
                mesh.pushPosition(0.0f);
                mesh.pushPosition(z);
                mesh.pushTextureCoordinate((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) UIText.this.fontTexture.getWidth());
                mesh.pushTextureCoordinate(0.0f);
                mesh.pushIndex(i * 4 + 3);

                mesh.pushIndex(i * 4);
                mesh.pushIndex(i * 4 + 2);

                startX += charInfo.getWidth();
            }
            this.width = startX;
            mesh.bakeMesh();

            return new Model<>(new Format2D(), mesh);
        }

        public void clear() {
            if (this.getModel() != null) {
                this.getModel().clean();
            }
        }

        public Model<Format2D> getModel() {
            return this.model;
        }

        public float getWidth() {
            return this.width;
        }

        public float getHeight() {
            return this.height;
        }
    }
}
