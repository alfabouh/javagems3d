/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.managing.resources;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelMeshLoader;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.loading.samples.TexturesLoader;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.managing.resources.data.ResourcesDataArrays;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.audio.sound.SoundBuffer;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import org.joml.Vector2i;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The GameResources class contains a cache, as well as tools for loading resources
 */
public final class GameResources implements IGameResources {
    private final ResourceCache resourceCache;
    private final Set<IAssetsInitializer> assetsLoaderSet;
    private final ResourcesDataArrays resourcesDataArrays;

    public GameResources(@NotNull ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
        this.resourcesDataArrays = new ResourcesDataArrays(new MeshBuffersDataArray(), new BindlessTexturesDataArray());
        this.assetsLoaderSet = new TreeSet<>(Comparator.comparingInt(e -> ((IAssetsInitializer) e).loadPriority().getPriority()).thenComparingInt(System::identityHashCode));
    }

    public SoundBuffer createSoundBuffer(JGemsPath soundPath, int soundFormat) {
        return SoundBuffer.createSoundBuffer(this.getResourceCache(), soundPath, soundFormat);
    }

    public MeshBuffer createMeshBuffer(@NotNull JGemsPath modelPath, int modelLoadingFlags) {
        return this.loadModel(modelPath, () -> new ModelMeshLoader(this, modelPath).createMeshBuffer(modelLoadingFlags));
    }

    public MeshGroup createMeshGroup(@NotNull JGemsPath modelPath, int modelLoadingFlags) {
        return this.loadModel(modelPath, () -> new ModelMeshLoader(this, modelPath).createMeshGroup(modelLoadingFlags));
    }

    public ImageTexture createTexture(@Nullable ImageTexture returnDefault, @NotNull JGemsPath path, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, path.toString(), () -> new TexturesLoader(this, path.toString()).createImageTexture(textureProperties, path));
    }

    public ImageTexture createTexture(@Nullable ImageTexture returnDefault, @Nullable String name, @NotNull ByteBuffer buffer, @NotNull Vector2i size, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new TexturesLoader(this, name).createImageTexture(textureProperties, new ImageTexture.Data(buffer, size)));
    }

    public ImageTexture createTexture(@Nullable ImageTexture returnDefault, @Nullable String name, @NotNull InputStream stream, @Nullable ImageTexture.Properties textureProperties) {
        return this.loadTexture(returnDefault, name, () -> new TexturesLoader(this, name).createImageTexture(textureProperties, stream));
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture returnDefault, @NotNull String name, @NotNull ImageTexture.Data[] dataSet, @Nullable CubeMapTexture.Properties textureProperties) {
        return this.loadCubeMap(returnDefault, name, () -> new CubeMapsLoader(this, name).createCubeMapTexture(textureProperties, new CubeMapTexture.Data(dataSet)));
    }

    public CubeMapTexture createCubeMapTexture(@Nullable CubeMapTexture returnDefault, @NotNull JGemsPath pathToCubeMapFile, @NotNull String textureDescriptor, @Nullable CubeMapTexture.Properties textureProperties) {
        return this.loadCubeMap(returnDefault, pathToCubeMapFile.toString(), () -> new CubeMapsLoader(this, pathToCubeMapFile.toString()).createCubeMapTexture(textureProperties, pathToCubeMapFile, textureDescriptor));
    }

    private <T> T loadModel(@NotNull JGemsPath modelPath, Supplier<T> modelLoader) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading model: " + modelPath);
        try {
            return modelLoader.get();
        } catch (Exception e) {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, "Error, while loading model: " + modelPath);
            throw e;
        }
    }

    private ImageTexture loadTexture(@Nullable ImageTexture returnDefault, @Nullable String name, Supplier<ImageTexture> textureLoader) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading texture: " + name);
        try {
            return textureLoader.get();
        } catch (Exception e) {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, "Couldn't load: " + name);
            if (returnDefault != null) {
                JGemsHelper.getLogger().error("Couldn't load: " + name + ". Default returned!");
                return returnDefault;
            } else {
                throw new JGemsRuntimeException(e);
            }
        }
    }

    private CubeMapTexture loadCubeMap(@Nullable CubeMapTexture returnDefault, @Nullable String name, Supplier<CubeMapTexture> textureLoader) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading cube map: " + name);
        try {
            return textureLoader.get();
        } catch (Exception e) {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, "Couldn't load: " + name);
            if (returnDefault != null) {
                JGemsHelper.getLogger().error("Couldn't load: " + name + ". Default returned!");
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

    public void destroy() {
        this.clearCache();
        this.getAssetsLoaderSet().clear();
        this.getResourceArrays().clearAll();
    }

    public void clearCache() {
        this.getResourceCache().clearCache();
    }

    public void reloadTexturesInCache() {
        for (ImageTexture cached : this.getResourceCache().getAllCachedObjectsCollection(ImageTexture.class)) {
            cached.reload(null);
        }
    }

    private Set<Thread> initAssets() {
        Set<Thread> set = new HashSet<>();
        for (IAssetsInitializer assets : this.getAssetsLoaderSet()) {
            if (assets.loadMode() == IAssetsInitializer.LaunchMode.ASYNC) {
                Thread thread = new Thread(() -> {
                    try {
                        assets.load(this);
                    } catch (Exception e) {
                        JGemsHelper.getLogger().exception(e);
                    }
                });
                thread.setDaemon(true);
                set.add(thread);
            }
        }
        return set;
    }

    public void loadResources() {
        JGemsHelper.getLogger().log("Loading rendering resources...");
        Set<Thread> threads = this.initAssets();
        threads.forEach(Thread::start);
        List<IAssetsInitializer> normalLoad = this.getAssetsLoaderSet().stream().filter(e -> e.loadMode() == IAssetsInitializer.LaunchMode.REGULAR).collect(Collectors.toList());
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException ex) {
                throw new JGemsRuntimeException(ex);
            }
        });
        for (IAssetsInitializer assets : normalLoad) {
            assets.load(this);
        }
        JGemsHelper.getLogger().log("Rendering resources loaded!");
    }

    public void addAssetsLoaders(IAssetsInitializer... a) {
        if (a == null) {
            throw new JGemsNullException("Caught NULL AssetsLoader!");
        }
        this.assetsLoaderSet.addAll(Arrays.asList(a));
    }

    public void addAssetsLoaders(Collection<IAssetsInitializer> a) {
        if (a == null) {
            throw new JGemsNullException("Caught NULL AssetsLoader Collection!");
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
