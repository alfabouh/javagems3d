package ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.UIElement;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.font.GuiFont;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class UIText extends UIElement {
    private UIText.TextModel textModel;
    private final String text;
    private final int hexColor;
    private final Vector2i position;
    private boolean cacheText;

    public UIText(@NotNull String text, @NotNull GuiFont fontTexture, int hexColor, @NotNull Vector2i position, float zValue) {
        super(ResourceManager.shaderAssets.gui_text, zValue);
        this.text = text;
        this.hexColor = hexColor;
        this.position = position;
        this.cacheText = true;

        if (this.getText() != null && !this.getText().isEmpty()) {
            this.textModel = new UIText.TextModel(fontTexture);
            this.textModel.getModel().getFormat().setPosition(new Vector2d(this.getPosition()));
            this.textModel.getModel().getFormat().setScale(new Vector2d(this.getScaling()));
        }
    }

    @Override
    public void render(double partialTicks) {
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(this.textModel.getModel());
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.textModel.getFontTexture().getTexture().bindTexture();
        shaderManager.performUniform("texture_sampler", 0);
        shaderManager.performUniform("colour", new Vector4f(ImmediateUI.HEX2RGB(this.hexColor), 1.0f));
        JGemsSceneUtils.renderModel(this.textModel.getModel(), GL30.GL_TRIANGLES);
        shaderManager.unBind();
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
        result = prime * result + this.textModel.getFontTexture().hashCode();
        return result;
    }

    public void setCacheText(boolean cacheText) {
        this.cacheText = cacheText;
    }

    public boolean isCacheText() {
        return this.cacheText;
    }

    public class TextModel {
        private final Model<Format2D> model;
        private float width;
        private float height;
        private final GuiFont fontTexture;

        public TextModel(GuiFont fontTexture) {
            this.fontTexture = fontTexture;
            this.model = this.buildModel();
        }

        private Model<Format2D> buildModel() {
            Mesh mesh = new Mesh();
            char[] chars = UIText.this.getText().toCharArray();
            float z = UIText.this.getZValue();
            this.height = this.fontTexture.getHeight();

            float startX = 0.0f;
            for (int i = 0; i < chars.length; i++) {
                GuiFont.CharInfo charInfo = this.fontTexture.getCharInfo(chars[i]);

                mesh.putPositionValue(startX);
                mesh.putPositionValue(0.0f);
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) charInfo.getStartX() / (float) this.fontTexture.getWidth());
                mesh.putTextureCoordinateValue(0.0f);
                mesh.putIndexValue(i * 4);

                mesh.putPositionValue(startX);
                mesh.putPositionValue(this.getHeight());
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) charInfo.getStartX() / (float) this.fontTexture.getWidth());
                mesh.putTextureCoordinateValue(1.0f);
                mesh.putIndexValue(i * 4 + 1);

                mesh.putPositionValue(startX + charInfo.getWidth());
                mesh.putPositionValue(this.getHeight());
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) this.fontTexture.getWidth());
                mesh.putTextureCoordinateValue(1.0f);
                mesh.putIndexValue(i * 4 + 2);

                mesh.putPositionValue(startX + charInfo.getWidth());
                mesh.putPositionValue(0.0f);
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) this.fontTexture.getWidth());
                mesh.putTextureCoordinateValue(0.0f);
                mesh.putIndexValue(i * 4 + 3);

                mesh.putIndexValue(i * 4);
                mesh.putIndexValue(i * 4 + 2);

                startX += charInfo.getWidth();
            }
            this.width = startX;
            mesh.bakeMesh();

            return new Model<>(new Format2D(), mesh);
        }

        public GuiFont getFontTexture() {
            return this.fontTexture;
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
