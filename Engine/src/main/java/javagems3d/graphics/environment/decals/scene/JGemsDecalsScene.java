package javagems3d.graphics.environment.decals.scene;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.environment.decals.fx.WorldDefaultDecalFX;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.initialization.GlobalShadersInitializer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

public class JGemsDecalsScene extends DecalsScene {
    public JGemsDecalsScene(IEnvironment environment) {
        super(environment);
    }

    @Override
    public WorldDefaultDecalFX createDefaultWorldDecal(@NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scale, @NotNull DecalMaterial decalMaterial, @NotNull DecalTextureProperties decalTextureProperties, float lifeTime, int terrainLayerID) {
        return new WorldDefaultDecalFX(decalMaterial, decalTextureProperties, lifeTime, terrainLayerID).setPosition(position).setRotation(rotation).setScale(scale);
    }
}
