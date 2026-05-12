package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.flat.MeshGui;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;

import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIText extends UIElement {
    private final String text;
    private final int hexColor;
    private final Vector2i position;
    private final JGemsGuiFont fontTexture;
    private UIText.TextModel textModel;
    private boolean cacheText;

    public UIText(@NotNull String text, @NotNull JGemsGuiFont fontTexture, int hexColor, @NotNull Vector2i position, float zValue) {
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
        shaderManager.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.textModel.getModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        shaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getFontTexture().getTexture());
        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(JGemsUI.HEX2RGB(this.hexColor), 1.0f)));
        JGemsHelper.render().renderModel2D(this.textModel.getModel(), GL46.GL_TRIANGLES);
        shaderManager.endShading();
    }

    @Override
    public void build() {
        if (this.getText() != null && !this.getText().isEmpty()) {
            this.textModel = new UIText.TextModel();
            this.textModel.getModel().getPose().setPosition(new Vector2f(this.getPosition()));
            this.textModel.getModel().getPose().setScale(new Vector2f(this.getScaling()));
        }
    }

    @Override
    public void clear() {
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
    public int calcUIHash() {
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

    public JGemsGuiFont getFontTexture() {
        return this.fontTexture;
    }

    public boolean isCacheText() {
        return this.cacheText;
    }

    public void setCacheText(boolean cacheText) {
        this.cacheText = cacheText;
    }

    public class TextModel {
        private final Model2D model;
        private float width;
        private float height;

        public TextModel() {
            this.model = this.buildModel();
        }

        private Model2D buildModel() {
            RenderMesh renderMesh = new RenderMesh();
            char[] chars = UIText.this.getText().toCharArray();
            float z = UIText.this.getZValue();
            this.height = UIText.this.fontTexture.getHeight();

            FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
            FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);

            float startX = 0.0f;
            for (int i = 0; i < chars.length; i++) {
                JGemsGuiFont.CharInfo charInfo = UIText.this.fontTexture.getCharInfo(chars[i]);
                vaPositions.put(startX);
                vaPositions.put(0.0f);
                vaPositions.put(z);
                vaTextureCoordinates.put((float) charInfo.startX() / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(0.0f);
                renderMesh.putVertexIndex(i * 4);

                vaPositions.put(startX);
                vaPositions.put(this.getHeight());
                vaPositions.put(z);
                vaTextureCoordinates.put((float) charInfo.startX() / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(1.0f);
                renderMesh.putVertexIndex(i * 4 + 1);

                vaPositions.put(startX + charInfo.width());
                vaPositions.put(this.getHeight());
                vaPositions.put(z);
                vaTextureCoordinates.put((float) (charInfo.startX() + charInfo.width()) / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(1.0f);
                renderMesh.putVertexIndex(i * 4 + 2);

                vaPositions.put(startX + charInfo.width());
                vaPositions.put(0.0f);
                vaPositions.put(z);
                vaTextureCoordinates.put((float) (charInfo.startX() + charInfo.width()) / (float) UIText.this.fontTexture.getWidth());
                vaTextureCoordinates.put(0.0f);
                renderMesh.putVertexIndex(i * 4 + 3);

                renderMesh.putVertexIndex(i * 4);
                renderMesh.putVertexIndex(i * 4 + 2);

                startX += charInfo.width();
            }
            this.width = startX;

            renderMesh.putVertexAttribute(vaPositions);
            renderMesh.putVertexAttribute(vaTextureCoordinates);

            renderMesh.bakeMesh();
            return new Model2D(new Pose2D(), new MeshGui(renderMesh));
        }

        public void clear() {
            if (this.getModel() != null) {
                this.getModel().clear();
            }
        }

        public Model2D getModel() {
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
