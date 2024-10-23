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

package javagems3d.system.resources.manager;

import org.joml.Vector2i;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.audio.sound.SoundBuffer;
import javagems3d.system.resources.assets.loaders.base.IAssetsLoader;
import javagems3d.system.resources.assets.material.samples.CubeMapSample;
import javagems3d.system.resources.assets.material.samples.TextureSample;
import javagems3d.system.resources.assets.material.samples.packs.CubeMapTexturePack;
import javagems3d.system.resources.assets.models.loader.ModelLoader;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The GameResources class contains a cache, as well as tools for loading resources
 */
public class GameResources {
    private final ResourceCache resourceCache;
    private final Set<IAssetsLoader> assetsLoaderSet;

    public GameResources(ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
        this.assetsLoaderSet = new TreeSet<>(Comparator.comparingInt(e -> ((IAssetsLoader) e).loadPriority().getPriority()).thenComparingInt(System::identityHashCode));
    }

    public SoundBuffer createSoundBuffer(JGemsPath soundPath, int soundFormat) {
        return SoundBuffer.createSoundBuffer(this.getResourceCache(), soundPath, soundFormat);
    }

    public MeshGroup createMesh(JGemsPath modelPath, boolean constructCollisionMesh, boolean constructRenderAABB) {
        MeshGroup meshGroup = this.createMesh(modelPath, constructRenderAABB);
        if (constructCollisionMesh) {
            JGemsHelper.UTILS.createMeshCollisionData(meshGroup);
        }
        return meshGroup;
    }

    public MeshGroup createMesh(JGemsPath modelPath, boolean constructRenderAABB) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading model: " + modelPath);
        MeshGroup meshGroup = ModelLoader.createMesh(this, modelPath);
        if (meshGroup == null) {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, "Error, while loading texture: " + modelPath);
        } else {
            if (constructRenderAABB) {
                JGemsHelper.UTILS.createMeshRenderAABBData(meshGroup);
            }
        }
        return meshGroup;
    }

    public TextureSample createTexture(JGemsPath path, TextureSample.Params params) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading texture: " + path);
        try {
            return TextureSample.registerTexture(this.getResourceCache(), path, params);
        } catch (Exception e) {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, "Couldn't load: " + path);
            throw e;
        }
    }

    public CubeMapSample createCubeMap(JGemsPath pathToCubeMap, String type) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading CubeMap: " + pathToCubeMap);
        try {
            return CubeMapSample.createCubeMap(this.getResourceCache(), new CubeMapTexturePack(pathToCubeMap, type));
        } catch (Exception e) {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, "Couldn't load: " + pathToCubeMap);
            throw e;
        }
    }

    public TextureSample createTextureOrDefault(TextureSample defaultT, JGemsPath path, TextureSample.Params params) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading texture: " + path);
        try {
            return TextureSample.registerTexture(this.getResourceCache(), path, params);
        } catch (Exception e) {
            String s = "Couldn't load: " + path + ". Default texture returned!";
            JGemsHelper.getLogger().error(s);
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, s);
            return defaultT;
        }
    }

    public TextureSample createTexture(String name, Vector2i size, ByteBuffer buffer, TextureSample.Params params) {
        try {
            return TextureSample.registerTexture(this.getResourceCache(), name, size, buffer, params);
        } catch (Exception e) {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0xff0000, "Couldn't load: " + name);
            throw e;
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
        this.cleanCache();
        this.getAssetsLoaderSet().clear();
    }

    public void cleanCache() {
        this.getResourceCache().cleanCache();
    }

    public void reloadTexturesInCache() {
        for (TextureSample cached : this.getResourceCache().getAllCachedObjectsCollection(TextureSample.class)) {
            cached.reloadTexture();
        }
    }

    private Set<Thread> initAssets() {
        Set<Thread> set = new HashSet<>();
        for (IAssetsLoader assets : this.getAssetsLoaderSet()) {
            if (assets.loadMode() == IAssetsLoader.LoadMode.PARALLEL) {
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
        List<IAssetsLoader> normalLoad = this.getAssetsLoaderSet().stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.NORMAL).collect(Collectors.toList());
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException ex) {
                throw new JGemsRuntimeException(ex);
            }
        });
        for (IAssetsLoader assets : normalLoad) {
            assets.load(this);
        }
        JGemsHelper.getLogger().log("Rendering resources loaded!");
    }

    public void addAssetsLoaders(IAssetsLoader... a) {
        if (a == null) {
            throw new JGemsNullException("Caught NULL AssetsLoader!");
        }
        this.assetsLoaderSet.addAll(Arrays.asList(a));
    }

    public void addAssetsLoaders(Collection<IAssetsLoader> a) {
        if (a == null) {
            throw new JGemsNullException("Caught NULL AssetsLoader Collection!");
        }
        this.assetsLoaderSet.addAll(a);
    }

    public Set<IAssetsLoader> getAssetsLoaderSet() {
        return this.assetsLoaderSet;
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }
}
