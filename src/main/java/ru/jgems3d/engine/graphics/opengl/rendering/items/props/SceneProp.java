package ru.jgems3d.engine.graphics.opengl.rendering.items.props;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class SceneProp extends AbstractSceneProp {
    public SceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull MeshRenderData meshRenderData) {
        super(renderFabric, model, meshRenderData);
    }

    public SceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        super(renderFabric, model, shaderManager);
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
