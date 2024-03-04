package ru.BouH.engine.game.resources;

import ru.BouH.engine.audio.sound.SoundBuffer;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.*;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.assets.utils.ModelLoader;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.game.resources.cache.ICached;
import ru.BouH.engine.render.screen.window.Window;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceManager {
    public static TextureAssetsLoader renderAssets = null;
    public static ShaderAssetsLoader shaderAssets = null;
    public static ModelAssetsLoader modelAssets = null;
    public static RenderDataLoader renderDataAssets = null;
    public static SoundAssetsLoader soundAssetsLoader = null;
    private final List<IAssetsLoader> assetsObjects;
    private final GameCache gameCache;

    public ResourceManager() {
        this.gameCache = new GameCache();
        this.assetsObjects = new ArrayList<>();
    }

    public static SoundBuffer createSoundBuffer(String soundName, int soundFormat) {
        return SoundBuffer.createSoundBuffer(Game.getGame().getResourceManager().getGameCache(), soundName, soundFormat);
    }

    public static MeshDataGroup createMesh(String modelPath, String modelName) {
        return ModelLoader.createMesh(Game.getGame().getResourceManager().getGameCache(), modelPath, modelName);
    }

    public static TextureSample createTextureOutsideJar(String fullPath) {
        return TextureSample.createTextureOutsideJar(Game.getGame().getResourceManager().getGameCache(), fullPath);
    }

    public static TextureSample createTexture(String path) {
        return TextureSample.createTexture(Game.getGame().getResourceManager().getGameCache(), path);
    }

    public static TextureSample createTextureIS(String id, InputStream inputStream) {
        return TextureSample.createTextureIS(id, inputStream);
    }

    public static TextureSample createTextureIS(InputStream inputStream) {
        return TextureSample.createTextureIS("#inputstream", inputStream);
    }

    public static ICached getResource(String key) {
        return Game.getGame().getResourceManager().getGameCache().getCachedObject(key);
    }

    public static TextureSample getTextureResource(String key) {
        return Game.getGame().getResourceManager().getGameCache().getCachedTexture(key);
    }

    public static MeshDataGroup getMeshDataGroupResource(String key) {
        return Game.getGame().getResourceManager().getGameCache().getCachedMeshDataGroup(key);
    }

    public void init() {
        ResourceManager.renderAssets = new TextureAssetsLoader();
        ResourceManager.shaderAssets = new ShaderAssetsLoader();
        ResourceManager.modelAssets = new ModelAssetsLoader();
        ResourceManager.renderDataAssets = new RenderDataLoader();
        ResourceManager.soundAssetsLoader = new SoundAssetsLoader();
        this.addAssetLoader(ResourceManager.renderAssets);
        this.addAssetLoader(ResourceManager.shaderAssets);
        this.addAssetLoader(ResourceManager.modelAssets);
        this.addAssetLoader(ResourceManager.renderDataAssets);
        this.addAssetLoader(ResourceManager.soundAssetsLoader);
    }

    public void destroy() {
        ResourceManager.shaderAssets.destroyShaders();
        this.getGameCache().cleanCache();
    }

    public GameCache getGameCache() {
        return this.gameCache;
    }

    public List<IAssetsLoader> getAssetsObjects() {
        return this.assetsObjects;
    }

    private void addAssetLoader(IAssetsLoader asset) {
        this.assetsObjects.add(asset);
    }

    private Set<Thread> initAssets() {
        Set<Thread> set = new HashSet<>();
        Iterator<IAssetsLoader> assetsIterator = this.assetsObjects.iterator();
        while (assetsIterator.hasNext()) {
            IAssetsLoader assets = assetsIterator.next();
            if (assets.loadMode() == IAssetsLoader.LoadMode.PARALLEL) {
                Thread thread = new Thread(() -> assets.load(this.getGameCache()));
                set.add(thread);
                assetsIterator.remove();
            }
        }
        return set;
    }

    public void loadAllAssets() {
        this.assetsObjects.sort(Comparator.comparingInt(IAssetsLoader::loadOrder));
        Set<Thread> threads = this.initAssets();
        threads.forEach(Thread::start);
        List<IAssetsLoader> preLoad = this.assetsObjects.stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.PRE).collect(Collectors.toList());
        List<IAssetsLoader> postLoad = this.assetsObjects.stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.POST).collect(Collectors.toList());
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        for (IAssetsLoader assets : preLoad) {
            assets.load(this.getGameCache());
        }
        for (IAssetsLoader assets : postLoad) {
            assets.load(this.getGameCache());
        }
    }
}
