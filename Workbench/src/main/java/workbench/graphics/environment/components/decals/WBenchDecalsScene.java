package workbench.graphics.environment.components.decals;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.environment.decals.fx.WorldDefaultDecalFX;
import javagems3d.graphics.environment.decals.scene.DecalsScene;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.resources.WBenchResourceManager;

public class WBenchDecalsScene extends DecalsScene {
    public WBenchDecalsScene(IEnvironment environment) {
        super(environment);
    }

    @Override
    public WorldDefaultDecalFX createDefaultWorldDecal(@NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scale, @NotNull DecalMaterial decalMaterial, @NotNull DecalTextureProperties decalTextureProperties, IEnvironment environment, float lifeTime, int terrainLayerID) {
        return new WorldDefaultDecalFX(decalMaterial, decalTextureProperties, lifeTime, terrainLayerID).setPosition(position).setRotation(rotation).setScale(scale);
    }
}
