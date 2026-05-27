package workbench.graphics.environment.components.decals;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.environment.decals.fx.WorldDefaultDecalFX;
import javagems3d.graphics.environment.decals.scene.DecalsScene;
import javagems3d.graphics.environment.decals.scene.JGemsDecalsScene;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.resources.WBenchResourceManager;

public class WBenchDecalsScene extends JGemsDecalsScene {
    public WBenchDecalsScene(IEnvironment environment) {
        super(environment);
    }
}
