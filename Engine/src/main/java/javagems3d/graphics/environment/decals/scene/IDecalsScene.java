package javagems3d.graphics.environment.decals.scene;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.environment.decals.fx.WorldDefaultDecalFX;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public interface IDecalsScene {
    void update(IRenderWorld renderWorld);
    DecalFX spawnDecalFX(@NotNull DecalFX decalFX);
    WorldDefaultDecalFX createDefaultWorldDecal(@NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scale, @NotNull DecalMaterial decalMaterial, @NotNull DecalTextureProperties decalTextureProperties, IEnvironment environment, float lifeTime, int terrainLayerID);

    default void destroyDecalFX(@NotNull DecalFX decalFX) {
        decalFX.setDead();
    }

    void clear();
    List<DecalFX> getDecalFXCollection();
}
