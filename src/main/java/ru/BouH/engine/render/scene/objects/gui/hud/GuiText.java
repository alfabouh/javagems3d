package ru.BouH.engine.render.scene.objects.gui.hud;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.fabric.render.RenderGui;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.BouH.engine.render.scene.objects.gui.AbstractGui;
import ru.BouH.engine.render.scene.objects.gui.font.FontTexture;

public class GuiText extends AbstractGui {
    private static final RenderGui renderGui = new RenderGui();
    private final FontTexture fontTexture;
    private String text;
    private float width;

    public GuiText(String text, ShaderManager shaderManager, int x, int y) {
        this(text, shaderManager, ResourceManager.renderAssets.standardFont, x, y, 0);
    }

    public GuiText(String text, ShaderManager shaderManager, FontTexture fontTexture, int x, int y) {
        this(text, shaderManager, fontTexture, x, y, 0);
    }

    public GuiText(String text, ShaderManager shaderManager, FontTexture fontTexture, int x, int y, int zLevel) {
        super("gui_text: " + text, shaderManager, zLevel);
        this.fontTexture = fontTexture;
        this.setText(text);
        this.getModel2DInfo().getFormat().getPosition().set(x, y);
    }

    public GuiText(String text, ShaderManager shaderManager, int x, int y, int zLevel) {
        this(text, shaderManager, ResourceManager.renderAssets.standardFont, x, y, zLevel);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        if (this.getModel2DInfo() != null) {
            this.getModel2DInfo().clean();
        }
        this.setModel2DInfo(this.createModel());
    }

    public FontTexture getFontTexture() {
        return this.fontTexture;
    }

    public float getWidth() {
        return this.width;
    }

    private Model<Format2D> createModel() {
        Mesh mesh = new Mesh();
        char[] chars = this.getText().toCharArray();

        float startX = 0.0f;
        for (int i = 0; i < chars.length; i++) {
            FontTexture.CharInfo charInfo = this.fontTexture.getCharInfo(chars[i]);

            mesh.putPositionValue(startX);
            mesh.putPositionValue(0.0f);
            mesh.putPositionValue((float) this.getzLevel());
            mesh.putTextureCoordinateValue((float) charInfo.getStartX() / (float) this.fontTexture.getWidth());
            mesh.putTextureCoordinateValue(0.0f);
            mesh.putIndexValue(i * 4);

            mesh.putPositionValue(startX);
            mesh.putPositionValue((float) this.fontTexture.getHeight());
            mesh.putPositionValue((float) this.getzLevel());
            mesh.putTextureCoordinateValue((float) charInfo.getStartX() / (float) this.fontTexture.getWidth());
            mesh.putTextureCoordinateValue(1.0f);
            mesh.putIndexValue(i * 4 + 1);

            mesh.putPositionValue(startX + charInfo.getWidth());
            mesh.putPositionValue((float) this.fontTexture.getHeight());
            mesh.putPositionValue((float) this.getzLevel());
            mesh.putTextureCoordinateValue((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) this.fontTexture.getWidth());
            mesh.putTextureCoordinateValue(1.0f);
            mesh.putIndexValue(i * 4 + 2);

            mesh.putPositionValue(startX + charInfo.getWidth());
            mesh.putPositionValue(0.0f);
            mesh.putPositionValue((float) this.getzLevel());
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

    @Override
    public IRenderFabric renderFabric() {
        return GuiText.renderGui;
    }

    @Override
    public boolean hasRender() {
        return this.getModel2DInfo() != null;
    }

    @Override
    public void performGuiTexture() {
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.getFontTexture().getTexture().bindTexture();
    }
}
