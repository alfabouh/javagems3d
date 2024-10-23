/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.rendering.imgui.elements;

import javagems3d.system.resources.assets.models.mesh.Mesh;
import javagems3d.system.resources.assets.models.mesh.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.DefaultPointers;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.imgui.ImmediateUI;
import javagems3d.graphics.opengl.rendering.imgui.elements.base.UIElement;
import javagems3d.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.shaders.base.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.manager.JGemsResourceManager;

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

            FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultPointers.POSITIONS);
            FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultPointers.TEXTURE_COORDINATES);

            float startX = 0.0f;
            for (int i = 0; i < chars.length; i++) {
                GuiFont.CharInfo charInfo = UIText.this.fontTexture.getCharInfo(chars[i]);
                vaPositions.put(startX);
                vaPositions.put(0.0f);
                vaPositions.put(z);
                vaTextureCoordinates.put((float) charInfo.getStartX() / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(0.0f);
                mesh.putVertexIndex(i * 4);

                vaPositions.put(startX);
                vaPositions.put(this.getHeight());
                vaPositions.put(z);
                vaTextureCoordinates.put((float) charInfo.getStartX() / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(1.0f);
                mesh.putVertexIndex(i * 4 + 1);

                vaPositions.put(startX + charInfo.getWidth());
                vaPositions.put(this.getHeight());
                vaPositions.put(z);
                vaTextureCoordinates.put((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(1.0f);
                mesh.putVertexIndex(i * 4 + 2);

                vaPositions.put(startX + charInfo.getWidth());
                vaPositions.put(0.0f);
                vaPositions.put(z);
                vaTextureCoordinates.put((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(0.0f);
                mesh.putVertexIndex(i * 4 + 3);

                mesh.putVertexIndex(i * 4);
                mesh.putVertexIndex(i * 4 + 2);

                startX += charInfo.getWidth();
            }
            this.width = startX;

            mesh.addVertexAttributeInMesh(vaPositions);
            mesh.addVertexAttributeInMesh(vaTextureCoordinates);

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
