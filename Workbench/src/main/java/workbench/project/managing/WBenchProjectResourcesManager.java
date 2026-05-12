package workbench.project.managing;

import com.google.gson.reflect.TypeToken;
import javagems3d.JGems3D;
import javagems3d.audio.sound.SoundBuffer;
import javagems3d.audio.sound.data.SoundType;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.external.gaming.JGemsGaming;
import javagems3d.system.external.gaming.def.misc.*;
import javagems3d.system.external.gaming.def.world.GameResourceMarkerObjectAsset;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.VirtualObjectsFolder;
import javagems3d.system.service.files.json.JSONFileManaging;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL10;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import workbench.project.managing.instances.WBenchResourceMapAsset;
import javagems3d.system.external.gaming.def.world.GameResourceEntityObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;
import workbench.project.map.WBenchMapProject;
import workbench.resources.WBenchResourceManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class WBenchProjectResourcesManager {
    private final SystemResources systemResources;
    private GameResourceAssetsFolder<GameResourceModelAsset> modelAssetsFolder;
    private GameResourceAssetsFolder<GameResourceTextureAsset> textureAssetsFolder;
    private GameResourceAssetsFolder<GameResourceSoundAsset> soundAssetsFolder;
    private GameResourceAssetsFolder<WBenchResourceMapAsset> mapAssetsFolder;
    private GameResourceAssetsFolder<GameResourceScriptAsset> scriptAssetsFolder;
    private GameResourceAssetsFolder<GameResourcePropObjectAsset> propAssetsFolder;
    private GameResourceAssetsFolder<GameResourceEntityObjectAsset> entityAssetsFolder;
    private GameResourceAssetsFolder<GameResourceMarkerObjectAsset> markerAssetsFolder;
    private GameResourceAssetsFolder<GameResourceObjectTagData> tagAssetsFolder;
    private GameResourceAssetsFolder<GameResourceSkyboxAsset> skyBoxesAssetsFolder;

    private final Map<String, GameResourceModelAsset> modelsKeysCache;
    private final Map<String, GameResourceTextureAsset> texturesKeysCache;
    private final Map<String, GameResourceSoundAsset> soundKeysCache;

    public enum AssetsTarget {
        ALL,
        PROPS,
        MARKERS,
        ENTITIES,
        TAGS,
        SCRIPTS,
        SKYBOXES
    }

    public WBenchProjectResourcesManager(SystemResources systemResources) {
        this.systemResources = systemResources;
        this.modelsKeysCache = new HashMap<>();
        this.soundKeysCache = new HashMap<>();
        this.texturesKeysCache = new HashMap<>();
    }

    public @Nullable GameResourceModelAsset extractFromCacheModel(String relativePath) {
        return this.modelsKeysCache.get(relativePath);
    }

    public @Nullable GameResourceTextureAsset extractFromCacheTexture(String relativePath) {
        return this.texturesKeysCache.get(relativePath);
    }

    public @Nullable GameResourceSoundAsset extractFromCacheSound(String relativePath) {
        return this.soundKeysCache.get(relativePath);
    }

    public static void openSoundsFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(JGemsGaming.getSoundsFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public static void openTexturesFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(JGemsGaming.getTexturesFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public static void openModelsFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(JGemsGaming.getModelsFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public static void openMapsFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(JGemsGaming.getMapsFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public static void openScriptsFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(JGemsGaming.getScriptsFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public File[] createSystemFolders(File file) {
        File dir1 = new File(file, JGemsGaming.SYS_ASSETS_FOLDER);
        File dir2 = new File(file, JGemsGaming.SYS_MAPS_FOLDER);
        File dir3 = new File(file, JGemsGaming.SYS_SCRIPTS_FOLDER);
        {
            for (String s : new String[]{JGemsGaming.MODEL_ASSETS_FOLDER, JGemsGaming.TEXTURE_ASSETS_FOLDER, JGemsGaming.OBJECT_ASSETS_FOLDER, JGemsGaming.SOUND_ASSETS_FOLDER}) {
                File inFile = new File(dir1, s);
                if (!inFile.exists()) {
                    if (inFile.mkdirs()) {
                        Log.get().debug("Created folder: " + inFile.getPath());
                    }
                }
            }
        }
        {
            if (!dir2.exists()) {
                if (dir2.mkdirs()) {
                    Log.get().debug("Created folder: " + dir2.getPath());
                }
            }
        }
        {
            if (!dir3.exists()) {
                if (dir3.mkdirs()) {
                    Log.get().debug("Created folder: " + dir3.getPath());
                }
            }
        }
        return new File[]{dir1, dir2, dir3};
    }

    public void refreshSounds(JGemsPath pathToGameFolder) {
        {
            this.soundKeysCache.values().forEach(e -> {
                this.systemResources.getResourceCache().clearObjectFromCache(new JGemsPath(JGemsGaming.getSoundsFolder(pathToGameFolder), e.relativePath()));
            });
            this.soundKeysCache.clear();
        }
        this.createSystemFolders(pathToGameFolder.toFile());
        this.soundAssetsFolder = this.readSoundsFolder(JGemsGaming.getSoundsFolder(pathToGameFolder));
    }

    public void refreshTextures(JGemsPath pathToGameFolder) {
        {
            this.texturesKeysCache.values().forEach(e -> {
                this.systemResources.getResourceCache().clearObjectFromCache(new JGemsPath(JGemsGaming.getTexturesFolder(pathToGameFolder), e.relativePath()));
            });
            this.texturesKeysCache.clear();
        }
        this.createSystemFolders(pathToGameFolder.toFile());
        this.textureAssetsFolder = this.readTexturesFolder(JGemsGaming.getTexturesFolder(pathToGameFolder));
    }

    public void refreshModels(JGemsPath pathToGameFolder) {
        {
            this.modelsKeysCache.values().forEach(e -> {
                this.systemResources.getResourceCache().clearObjectFromCache(new JGemsPath(JGemsGaming.getModelsFolder(pathToGameFolder), e.relativePath() + MeshGroup.POSTFIX));
            });
            this.modelsKeysCache.clear();
        }
        this.createSystemFolders(pathToGameFolder.toFile());
        this.modelAssetsFolder = this.readModelsFolder(JGemsGaming.getModelsFolder(pathToGameFolder));

        {
            VirtualObjectsFolder<GameResourceModelAsset> defaultModels = new VirtualObjectsFolder<>("defaults");
            GameResourceModelAsset model1 = new GameResourceModelAsset("DefaultPoint", "/defaults/DefaultPoint", WBenchResourceManager.gameEditorModelAssets.markerCube);
            GameResourceModelAsset model2 = new GameResourceModelAsset("DefaultCursor", "/defaults/DefaultCursor", WBenchResourceManager.gameEditorModelAssets.markerCursor);
            GameResourceModelAsset model3 = new GameResourceModelAsset("DefaultAABB", "/defaults/DefaultAABB", WBenchResourceManager.gameEditorModelAssets.markerAabb);
            defaultModels.putObjectThere(model1);
            defaultModels.putObjectThere(model2);
            defaultModels.putObjectThere(model3);
            this.modelsKeysCache.put("/defaults/DefaultPoint", model1);
            this.modelsKeysCache.put("/defaults/DefaultCursor", model2);
            this.modelsKeysCache.put("/defaults/DefaultAABB", model3);
            this.modelAssetsFolder.putFolderThere(defaultModels);
        }
    }

    public void refreshMaps(JGemsPath pathToGameFolder) {
        this.createSystemFolders(pathToGameFolder.toFile());
        this.mapAssetsFolder = this.readMapsFolder(JGemsGaming.getMapsFolder(pathToGameFolder));
    }

    public void refreshScripts(JGemsPath pathToGameFolder) {
        this.createSystemFolders(pathToGameFolder.toFile());
        this.scriptAssetsFolder = this.readScriptsFolder(JGemsGaming.getScriptsFolder(pathToGameFolder));
    }

    public void saveCreatableResourceObjects(@NotNull AssetsTarget assetsTarget, JGemsPath folder) {
        if (assetsTarget.equals(AssetsTarget.SKYBOXES) || assetsTarget.equals(AssetsTarget.ALL)) {
            JSONFileManaging.createSerializationRules().writeToFile(this.getSkyBoxesAssetsFolder(), new JGemsPath(JGemsGaming.getEnvironmentFolder(folder), "_skyBoxes.json").toFile(), null);
        }
        if (assetsTarget.equals(AssetsTarget.PROPS) || assetsTarget.equals(AssetsTarget.ALL)) {
            TagsContainer.createJSONFileManaging().writeToFile(this.getPropAssetsFolder(), new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_props.json").toFile(), null);
        }
        if (assetsTarget.equals(AssetsTarget.ENTITIES) || assetsTarget.equals(AssetsTarget.ALL)) {
            TagsContainer.createJSONFileManaging().writeToFile(this.getEntityAssetsFolder(), new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_entities.json").toFile(), null);
        }
        if (assetsTarget.equals(AssetsTarget.MARKERS) || assetsTarget.equals(AssetsTarget.ALL)) {
            TagsContainer.createJSONFileManaging().writeToFile(this.getMarkerAssetsFolder(), new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_markers.json").toFile(), null);
        }
        if (assetsTarget.equals(AssetsTarget.TAGS) || assetsTarget.equals(AssetsTarget.ALL)) {
            TagsContainer.createJSONFileManaging().writeToFile(this.getTagAssetsFolder(), new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_tags.json").toFile(), null);
        }
    }

    public void readCreatableResourceObjects(@NotNull AssetsTarget assetsTarget, JGemsPath folder) {
        if (assetsTarget.equals(AssetsTarget.PROPS) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.propAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_props.json").toFile(), new TypeToken<>() {
                }, null);
                this.propAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.propAssetsFolder = new GameResourceAssetsFolder<>(VirtualObjectsFolder.DEF_PATH);
                Log.get().exception(e);
                Log.get().info("CREATED DEFAULT!!!");
            }
        }
        if (assetsTarget.equals(AssetsTarget.ENTITIES) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.entityAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_entities.json").toFile(), new TypeToken<>() {
                }, null);
                this.entityAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.entityAssetsFolder = new GameResourceAssetsFolder<>(VirtualObjectsFolder.DEF_PATH);
                Log.get().exception(e);
                Log.get().info("CREATED DEFAULT!!!");
            }
        }
        if (assetsTarget.equals(AssetsTarget.MARKERS) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.markerAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_markers.json").toFile(), new TypeToken<>() {
                }, null);
                this.markerAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.markerAssetsFolder = new GameResourceAssetsFolder<>(VirtualObjectsFolder.DEF_PATH);
                Log.get().exception(e);
                Log.get().info("CREATED DEFAULT!!!");
            }
        }
        if (assetsTarget.equals(AssetsTarget.TAGS) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.tagAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(JGemsGaming.getObjectsFolder(folder), "_tags.json").toFile(), new TypeToken<>() {
                }, null);
                this.tagAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.tagAssetsFolder = new GameResourceAssetsFolder<>(VirtualObjectsFolder.DEF_PATH);
                Log.get().exception(e);
                Log.get().info("CREATED DEFAULT!!!");
            }
        }
        if (assetsTarget.equals(AssetsTarget.SKYBOXES) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.skyBoxesAssetsFolder = JSONFileManaging.createSerializationRules().readFromFile(new JGemsPath(JGemsGaming.getEnvironmentFolder(folder), "_skyBoxes.json").toFile(), new TypeToken<>() {
                }, null);
                this.skyBoxesAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.skyBoxesAssetsFolder = new GameResourceAssetsFolder<>(VirtualObjectsFolder.DEF_PATH);
                Log.get().exception(e);
                Log.get().info("CREATED DEFAULT!!!");
            }
        }
    }

    protected GameResourceAssetsFolder<GameResourceTextureAsset> readTexturesFolder(JGemsPath folder) {
        return this.readTexturesFolder(folder.toFile());
    }

    protected GameResourceAssetsFolder<GameResourceTextureAsset> readTexturesFolder(File rootFile) {
        return this.readTexturesFolderRecursive(rootFile, rootFile);
    }

    protected GameResourceAssetsFolder<GameResourceTextureAsset> readTexturesFolderRecursive(File rootFolder, File relativeFolder) {
        GameResourceAssetsFolder<GameResourceTextureAsset> assetsFolder = new GameResourceAssetsFolder<>(relativeFolder.getName());
        File[] files = relativeFolder.listFiles();
        if (files == null) {
            return assetsFolder;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.putFolderThere(this.readTexturesFolderRecursive(rootFolder, file));
            } else if (this.isTextureFile(file)) {
                GameResourceTextureAsset asset = this.loadTextureAsset(rootFolder, file);
                if (asset != null) {
                    assetsFolder.putObjectThere(asset);
                }
            }
        }
        return assetsFolder;
    }

    protected GameResourceAssetsFolder<GameResourceSoundAsset> readSoundsFolder(JGemsPath folder) {
        return this.readSoundsFolder(folder.toFile());
    }

    protected GameResourceAssetsFolder<GameResourceSoundAsset> readSoundsFolder(File rootFile) {
        return this.readSoundsFolderRecursive(rootFile, rootFile);
    }

    protected GameResourceAssetsFolder<GameResourceSoundAsset> readSoundsFolderRecursive(File rootFolder, File relativeFolder) {
        GameResourceAssetsFolder<GameResourceSoundAsset> assetsFolder = new GameResourceAssetsFolder<>(relativeFolder.getName());
        File[] files = relativeFolder.listFiles();
        if (files == null) {
            return assetsFolder;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.putFolderThere(this.readSoundsFolderRecursive(rootFolder, file));
            } else if (this.isSoundFile(file)) {
                GameResourceSoundAsset asset = this.loadSoundAsset(rootFolder, file);
                if (asset != null) {
                    assetsFolder.putObjectThere(asset);
                }
            }
        }
        return assetsFolder;
    }

    protected GameResourceAssetsFolder<GameResourceModelAsset> readModelsFolder(JGemsPath pathToGameFolder) {
        return this.readModelsFolder(pathToGameFolder.toFile());
    }

    protected GameResourceAssetsFolder<GameResourceModelAsset> readModelsFolder(File rootFile) {
        return this.readModelsFolderRecursive(rootFile, rootFile);
    }

    protected GameResourceAssetsFolder<GameResourceModelAsset> readModelsFolderRecursive(File rootFolder, File folder) {
        GameResourceAssetsFolder<GameResourceModelAsset> assetsFolder = new GameResourceAssetsFolder<>(folder.getName());
        File[] files = folder.listFiles();
        if (files == null) {
            return assetsFolder;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.putFolderThere(this.readModelsFolderRecursive(rootFolder, file));
            } else if (this.isModelFile(file)) {
                GameResourceModelAsset asset = this.loadModelAsset(rootFolder, file);
                if (asset != null) {
                    assetsFolder.putObjectThere(asset);
                }
            }
        }
        return assetsFolder;
    }

    protected GameResourceAssetsFolder<WBenchResourceMapAsset> readMapsFolder(JGemsPath pathToGameFolder) {
        return this.readMapsFolder(pathToGameFolder.toFile());
    }

    protected GameResourceAssetsFolder<WBenchResourceMapAsset> readMapsFolder(File rootFile) {
        return this.readMapsFolderRecursive(rootFile, rootFile);
    }

    protected GameResourceAssetsFolder<WBenchResourceMapAsset> readMapsFolderRecursive(File rootFolder, File relativeFolder) {
        GameResourceAssetsFolder<WBenchResourceMapAsset> assetsFolder = new GameResourceAssetsFolder<>(relativeFolder.getName());
        File[] files = relativeFolder.listFiles();
        if (files == null) {
            return assetsFolder;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.putFolderThere(this.readMapsFolderRecursive(rootFolder, file));
            } else if (this.isMapDefFile(file)) {
                WBenchResourceMapAsset asset = this.loadMapAsset(rootFolder, file);
                if (asset != null) {
                    if (asset.getMapProject() == null) {
                        Log.get().error("Failed to get map");
                        continue;
                    }
                    if (asset.getMapProject().getMapName() == null) {
                        Log.get().error("Failed to get map: " + file.getPath() + ". It's name invalid!");
                    } else {
                        assetsFolder.putObjectThere(asset);
                    }
                }
            }
        }
        return assetsFolder;
    }

    private WBenchResourceMapAsset loadMapAsset(File rootFolder, File fullPath) {
        try {
            final WBenchMapProject mapProject1 = WBench.get().getMapProjectManager().readMainFile(fullPath, true);
            return new WBenchResourceMapAsset(mapProject1);
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    protected GameResourceAssetsFolder<GameResourceScriptAsset> readScriptsFolder(JGemsPath pathToGameFolder) {
        return this.readScriptsFolder(pathToGameFolder.toFile());
    }

    protected GameResourceAssetsFolder<GameResourceScriptAsset> readScriptsFolder(File rootFile) {
        return WBenchProjectResourcesManager.readScriptsFolderRecursive(rootFile, rootFile);
    }

    public static GameResourceAssetsFolder<GameResourceScriptAsset> readScriptsFolderRecursive(File rootFolder, File relativeFolder) {
        GameResourceAssetsFolder<GameResourceScriptAsset> assetsFolder = new GameResourceAssetsFolder<>(relativeFolder.getName());
        File[] files = relativeFolder.listFiles();
        if (files == null) {
            return assetsFolder;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.putFolderThere(WBenchProjectResourcesManager.readScriptsFolderRecursive(rootFolder, file));
            } else if (WBenchProjectResourcesManager.isScriptFile(file)) {
                GameResourceScriptAsset asset = WBenchProjectResourcesManager.loadScriptAsset(rootFolder, file);
                if (asset != null) {
                    assetsFolder.putObjectThere(asset);
                }
            }
        }
        return assetsFolder;
    }

    public static GameResourceScriptAsset loadScriptAsset(File rootFolder, File fullPath) {
        try {
            final String relativePath = rootFolder.toPath().relativize(fullPath.toPath()).toString().replace("\\", "/");
            final String scriptText = Files.readString(fullPath.toPath());
            return new GameResourceScriptAsset(fullPath.getName(), relativePath, scriptText);
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    public static boolean isScriptFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.JS_SCRIPT_FILE);
    }

    private GameResourceModelAsset loadModelAsset(File rootFolder, File fullPath) {
        try {
            MeshGroup meshGroup = this.systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath(fullPath.getPath()), ISource.Source.OUTSIDE_JAR), true, true);
            final String relativePath = fullPath.getPath().substring(rootFolder.getPath().length()).replace("\\", "/");
            final GameResourceModelAsset modelAsset = new GameResourceModelAsset(fullPath.getName(), relativePath, meshGroup);
            this.modelsKeysCache.put(relativePath, modelAsset);
            return modelAsset;
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    private GameResourceSoundAsset loadSoundAsset(File rootFolder, File fullPath) {
        try {
            SoundBuffer soundBuffer = this.systemResources.createSoundBuffer(new JGemsPathSource(new JGemsPath(fullPath.getPath()), ISource.Source.OUTSIDE_JAR), AL10.AL_FORMAT_STEREO16);
            final String relativePath = fullPath.getPath().substring(rootFolder.getPath().length()).replace("\\", "/");
            final GameResourceSoundAsset soundAsset = new GameResourceSoundAsset(fullPath.getName(), relativePath, soundBuffer);
            this.soundKeysCache.put(relativePath, soundAsset);
            return soundAsset;
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    private GameResourceTextureAsset loadTextureAsset(File rootFolder, File fullPath) {
        try {
            ITexture2DProgram texture2DProgram = this.systemResources.createTexture(new JGemsPathSource(new JGemsPath(fullPath.getPath()), ISource.Source.OUTSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(false, false, false, false, false));
            final String relativePath = fullPath.getPath().substring(rootFolder.getPath().length()).replace("\\", "/");
            final GameResourceTextureAsset textureAsset = new GameResourceTextureAsset(fullPath.getName(), relativePath, texture2DProgram);
            this.texturesKeysCache.put(relativePath, textureAsset);
            return textureAsset;
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    protected boolean isMapDefFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE);
    }

    protected boolean isTextureFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp");
    }

    protected boolean isModelFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".gltf");
    }

    protected boolean isSoundFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".ogg");
    }

    public GameResourceAssetsFolder<GameResourceSkyboxAsset> getSkyBoxesAssetsFolder() {
        return this.skyBoxesAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceMarkerObjectAsset> getMarkerAssetsFolder() {
        return this.markerAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceEntityObjectAsset> getEntityAssetsFolder() {
        return this.entityAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceObjectTagData> getTagAssetsFolder() {
        return this.tagAssetsFolder;
    }

    public GameResourceAssetsFolder<WBenchResourceMapAsset> getMapAssetsFolder() {
        return this.mapAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceScriptAsset> getScriptAssetsFolder() {
        return this.scriptAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourcePropObjectAsset> getPropAssetsFolder() {
        return this.propAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceSoundAsset> getSoundAssetsFolder() {
        return this.soundAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceModelAsset> getModelAssetsFolder() {
        return this.modelAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceTextureAsset> getTextureAssetsFolder() {
        return this.textureAssetsFolder;
    }


    public void getAllMapsDataPaths(List<JGemsPath> init, VirtualObjectsFolder<WBenchResourceMapAsset> root) {
        for (WBenchResourceMapAsset map : root.getObjectsThere()) {
            init.add(map.getMapProject().getPathToDataMapFile());
        }

        for (VirtualObjectsFolder<WBenchResourceMapAsset> inner : root.getFoldersThere()) {
            this.getAllMapsDataPaths(init, inner);
        }
    }

    //public void readModelsJSON(JGemsPath pathToGameFolder) {
    //    try {
    //        File dir = this.createSystemFolders(pathToGameFolder);
    //        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
    //        this.models = jsonFileManaging.readFromFile(new File(dir, WBenchProjectResourcesManager.MODEL_ASSETS_FOLDER), Models.class, null);
    //    } catch (IOException e) {
    //        throw new JGemsIOException(e);
    //    }
    //}

    //public void saveModelsJSON(JGemsPath pathToGameFolder) {
    //    try {
    //        File dir = this.createSystemFolders(pathToGameFolder);
    //        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
    //        jsonFileManaging.writeToFile(this.models, new File(dir, WBenchProjectResourcesManager.MODEL_ASSETS_FOLDER), null);
    //    } catch (IOException e) {
    //        throw new JGemsIOException(e);
    //    }
    //}
}
