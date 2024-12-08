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

package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.JGemsSceneUtils;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
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
        shaderManager.beginShading();
        shaderManager.getUtils().performOrthographicMatrix(this.textModel.getModel());
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        this.getFontTexture().getTexture().bindTexture();
        shaderManager.performUniform(new UniformString("texture_sampler"), UniformFunctions.INTEGER(0));
        shaderManager.performUniform(new UniformString("colour"), UniformFunctions.VEC4F(new Vector4f(JGemsUI.HEX2RGB(this.hexColor), 1.0f)));
        JGemsSceneUtils.renderModel(this.textModel.getModel(), GL46.GL_TRIANGLES);
        shaderManager.endShading();
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
    public void clearData() {
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
            RenderMesh renderMesh = new RenderMesh();
            char[] chars = UIText.this.getText().toCharArray();
            float z = UIText.this.getZValue();
            this.height = UIText.this.fontTexture.getHeight();

            FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
            FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);

            float startX = 0.0f;
            for (int i = 0; i < chars.length; i++) {
                GuiFont.CharInfo charInfo = UIText.this.fontTexture.getCharInfo(chars[i]);
                vaPositions.put(startX);
                vaPositions.put(0.0f);
                vaPositions.put(z);
                vaTextureCoordinates.put((float) charInfo.getStartX() / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(0.0f);
                renderMesh.putVertexIndex(i * 4);

                vaPositions.put(startX);
                vaPositions.put(this.getHeight());
                vaPositions.put(z);
                vaTextureCoordinates.put((float) charInfo.getStartX() / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(1.0f);
                renderMesh.putVertexIndex(i * 4 + 1);

                vaPositions.put(startX + charInfo.getWidth());
                vaPositions.put(this.getHeight());
                vaPositions.put(z);
                vaTextureCoordinates.put((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(1.0f);
                renderMesh.putVertexIndex(i * 4 + 2);

                vaPositions.put(startX + charInfo.getWidth());
                vaPositions.put(0.0f);
                vaPositions.put(z);
                vaTextureCoordinates.put((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(0.0f);
                renderMesh.putVertexIndex(i * 4 + 3);

                renderMesh.putVertexIndex(i * 4);
                renderMesh.putVertexIndex(i * 4 + 2);

                startX += charInfo.getWidth();
            }
            this.width = startX;

            renderMesh.addVertexAttributeInMesh(vaPositions);
            renderMesh.addVertexAttributeInMesh(vaTextureCoordinates);

            renderMesh.bakeMesh();
            return new Model<>(new Format2D(), new MeshGroup(new MeshGroup.MeshGroupNode(renderMesh)));
        }

        public void clear() {
            if (this.getModel() != null) {
                this.getModel().clear();
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
