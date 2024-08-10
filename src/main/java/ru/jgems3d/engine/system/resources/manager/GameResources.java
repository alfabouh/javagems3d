package ru.jgems3d.engine.system.resources.manager;

import org.joml.Vector2i;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.audio.sound.SoundBuffer;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.exceptions.JGemsException;
import ru.jgems3d.engine.system.service.exceptions.JGemsNullException;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine.system.service.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.utils.ModelLoader;
import ru.jgems3d.engine.system.resources.cache.ICached;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class GameResources {
    private final ResourceCache resourceCache;
    private final Set<IAssetsLoader> assetsLoaderSet;

    public GameResources(ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
        this.assetsLoaderSet = new TreeSet<>(Comparator.comparingInt(e -> ((IAssetsLoader) e).loadPriority().getPriority()).thenComparingInt(System::identityHashCode));
    }

    public SoundBuffer createSoundBuffer(JGPath soundPath, int soundFormat) {
        return SoundBuffer.createSoundBuffer(this.getResourceCache(), soundPath, soundFormat);
    }

    public MeshDataGroup createMesh(JGPath modelPath, boolean constructCollisionMesh) {
        MeshDataGroup meshDataGroup = this.createMesh(modelPath);
        if (constructCollisionMesh) {
            JGemsHelper.UTILS.createMeshCollisionData(meshDataGroup);
        }
        return meshDataGroup;
    }

    public MeshDataGroup createMesh(JGPath modelPath) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen("Loading model: " + modelPath);
        return ModelLoader.createMesh(this, modelPath);
    }

    public TextureSample createTextureOutsideJar(JGPath path, TextureSample.Params params) {
        return TextureSample.createTextureOutsideJar(this.getResourceCache(), path, params);
    }

    public TextureSample createTexture(JGPath path, TextureSample.Params params) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen("Loading texture: " + path);
        return TextureSample.createTexture(this.getResourceCache(), path, params);
    }

    public TextureSample createTextureOrDefault(TextureSample defaultT, JGPath path, TextureSample.Params params) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen("Loading texture: " + path);
        try {
            return TextureSample.createTexture(this.getResourceCache(), path, params);
        } catch (JGemsException e) {
            e.printStackTrace(System.err);
            return defaultT;
        }
    }

    public TextureSample createTexture(String name, Vector2i size, ByteBuffer buffer, TextureSample.Params params) {
        return TextureSample.createTexture(this.getResourceCache(), name, size, buffer, params);
    }

    @SuppressWarnings("all")
    public <S extends ICached> S getResource(JGPath key) {
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
