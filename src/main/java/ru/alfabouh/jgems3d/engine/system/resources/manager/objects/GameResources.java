package ru.alfabouh.jgems3d.engine.system.resources.manager.objects;

import ru.alfabouh.jgems3d.engine.audio.sound.SoundBuffer;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.utils.ModelLoader;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ICached;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.managers.JGemsLogging;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class GameResources {
    private final ResourceCache resourceCache;
    private final Set<IAssetsLoader> assetsLoaderSet;

    public GameResources(ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
        this.assetsLoaderSet = new TreeSet<>(Comparator.comparingInt(IAssetsLoader::loadOrder));
    }

    public SoundBuffer createSoundBuffer(String soundName, int soundFormat) {
        return SoundBuffer.createSoundBuffer(this.getResourceCache(), soundName, soundFormat);
    }

    public MeshDataGroup createMesh(String modelPath) {
        return ModelLoader.createMesh(this, modelPath);
    }

    public TextureSample createTextureOutsideJar(String fullPath, boolean interpolate, int wrapping) {
        return TextureSample.createTextureOutsideJar(this.getResourceCache(), fullPath, interpolate, wrapping);
    }

    public TextureSample createTexture(String path, boolean interpolate, int wrapping) {
        return TextureSample.createTexture(this.getResourceCache(), path, interpolate, wrapping);
    }

    public TextureSample createTexture(String name, int width, int height, ByteBuffer buffer) {
        return TextureSample.createTexture(this.getResourceCache(), name, width, height, buffer);
    }

    public ICached getResource(String key) {
        return this.getResourceCache().getCachedObject(key);
    }

    public void destroy() {
        this.cleanCache();
        this.getAssetsLoaderSet().clear();
    }

    public void cleanCache() {
        this.getResourceCache().cleanCache();
    }

    public void recreateTexturesInCache() {
        for (TextureSample cached : this.getResourceCache().getAllCachedObjectsCollection(TextureSample.class)) {
            cached.recreateTexture();
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
                        SystemLogging.get().getLogManager().exception(e);
                        JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
                    }
                });
                thread.setDaemon(true);
                set.add(thread);
            }
        }
        return set;
    }

    public void loadResources() {
        SystemLogging.get().getLogManager().log("Loading rendering resources...");
        Set<Thread> threads = this.initAssets();
        threads.forEach(Thread::start);
        List<IAssetsLoader> preLoad = this.getAssetsLoaderSet().stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.PRE).collect(Collectors.toList());
        List<IAssetsLoader> postLoad = this.getAssetsLoaderSet().stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.POST).collect(Collectors.toList());
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException ex) {
                throw new JGemsException(ex);
            }
        });
        for (IAssetsLoader assets : preLoad) {
            assets.load(this);
        }
        for (IAssetsLoader assets : postLoad) {
            assets.load(this);
        }
        SystemLogging.get().getLogManager().log("Rendering resources loaded!");
    }

    public void addAssetsLoaders(IAssetsLoader... a) {
        if (a == null) {
            return;
        }
        this.assetsLoaderSet.addAll(Arrays.asList(a));
    }

    public Set<IAssetsLoader> getAssetsLoaderSet() {
        return this.assetsLoaderSet;
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }
}
