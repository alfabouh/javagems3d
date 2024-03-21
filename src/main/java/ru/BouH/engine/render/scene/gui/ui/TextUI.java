package ru.BouH.engine.render.scene.gui.ui;

import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.gui.font.GuiFont;

public class TextUI implements BasicUI {
    private String text;
    private int hexColor;
    private Vector3f position;
    private final TextModel textModel;
    private boolean isVisible;

    public TextUI(String text, GuiFont fontTexture, int hexColor, Vector3f position) {
        this.text = text;
        this.hexColor = hexColor;
        this.position = position;
        this.textModel = new TextModel(fontTexture);
        this.isVisible = true;
    }

    public TextUI(String text, GuiFont fontTexture, Vector3f position) {
        this(text, fontTexture, 0xffffff, position);
    }

    public TextUI(String text, GuiFont fontTexture) {
        this(text, fontTexture, 0xffffff, new Vector3f(0.0f, 0.0f, 0.5f));
    }

    public TextUI(GuiFont fontTexture) {
        this("", fontTexture, 0xffffff, new Vector3f(0.0f, 0.0f, 0.5f));
    }

    public TextUI(GuiFont fontTexture, Vector3f position) {
        this("", fontTexture, 0xffffff, position);
    }

    @Override
    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        float[] f1 = BasicUI.HEX2RGB(this.getHexColor());
        this.getTextModel().getModel().getFormat().setPosition(new Vector2d(this.getPosition().x, this.getPosition().y));
        ShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performProjectionMatrix2d(this.getTextModel().getModel());
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.getTextModel().getFontTexture().getTexture().bindTexture();
        shaderManager.performUniform("texture_sampler", 0);
        shaderManager.performUniform("colour", new Vector4f(f1[0], f1[1], f1[2], 1.0f));
        Scene.renderModel(this.getTextModel().getModel(), GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }

    @Override
    public void clear() {
        this.getTextModel().clear();
    }

    @Override
    public boolean isVisible() {
        return this.isVisible && !this.getText().isEmpty() && this.getTextModel().getModel() != null;
    }

    @Override
    public ShaderManager getCurrentShader() {
        return ResourceManager.shaderAssets.gui_text;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setHexColor(int hexColor) {
        this.hexColor = hexColor;
    }

    public void setText(String text) {
        this.text = text;
        this.getTextModel().refresh();
    }

    public float getTextWidth() {
        return this.getTextModel().getWidth();
    }

    public float getTextHeight() {
        return this.getTextModel().getHeight();
    }

    protected TextModel getTextModel() {
        return this.textModel;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public String getText() {
        return this.text;
    }

    public class TextModel {
        private Model<Format2D> model;
        private float width;
        private float height;
        private final GuiFont fontTexture;

        public TextModel(GuiFont fontTexture) {
            this.fontTexture = fontTexture;
            this.refresh();
        }

        private Model<Format2D> buildModel() {
            if (TextUI.this.getText().isEmpty()) {
                return null;
            }
            Mesh mesh = new Mesh();
            char[] chars = TextUI.this.getText().toCharArray();
            float z = TextUI.this.getPosition().z;
            this.height = this.getFontTexture().getHeight();

            float startX = 0.0f;
            for (int i = 0; i < chars.length; i++) {
                GuiFont.CharInfo charInfo = this.getFontTexture().getCharInfo(chars[i]);

                mesh.putPositionValue(startX);
                mesh.putPositionValue(0.0f);
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) charInfo.getStartX() / (float) this.getFontTexture().getWidth());
                mesh.putTextureCoordinateValue(0.0f);
                mesh.putIndexValue(i * 4);

                mesh.putPositionValue(startX);
                mesh.putPositionValue(this.getHeight());
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) charInfo.getStartX() / (float) this.getFontTexture().getWidth());
                mesh.putTextureCoordinateValue(1.0f);
                mesh.putIndexValue(i * 4 + 1);

                mesh.putPositionValue(startX + charInfo.getWidth());
                mesh.putPositionValue(this.getHeight());
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) this.getFontTexture().getWidth());
                mesh.putTextureCoordinateValue(1.0f);
                mesh.putIndexValue(i * 4 + 2);

                mesh.putPositionValue(startX + charInfo.getWidth());
                mesh.putPositionValue(0.0f);
                mesh.putPositionValue(z);
                mesh.putTextureCoordinateValue((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) this.getFontTexture().getWidth());
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

        public void refresh() {
            this.clear();
            this.model = this.buildModel();
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
