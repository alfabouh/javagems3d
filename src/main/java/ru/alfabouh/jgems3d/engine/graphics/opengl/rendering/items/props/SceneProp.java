package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.props;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SceneProp extends AbstractSceneProp {
    public SceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull ModelRenderParams modelRenderParams) {
        super(renderFabric, model, modelRenderParams);
    }

    public SceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        super(renderFabric, model, shaderManager);
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
