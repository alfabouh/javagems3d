package ru.alfabouh.engine.render.scene.fabric.models;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.render.scene.fabric.render.base.IRenderFabric;

public class SceneObject extends AbstractSceneObject {
    public SceneObject(IRenderFabric renderFabric, Model<Format3D> model, @NotNull ShaderManager shaderManager) {
        super(renderFabric, model, shaderManager);
    }
}
