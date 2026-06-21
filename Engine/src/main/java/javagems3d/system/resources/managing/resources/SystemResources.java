package javagems3d.system.resources.managing.resources;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.gltf.GLTF2ModelLoader;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.loading.samples.TexturesLoader;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.IPropertiesSample;
import javagems3d.system.resources.assets.texturing.ISample;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.data.ResourcesDataArrays;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.JGems3D;
import javagems3d.audio.SoundBuffer;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.JGemsPath;
import org.joml.Vector2i;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SystemResources implements ISystemResources {
    private final ResourceCache resourceCache;
    private final Set<IAssetsInitializer> assetsLoaderSet;
    private final ResourcesDataArrays resourcesDataArrays;

    public enum ResLoadSysMessageType {
        LOG,
        ERR,
        WARN
    }

    public SystemResources(@NotNull ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
        this.resourcesDataArrays = new ResourcesDataArrays(new MeshBuffersDataArray(), new BindlessTexturesDataArray());
        this.assetsLoaderSet = new TreeSet<>(Comparator.comparingInt(e -> ((IAssetsInitializer) e).loadPriority().getPriority()).thenComparingInt(System::identityHashCode));
    }

    public SoundBuffer createSoundBuffer(@NotNull JGemsPathSource soundPath, int soundFormat) {
        return SoundBuffer.createSoundBuffer(soundPath, this.getResourceCache(), soundFormat);
    }

    public MeshBuffer createMeshBuffer(JGemsPathSource modelPath, @Nullable MeshCollisionData.Fabric fabric, boolean keepMeshInMemoryAfterProcessing) {
        try {
            if (modelPath == null) {
                throw new JGemsNullException("Null model");
            }
            return this.loadModel(modelPath, () -> new GLTF2ModelLoader(modelPath, this).createMeshBuffer(fabric, keepMeshInMemoryAfterProcessing));
        } catch (Exception e) {
            Log.get().exception(e);
            Log.get().error("Returned default model");
            return ResourceManager.DEFAULT_CUBE_MESHBUFFER();
        }
    }

    public MeshGroup createMeshGroup(JGemsPathSource modelPath, @Nullable MeshCollisionData.Fabric fabric, boolean keepMeshInMemoryAfterProcessing, boolean buildSubMeshesAABBs) {
        try {
            if (modelPath == null) {
                throw new JGemsNullException("Null model");
            }
            return this.loadModel(modelPath, () -> new GLTF2ModelLoader(modelPath, this).createMeshGroup(fabric, keepMeshInMemoryAfterProcessing, buildSubMeshesAABBs));
        } catch (Exception e) {
            Log.get().exception(e);
            Log.get().error("Returned default model");
            return ResourceManager.DEFAULT_CUBE_MESHGROUP();
        }
    }

    public MeshBuffer createMeshBuffer(JGemsPathSource modelPath, boolean keepMeshInMemoryAfterProcessing) {
        try {
            if (modelPath == null) {
                throw new JGemsNullException("Null model");
            }
            return this.createMeshBuffer(modelPath, null, keepMeshInMemoryAfterProcessing);
        } catch (Exception e) {
            Log.get().exception(e);
            Log.get().error("Returned default model");
            return ResourceManager.DEFAULT_CUBE_MESHBUFFER();
        }
    }

    public MeshGroup createMeshGroup(JGemsPathSource modelPath, boolean keepMeshInMemoryAfterProcessing, boolean buildSubMeshesAABBs) {
        try {
            if (modelPath == null) {
                throw new JGemsNullException("Null model");
            }
            return this.createMeshGroup(modelPath, null, keepMeshInMemoryAfterProcessing, buildSubMeshesAABBs);
        } catch (Exception e) {
            Log.get().exception(e);
            Log.get().error("Returned default model");
            return ResourceManager.DEFAULT_CUBE_MESHGROUP();
        }
    }

    public ITexture2DProgram createTexture(@NotNull JGemsPathSource path, @Nullable ITexture2DProgram returnDefault, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, path.toString(), () -> new TexturesLoader(this, path.toString()).createImageTexture(textureProperties, path));
    }

    public ITexture2DProgram createTexture(@Nullable ITexture2DProgram returnDefault, @Nullable String name, @NotNull ByteBuffer buffer, @NotNull Vector2i size, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new TexturesLoader(this, name).createImageTexture(textureProperties, new ImageTexture.Data(buffer, size)));
    }

    public ITexture2DProgram createTexture(@Nullable ITexture2DProgram returnDefault, @Nullable String name, @NotNull ByteBuffer buffer, @Nullable ImageTexture.Properties textureProperties) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            if (!STBImage.stbi_info_from_memory(buffer, w, h, comp)) {
                throw new JGemsIOException("Failed to read image info: " + STBImage.stbi_failure_reason());
            }
            return this.loadTexture(returnDefault, name, () -> new TexturesLoader(this, name).createImageTexture(textureProperties, new ImageTexture.Data(buffer, new Vector2i(w.get(0), h.get(0)))));
        }
    }

    public ITexture2DProgram createTexture(@Nullable ITexture2DProgram returnDefault, @Nullable String name, @NotNull InputStream stream, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new TexturesLoader(this, name).createImageTexture(textureProperties, stream));
    }

    public ICubeMapProgram createCubeMapTexture(@Nullable ICubeMapProgram returnDefault, @NotNull String name, @NotNull ImageTexture.Data[] dataSet, @Nullable CubeMapTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new CubeMapsLoader(this, name).createCubeMapTexture(textureProperties, new CubeMapTexture.Data(dataSet)));
    }

    public ICubeMapProgram createCubeMapTexture(@Nullable ICubeMapProgram returnDefault, @NotNull CubeMapsLoader.CubeMapTexturesContainer cubeMapTexturesContainer, @Nullable CubeMapTexture.Properties textureProperties) {
        final String name = cubeMapTexturesContainer.getTextures()[0].toString();
        return this.loadTexture(returnDefault, name, () -> new CubeMapsLoader(this, name).createCubeMapTexture(textureProperties, cubeMapTexturesContainer));
    }

    protected abstract @Nullable Consumer<ResLoadSysMessage> getMessagesConsumer();

    public void processMessage(@NotNull String text, int color, @NotNull ResLoadSysMessageType resLoadSysMessageType) {
        if (this.getMessagesConsumer() != null) {
            this.getMessagesConsumer().accept(new ResLoadSysMessage(color, text, resLoadSysMessageType));
        }
    }

    private <T> T loadModel(@NotNull JGemsPathSource modelPath, Supplier<T> modelLoader) {
        this.processMessage("Loading model: " + modelPath, 0xffffff, ResLoadSysMessageType.LOG);
        try {
            T t = modelLoader.get();
            this.processMessage("Successfully loaded model", 0x00ff00, ResLoadSysMessageType.LOG);
            return t;
        } catch (Exception e) {
            this.processMessage("Error, while processing model: " + modelPath, 0xff0000, ResLoadSysMessageType.ERR);
            throw e;
        }
    }

    private <T extends ITextureProgram> T loadTexture(@Nullable T returnDefault, @Nullable String name, Supplier<T> textureLoader) {
        this.processMessage("Loading texture: " + name, 0xffffff, ResLoadSysMessageType.LOG);
        try {
            T t = textureLoader.get();
            this.processMessage("Successfully loaded texture", 0x00ff00, ResLoadSysMessageType.LOG);
            return t;
        } catch (Exception e) {
            this.processMessage("Error, while processing texture: " + name + ". Default returned", 0xff0000, ResLoadSysMessageType.ERR);
            if (returnDefault != null) {
                return returnDefault;
            } else {
                throw new JGemsRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("all")
    public <S extends ICached> S getResource(JGemsPath key) {
        return (S) this.getResourceCache().getCachedObject(key);
    }

    @SuppressWarnings("all")
    public <S extends ICached> S getResource(String key) {
        return (S) this.getResourceCache().getCachedObject(key);
    }

    @SuppressWarnings("all")
    public <S extends ICached> S getResourceOrDefault(JGemsPath key, @NotNull S defaultO) {
        final S s = (S) this.getResourceCache().getCachedObject(key);
        if (s == null) {
            Log.get().warn("Couldn't get form cache " + key + ". Returned dafault!");
            return defaultO;
        }
        return s;
    }

    @SuppressWarnings("all")
    public <S extends ICached> S getResourceOrDefault(String key, @NotNull S defaultO) {
        final S s = (S) this.getResourceCache().getCachedObject(key);
        if (s == null) {
            Log.get().warn("Couldn't get form cache " + key + ". Returned dafault!");
            return defaultO;
        }
        return s;
    }

    public static Font createFontFromFile(@NotNull JGemsPathSource path) {
        Font font1;
        try {
            try (InputStream inputStream = JGems3D.getInputStream(path)) {
                font1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            }
        } catch (FontFormatException | IOException e) {
            throw new JGemsIOException(e);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font1);
        return font1;
    }

    public void destroy() {
        this.clearCache();
        this.getAssetsLoaderSet().clear();
    }

    public void clearCache() {
        this.getResourceArrays().clearAll();
        this.getResourceCache().clearCache();
        JGems3D.GC();
    }

    public void reloadSamplesInCache(@Nullable Function<ISample.IProperties, ISample.IProperties> processProperties, boolean updateProperties) {
        for (IPropertiesSample cached : this.getResourceCache().getAllCachedObjectsCollection(IPropertiesSample.class)) {
            cached.reload(processProperties == null ? null : processProperties.apply(cached.getProperties()), updateProperties);
        }
    }

    public void loadResources() {
        List<Thread> threads = this.getAssetsLoaderSet().stream().filter(assets -> assets.loadMode() == IAssetsInitializer.LaunchMode.ASYNC).map(assets -> new Thread(() -> {
            try {
                assets.load(this);
            } catch (Exception e) {
                Log.get().exception(e);
            }
        })).toList();

        threads.forEach(Thread::start);
        this.getAssetsLoaderSet().stream().filter(assets -> assets.loadMode() == IAssetsInitializer.LaunchMode.REGULAR).sorted(Comparator.comparingInt(e -> e.loadPriority().getPriority())).forEach(assets -> assets.load(this));
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new JGemsRuntimeException(e);
            }
        }
        Log.get().info("Initialized rendering resources " + this.getResourceCache());
        JGems3D.GC();
    }

    public void addAssetsLoaders(IAssetsInitializer... a) {
        if (a == null) {
            throw new JGemsNullException("Caught NULL AssetsLoader");
        }
        this.assetsLoaderSet.addAll(Arrays.asList(a));
    }

    public void addAssetsLoaders(Collection<IAssetsInitializer> a) {
        if (a == null) {
            throw new JGemsNullException("Caught NULL AssetsLoader Collection");
        }
        this.assetsLoaderSet.addAll(a);
    }

    public Set<IAssetsInitializer> getAssetsLoaderSet() {
        return this.assetsLoaderSet;
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }

    @Override
    public ResourcesDataArrays getResourceArrays() {
        return this.resourcesDataArrays;
    }

    public record ResLoadSysMessage(int color, String text, ResLoadSysMessageType resLoadSysMessageType) {
        public ResLoadSysMessage(int color, @NotNull String text, @NotNull ResLoadSysMessageType resLoadSysMessageType) {
            this.color = color;
            this.text = text;
            this.resLoadSysMessageType = resLoadSysMessageType;
        }
    }
}
