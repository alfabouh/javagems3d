package ru.alfabouh.jgems3d.engine.system.resources;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.SoundBuffer;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.font.GuiFont;
import ru.alfabouh.jgems3d.engine.system.resources.assets.*;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.loader.ShaderLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.utils.ModelLoader;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ICached;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.managers.JGemsLogging;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceManager {
    public static ShaderLoader shaderAssets = null;
    public static TextureAssetsLoader renderAssets = null;
    public static ModelAssetsLoader modelAssets = null;
    public static RenderDataLoader renderDataAssets = null;
    public static SoundAssetsLoader soundAssetsLoader = null;
    private final List<IAssetsLoader> assetsObjects;
    private final ResourceCache ResourceCache;

    public ResourceManager() {
        this.ResourceCache = new ResourceCache();
        this.assetsObjects = new ArrayList<>();
        ResourceManager.shaderAssets = new ShaderLoader();
    }

    public static void loadShaders() {
        ResourceManager.shaderAssets.loadShaders();
        ResourceManager.shaderAssets.startShaders();
    }

    public static Font createFontFromJAR(String font) {
        Font font1;
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, JGems.loadFileJar("/assets/jgems/" + font));
        } catch (FontFormatException | IOException e) {
            throw new JGemsException(e);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font1);
        return font1;
    }

    public static SoundBuffer createSoundBuffer(String soundName, int soundFormat) {
        return SoundBuffer.createSoundBuffer(JGems.get().getResourceManager().getCache(), soundName, soundFormat);
    }

    public static MeshDataGroup createMesh(String modelPath) {
        return ModelLoader.createMesh(JGems.get().getResourceManager().getCache(), modelPath);
    }

    public static TextureSample createTextureOutsideJar(String fullPath, boolean interpolate, int wrapping) {
        return TextureSample.createTextureOutsideJar(JGems.get().getResourceManager().getCache(), fullPath, interpolate, wrapping);
    }

    public static TextureSample createTexture(String path, boolean interpolate, int wrapping) {
        return TextureSample.createTexture(JGems.get().getResourceManager().getCache(), path, interpolate, wrapping);
    }

    public static TextureSample createTexture(String name, int width, int height, ByteBuffer buffer) {
        return TextureSample.createTexture(JGems.get().getResourceManager().getCache(), name, width, height, buffer);
    }

    public static TextureSample createTextureIS(String id, InputStream inputStream, boolean interpolate, int wrapping) {
        return TextureSample.createTextureIS(id, inputStream, interpolate, wrapping);
    }

    public static TextureSample createTextureIS(InputStream inputStream, boolean interpolate, int wrapping) {
        return TextureSample.createTextureIS("#inputstream", inputStream, interpolate, wrapping);
    }

    public static ICached getResource(String key) {
        return JGems.get().getResourceManager().getCache().getCachedObject(key);
    }

    public static TextureSample getTextureResource(String key) {
        return (TextureSample) JGems.get().getResourceManager().getCache().getCachedObject(key);
    }

    public static MeshDataGroup getMeshDataGroupResource(String key) {
        return (MeshDataGroup) JGems.get().getResourceManager().getCache().getCachedObject(key);
    }

    public static void reloadShaders() {
        ResourceManager.shaderAssets.reloadShaders();
    }

    public void init() {
        ResourceManager.renderAssets = new TextureAssetsLoader();
        ResourceManager.modelAssets = new ModelAssetsLoader();
        ResourceManager.renderDataAssets = new RenderDataLoader();
        ResourceManager.soundAssetsLoader = new SoundAssetsLoader();
        this.addAssetLoader(ResourceManager.renderAssets);
        this.addAssetLoader(ResourceManager.modelAssets);
        this.addAssetLoader(ResourceManager.renderDataAssets);
        this.addAssetLoader(ResourceManager.soundAssetsLoader);
    }

    public void destroy() {
        GuiFont.allCreatedFonts.forEach(GuiFont::cleanUp);
        ResourceManager.shaderAssets.destroyShaders();
        this.getCache().cleanCache();
    }

    public void recreateTexturesInCache() {
        for (ICached cached : this.getCache().getCache().values()) {
            if (cached instanceof TextureSample) {
                ((TextureSample) cached).recreateTexture();
            }
        }
    }

    public ResourceCache getCache() {
        return this.ResourceCache;
    }

    public List<IAssetsLoader> getAssetsObjects() {
        return this.assetsObjects;
    }

    private void addAssetLoader(IAssetsLoader asset) {
        this.assetsObjects.add(asset);
    }

    private Set<Thread> initAssets() {
        Set<Thread> set = new HashSet<>();
        for (IAssetsLoader assets : this.assetsObjects) {
            if (assets.loadMode() == IAssetsLoader.LoadMode.PARALLEL) {
                Thread thread = new Thread(() -> {
                    try {
                        assets.load(this.getCache());
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

    public void loadAllAssets() {
        SystemLogging.get().getLogManager().log("Loading rendering resources...");
        this.assetsObjects.sort(Comparator.comparingInt(IAssetsLoader::loadOrder));
        Set<Thread> threads = this.initAssets();
        threads.forEach(Thread::start);
        List<IAssetsLoader> preLoad = this.assetsObjects.stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.PRE).collect(Collectors.toList());
        List<IAssetsLoader> postLoad = this.assetsObjects.stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.POST).collect(Collectors.toList());
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException ex) {
                throw new JGemsException(ex);
            }
        });
        for (IAssetsLoader assets : preLoad) {
            assets.load(this.getCache());
        }
        for (IAssetsLoader assets : postLoad) {
            assets.load(this.getCache());
        }
        SystemLogging.get().getLogManager().log("Rendering resources loaded!");
    }
}
