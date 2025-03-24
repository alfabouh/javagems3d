package javagems3d.system.resources.assets.materials;

import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.service.synchronizing.GPUSyncer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@SuppressWarnings("all")
public class Material {
    private final Transparency transparency;
    private final ISample diffuse;
    private final ISample emission;
    private final ITexture2DProgram specularMetallicMap;
    private final ITexture2DProgram normalsMap;

    public Material(@NotNull ISample diffuse, @Nullable ITexture2DProgram normalsMap, @Nullable ISample emission, @Nullable ITexture2DProgram specularMetallicMap) {
        this.diffuse = diffuse;
        this.normalsMap = normalsMap;
        this.emission = emission;
        this.specularMetallicMap = specularMetallicMap;
        this.transparency = new Transparency(diffuse);
    }

    public Material(@Nullable ISample diffuse) {
        this(diffuse == null ? ResourceManager.DEFAULT_TEXTURE() : diffuse, null, null, null);
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

    public ISample getDiffuse() {
        return this.diffuse;
    }

    public ITexture2DProgram getNormalsMap() {
        return this.normalsMap;
    }

    public ISample getEmission() {
        return this.emission;
    }

    public ITexture2DProgram getSpecularMetallicMap() {
        return this.specularMetallicMap;
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
                computing.performUniformTexture(new UniformString("inputTexture"), imageTexture);
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
            if (this.getDiffuse() instanceof Color4Texture) {
                Color4Texture color4Texture = (Color4Texture) this.getDiffuse();
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