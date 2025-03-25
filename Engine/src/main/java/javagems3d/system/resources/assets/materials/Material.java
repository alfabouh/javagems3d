package javagems3d.system.resources.assets.materials;

import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.ISample;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor3;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.service.synchronizing.GPUSyncer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@SuppressWarnings("all")
public class Material {
    private final Transparency transparency;

    private final ITexture2DProgram diffuseMap;
    private final ISampleColor4 diffuseColor;

    private final ITexture2DProgram emissionMap;
    private final ISampleColor3 emissionColor;

    private final ITexture2DProgram metallicRoughnessMap;
    private final ITexture2DProgram normalsMap;

    private final float metallicFactor;
    private final float roughnessFactor;

    public Material(@Nullable ITexture2DProgram diffuseMap, @NotNull ISampleColor4 diffuseColor, @Nullable ITexture2DProgram emissionMap, @Nullable ISampleColor3 emissionColor, @Nullable ITexture2DProgram metallicRoughnessMap, @Nullable ITexture2DProgram normalsMap, float metallicFactor, float roughnessFactor) {
        this.diffuseMap = diffuseMap;
        this.diffuseColor = diffuseColor;
        this.emissionMap = emissionMap;
        this.emissionColor = emissionColor;
        this.metallicRoughnessMap = metallicRoughnessMap;
        this.normalsMap = normalsMap;
        this.metallicFactor = metallicFactor;
        this.roughnessFactor = roughnessFactor;
        this.transparency = new Transparency(diffuseColor);
    }

    public Material(@NotNull ITexture2DProgram diffuseMap, @NotNull ISampleColor4 diffuseColor) {
        this(diffuseMap, diffuseColor, null, null, null, null, 0.0f, 0.0f);
    }

    public Material(@NotNull ITexture2DProgram diffuseMap) {
        this(diffuseMap, new Color4Texture(new Vector4f(1.0f)), null, null, null, null, 0.0f, 0.0f);
    }

    public Material(@NotNull ISampleColor4 diffuseColor) {
        this(null, diffuseColor, null, null, null, null, 0.0f, 0.0f);
    }

    public Material() {
        this(ResourceManager.DEFAULT_TEXTURE(), new Color4Texture(new Vector4f(1.0f)), null, null, null, null, 0.0f, 0.0f);
    }

    public float getOpacity() {
        return this.getTransparency().getOpacity();
    }

    public Material setOpacity(float opacity) {
        this.getTransparency().setOpacity(opacity);
        return this;
    }

    public boolean hasTransparency() {
        return this.getTransparency().hasTransparency();
    }

    public Transparency getTransparency() {
        return this.transparency;
    }

    public ITexture2DProgram getDiffuseMap() {
        return this.diffuseMap;
    }

    public ISampleColor4 getDiffuseColor() {
        return this.diffuseColor;
    }

    public ITexture2DProgram getEmissionMap() {
        return this.emissionMap;
    }

    public ISampleColor3 getEmissionColor() {
        return this.emissionColor;
    }

    public ITexture2DProgram getMetallicRoughnessMap() {
        return this.metallicRoughnessMap;
    }

    public ITexture2DProgram getNormalsMap() {
        return this.normalsMap;
    }

    public float getMetallicFactor() {
        return this.metallicFactor;
    }

    public float getRoughnessFactor() {
        return this.roughnessFactor;
    }

    public static class Transparency {
        private boolean hasTransparentPixels;
        private float opacity;
        private ISample diffuse;

        public Transparency(@NotNull ISample diffuse) {
            this.opacity = 1.0f;
            this.diffuse = diffuse;
            this.hasTransparentPixels = false;
        }

        public static boolean scanForAlphaPixels(@NotNull JGemsShaderManager computing, ITexture2DProgram imageTexture) {
            int texWidth = imageTexture.getSize().x;
            int texHeight = imageTexture.getSize().y;

            ShaderStorageBufferProgram.clearBufferData(JGemsResourceManager.globalShaderAssets.TextureScan, GL46.GL_R32UI, GL46.GL_RED, GL46.GL_UNSIGNED_INT, null);
            try (GPUSyncer.SyncObj syncObj = GPUSyncer.create(1000)) {
                computing.beginComputing();
                computing.performUniformTextureBindless(new UniformString("inputTexture"), imageTexture);
                computing.dispatchComputeShader((texWidth + 15) / 16, (texHeight + 15) / 16, 1, GL46.GL_SHADER_STORAGE_BARRIER_BIT);
                computing.endComputing();
            }

            ByteBuffer buffer = ShaderStorageBufferProgram.readData(JGemsResourceManager.globalShaderAssets.TextureScan);
            buffer.rewind();
            return buffer.getInt(0) > 1;
        }

        public void setHasTransparentPixels(boolean hasTransparentPixels) {
            this.hasTransparentPixels = hasTransparentPixels;
        }

        public boolean hasTransparentPixels() {
            return this.hasTransparentPixels;
        }

        public boolean hasTransparency() {
            if (this.getOpacity() < 1.0f) {
                return true;
            }
            if (this.hasTransparentPixels()) {
                return true;
            }
            return false;
        }

        public float getOpacity() {
            if (this.getDiffuse() instanceof ISampleColor4) {
                ISampleColor4 color4Texture = (ISampleColor4) this.getDiffuse();
                return this.opacity * color4Texture.getColor().w;
            }
            return this.opacity;
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

        public ISample getDiffuse() {
            return this.diffuse;
        }
    }
}