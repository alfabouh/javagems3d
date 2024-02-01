package ru.BouH.engine.render.scene.fabric.models;

import org.joml.Vector2d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.fabric.constraints.ModelRenderConstraints;
import ru.BouH.engine.render.scene.fabric.models.base.RenderSceneObject;

public class RenderSceneModel extends RenderSceneObject {
    private final boolean lightOpaque;
    private final Vector2d textureScaling;

    public RenderSceneModel(boolean lightOpaque, Vector2d textureScaling, Model<Format3D> model, ShaderManager shaderManager) {
        super(model, shaderManager);
        this.lightOpaque = lightOpaque;
        this.textureScaling = textureScaling;
    }

    public RenderSceneModel(boolean lightOpaque, Model<Format3D> model, ShaderManager shaderManager) {
        super(model, shaderManager);
        this.lightOpaque = lightOpaque;
        this.textureScaling = new Vector2d(1.0d);
    }

    public RenderSceneModel(Model<Format3D> model, ShaderManager shaderManager) {
        super(model, shaderManager);
        this.lightOpaque = true;
        this.textureScaling = new Vector2d(1.0d);
    }

    @Override
    public void onRender(double partialTicks) {
        this.getShaderManager().bind();
        this.getShaderManager().getUtils().performProjectionMatrix();
        this.getShaderManager().performUniform("texture_scaling", this.textureScaling);
        Scene.renderModelWithMaterials(this.toRender(), this.getShaderManager(), this.getModelRenderConstraints(), GL30.GL_TRIANGLES);
        this.getShaderManager().unBind();
    }
}
