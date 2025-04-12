package javagems3d.system.resources.managing.resources;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelMeshLoader;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.loading.samples.TexturesLoader;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.IPropertiesSample;
import javagems3d.system.resources.assets.texturing.ISample;
import javagems3d.system.resources.managing.resources.data.ResourcesDataArrays;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.JGems3D;
import javagems3d.audio.sound.SoundBuffer;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import org.joml.Vector2i;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The SystemResources class contains a cache, as well as tools for processing resources
 */
public abstract class SystemResources implements ISystemResources {
    private final ResourceCache resourceCache;
    private final Set<IAssetsInitializer> assetsLoaderSet;
    private final ResourcesDataArrays resourcesDataArrays;

    public SystemResources(@NotNull ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
        this.resourcesDataArrays = new ResourcesDataArrays(new MeshBuffersDataArray(), new BindlessTexturesDataArray());
        this.assetsLoaderSet = new TreeSet<>(Comparator.comparingInt(e -> ((IAssetsInitializer) e).loadPriority().getPriority()).thenComparingInt(System::identityHashCode));
    }

    public SoundBuffer createSoundBuffer(JGemsPath soundPath, int soundFormat) {
        return SoundBuffer.createSoundBuffer(this.getResourceCache(), soundPath, soundFormat);
    }

    public MeshBuffer createMeshBuffer(@Nullable MeshCollisionData.Fabric fabric, @NotNull JGemsPath modelPath, boolean keepNodesInMemory, boolean animated) {
        return this.loadModel(modelPath, () -> new ModelMeshLoader(this, modelPath).createMeshBuffer(fabric, keepNodesInMemory, animated));
    }

    public MeshGroup createMeshGroup(@Nullable MeshCollisionData.Fabric fabric, @NotNull JGemsPath modelPath, boolean keepNodesInMemory, boolean animated) {
        return this.loadModel(modelPath, () -> new ModelMeshLoader(this, modelPath).createMeshGroup(fabric, false, keepNodesInMemory, animated));
    }

    public MeshGroup createMeshGroup_Buffer(@Nullable MeshCollisionData.Fabric fabric, @NotNull JGemsPath modelPath, boolean keepNodesInMemory, boolean animated) {
        return this.loadModel(modelPath, () -> new ModelMeshLoader(this, modelPath).createMeshGroup(fabric, true, keepNodesInMemory, animated));
    }

    public MeshBuffer createMeshBuffer(@NotNull JGemsPath modelPath, boolean keepNodesInMemory, boolean animated) {
        return this.createMeshBuffer(null, modelPath, keepNodesInMemory, animated);
    }

    public MeshGroup createMeshGroup(JGemsPath modelPath, boolean keepNodesInMemory, boolean animated) {
        return this.createMeshGroup(null, modelPath, keepNodesInMemory, animated);
    }

    public MeshGroup createMeshGroup_Buffer(@NotNull JGemsPath modelPath, boolean keepNodesInMemory, boolean animated) {
        return this.createMeshGroup_Buffer(null, modelPath, keepNodesInMemory, animated);
    }

    public ITexture2DProgram createTexture(@Nullable ITexture2DProgram returnDefault, @NotNull JGemsPath path, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, path.toString(), () -> new TexturesLoader(this, path.toString()).createImageTexture(textureProperties, path));
    }

    public ITexture2DProgram createTexture(@Nullable ITexture2DProgram returnDefault, @Nullable String name, @NotNull ByteBuffer buffer, @NotNull Vector2i size, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new TexturesLoader(this, name).createImageTexture(textureProperties, new ImageTexture.Data(buffer, size)));
    }

    public ITexture2DProgram createTexture(@Nullable ITexture2DProgram returnDefault, @Nullable String name, @NotNull InputStream stream, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new TexturesLoader(this, name).createImageTexture(textureProperties, stream));
    }

    public ICubeMapProgram createCubeMapTexture(@Nullable ICubeMapProgram returnDefault, @NotNull String name, @NotNull ImageTexture.Data[] dataSet, @Nullable CubeMapTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new CubeMapsLoader(this, name).createCubeMapTexture(textureProperties, new CubeMapTexture.Data(dataSet)));
    }

    public ICubeMapProgram createCubeMapTexture(@Nullable ICubeMapProgram returnDefault, @NotNull JGemsPath pathToCubeMapFile, @NotNull String textureDescriptor, @Nullable CubeMapTexture.Properties textureProperties) {
        JGemsPath path = new JGemsPath(pathToCubeMapFile, "sky_");
        return this.loadTexture(returnDefault, path.toString(), () -> new CubeMapsLoader(this, path.toString()).createCubeMapTexture(textureProperties, path, textureDescriptor));
    }

    protected abstract @Nullable Consumer<Pair<String, Integer>> getMessagesConsumer();

    public void processMessage(String text, int color) {
        if (this.getMessagesConsumer() != null) {
            this.getMessagesConsumer().accept(new Pair<>(text, color));
        }
    }

    private <T> T loadModel(@NotNull JGemsPath modelPath, Supplier<T> modelLoader) {
        this.processMessage("Loading model: " + modelPath, 0xffffff);
        try {
            T t = modelLoader.get();
            this.processMessage("Successfully loaded model", 0x00ff00);
            return t;
        } catch (Exception e) {
            this.processMessage("Error, while processing model: " + modelPath, 0xff0000);
            throw e;
        }
    }

    private <T extends ITextureProgram> T loadTexture(@Nullable T returnDefault, @Nullable String name, Supplier<T> textureLoader) {
        this.processMessage("Loading texture: " + name, 0xffffff);
        try {
            T t = textureLoader.get();
            this.processMessage("Successfully loaded texture", 0x00ff00);
            return t;
        } catch (Exception e) {
            this.processMessage("Error, while processing texture: " + name + ". Default returned", 0xff0000);
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

    public static Font createFontFromJAR(JGemsPath path) {
        Font font1;
        try {
            try (InputStream inputStream = JGems3D.loadFileFromJar(path)) {
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
        this.getResourceArrays().clearAll();
    }

    public void clearCache() {
        this.getResourceCache().clearCache();
        System.gc();
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
        })).collect(Collectors.toList());

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
}
