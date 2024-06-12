package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.models;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class SceneObject extends AbstractSceneObject {
    public SceneObject(IRenderFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        super(renderFabric, model, shaderManager);
    }
}
