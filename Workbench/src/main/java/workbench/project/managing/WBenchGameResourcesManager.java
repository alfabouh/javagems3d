package workbench.project.managing;

import com.google.gson.reflect.TypeToken;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;
import workbench.project.managing.instances.mapping.GameResourceMapAsset;
import workbench.project.managing.instances.mapping.GameResourceSkyboxAsset;
import workbench.project.managing.instances.misc.GameResourceModelAsset;
import workbench.project.managing.instances.misc.GameResourceObjectTagData;
import workbench.project.managing.instances.misc.GameResourceTextureAsset;
import workbench.project.managing.instances.world.GameResourceEntityObjectAsset;
import workbench.project.managing.instances.world.GameResourcePropObjectAsset;
import workbench.project.map.WBenchMapProject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;

public class WBenchGameResourcesManager {
    public static String SYS_ASSETS_FOLDER = "game_assets";
    public static String SYS_MAPS_FOLDER = "game_maps";
    public static String MODEL_ASSETS_FOLDER = "models";
    public static String TEXTURE_ASSETS_FOLDER = "textures";
    public static String OBJECT_ASSETS_FOLDER = "objects";
    public static String ENVIRONMENT_ASSETS_FOLDER = "environment";

    private final SystemResources systemResources;
    private GameResourceAssetsFolder<GameResourceModelAsset> modelAssetsFolder;
    private GameResourceAssetsFolder<GameResourceTextureAsset> textureAssetsFolder;
    private GameResourceAssetsFolder<GameResourceMapAsset> mapAssetsFolder;
    private GameResourceAssetsFolder<GameResourcePropObjectAsset> propAssetsFolder;
    private GameResourceAssetsFolder<GameResourceEntityObjectAsset> entityAssetsFolder;
    private GameResourceAssetsFolder<GameResourceObjectTagData> tagAssetsFolder;
    private GameResourceAssetsFolder<GameResourceSkyboxAsset> skyBoxesAssetsFolder;

    private final Map<String, GameResourceModelAsset> modelsKeysCache;
    private final Map<String, GameResourceTextureAsset> texturesKeysCache;

    public enum AssetsTarget {
        ALL,
        PROPS,
        ENTITIES,
        TAGS,
        SKYBOXES
    }

    public WBenchGameResourcesManager(SystemResources systemResources) {
        this.systemResources = systemResources;
        this.modelsKeysCache = new HashMap<>();
        this.texturesKeysCache = new HashMap<>();
    }

    public @Nullable GameResourceModelAsset extractFromCacheModel(String relativePath) {
        return this.modelsKeysCache.get(relativePath);
    }

    public @Nullable GameResourceTextureAsset extractFromCacheTexture(String relativePath) {
        return this.texturesKeysCache.get(relativePath);
    }

    public static JGemsPath getMapsFolder(JGemsPath pathToGameFolder) {
        return new JGemsPath(pathToGameFolder, WBenchGameResourcesManager.SYS_MAPS_FOLDER);
    }

    public static JGemsPath getModelsFolder(JGemsPath pathToGameFolder) {
        return new JGemsPath(pathToGameFolder, WBenchGameResourcesManager.SYS_ASSETS_FOLDER, WBenchGameResourcesManager.MODEL_ASSETS_FOLDER);
    }

    public static JGemsPath getTexturesFolder(JGemsPath pathToGameFolder) {
        return new JGemsPath(pathToGameFolder, WBenchGameResourcesManager.SYS_ASSETS_FOLDER, WBenchGameResourcesManager.TEXTURE_ASSETS_FOLDER);
    }

    public static JGemsPath getObjectsFolder(JGemsPath pathToGameFolder) {
        return new JGemsPath(pathToGameFolder, WBenchGameResourcesManager.SYS_ASSETS_FOLDER, WBenchGameResourcesManager.OBJECT_ASSETS_FOLDER);
    }

    public static JGemsPath getEnvironmentFolder(JGemsPath pathToGameFolder) {
        return new JGemsPath(pathToGameFolder, WBenchGameResourcesManager.SYS_ASSETS_FOLDER, WBenchGameResourcesManager.ENVIRONMENT_ASSETS_FOLDER);
    }

    public static void openTexturesFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(WBenchGameResourcesManager.getTexturesFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public static void openModelsFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(WBenchGameResourcesManager.getModelsFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public static void openMapsFolder(JGemsPath pathToGameFolder) {
        SwingUtilities.invokeLater(() -> {
            try {
                Desktop.getDesktop().open(WBenchGameResourcesManager.getMapsFolder(pathToGameFolder).toFile());
            } catch (Exception e) {
                Log.get().exception(e);
            }
        });
    }

    public File[] createSystemFolders(File file) {
        File dir1 = new File(file, WBenchGameResourcesManager.SYS_ASSETS_FOLDER);
        File dir2 = new File(file, WBenchGameResourcesManager.SYS_MAPS_FOLDER);
        {
            for (String s : new String[]{WBenchGameResourcesManager.MODEL_ASSETS_FOLDER, WBenchGameResourcesManager.TEXTURE_ASSETS_FOLDER, WBenchGameResourcesManager.OBJECT_ASSETS_FOLDER}) {
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
        return new File[]{dir1, dir2};
    }

    public void refreshTextures(JGemsPath pathToGameFolder) {
        {
            this.texturesKeysCache.values().forEach(e -> {
                this.systemResources.getResourceCache().clearObjectFromCache(new JGemsPath(WBenchGameResourcesManager.getTexturesFolder(pathToGameFolder), e.getRelativePath()));
            });
            this.texturesKeysCache.clear();
        }
        this.createSystemFolders(pathToGameFolder.toFile());
        this.textureAssetsFolder = this.readTexturesFolder(WBenchGameResourcesManager.getTexturesFolder(pathToGameFolder));
    }

    public void refreshModels(JGemsPath pathToGameFolder) {
        {
            this.modelsKeysCache.values().forEach(e -> {
                this.systemResources.getResourceCache().clearObjectFromCache(new JGemsPath(WBenchGameResourcesManager.getModelsFolder(pathToGameFolder), e.getRelativePath() + MeshGroup.POSTFIX));
            });
            this.modelsKeysCache.clear();
        }
        this.createSystemFolders(pathToGameFolder.toFile());
        this.modelAssetsFolder = this.readModelsFolder(WBenchGameResourcesManager.getModelsFolder(pathToGameFolder));
        WBenchOpenGLRenderer.reloadModelResources();
    }

    public void refreshMaps(JGemsPath pathToGameFolder) {
        this.createSystemFolders(pathToGameFolder.toFile());
        this.mapAssetsFolder = this.readMapsFolder(WBenchGameResourcesManager.getMapsFolder(pathToGameFolder));
    }

    public void saveCreatableResourceObjects(@NotNull AssetsTarget assetsTarget, JGemsPath folder) {
        if (assetsTarget.equals(AssetsTarget.SKYBOXES) || assetsTarget.equals(AssetsTarget.ALL)) {
            JSONFileManaging.createSerializationRules().writeToFile(this.getSkyBoxesAssetsFolder(), new JGemsPath(WBenchGameResourcesManager.getEnvironmentFolder(folder), "_skyBoxes.json").toFile(), null);
        }
        if (assetsTarget.equals(AssetsTarget.PROPS) || assetsTarget.equals(AssetsTarget.ALL)) {
            TagsContainer.createJSONFileManaging().writeToFile(this.getPropAssetsFolder(), new JGemsPath(WBenchGameResourcesManager.getObjectsFolder(folder), "_props.json").toFile(), null);
        }
        if (assetsTarget.equals(AssetsTarget.ENTITIES) || assetsTarget.equals(AssetsTarget.ALL)) {
            TagsContainer.createJSONFileManaging().writeToFile(this.getPropAssetsFolder(), new JGemsPath(WBenchGameResourcesManager.getObjectsFolder(folder), "_entities.json").toFile(), null);
        }
        if (assetsTarget.equals(AssetsTarget.TAGS) || assetsTarget.equals(AssetsTarget.ALL)) {
            TagsContainer.createJSONFileManaging().writeToFile(this.getTagAssetsFolder(), new JGemsPath(WBenchGameResourcesManager.getObjectsFolder(folder), "_tags.json").toFile(), null);
        }
    }

    public void readCreatableResourceObjects(@NotNull AssetsTarget assetsTarget, JGemsPath folder) {
        if (assetsTarget.equals(AssetsTarget.PROPS) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.propAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(WBenchGameResourcesManager.getObjectsFolder(folder), "_props.json").toFile(), new TypeToken<GameResourceAssetsFolder<GameResourcePropObjectAsset>>() {
                }, null);
                this.propAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.propAssetsFolder = new GameResourceAssetsFolder<>("root");
                Log.get().exception(e);
            }
        }
        if (assetsTarget.equals(AssetsTarget.ENTITIES) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.entityAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(WBenchGameResourcesManager.getObjectsFolder(folder), "_entities.json").toFile(), new TypeToken<GameResourceAssetsFolder<GameResourceEntityObjectAsset>>() {
                }, null);
                this.entityAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.entityAssetsFolder = new GameResourceAssetsFolder<>("root");
                Log.get().exception(e);
            }
        }
        if (assetsTarget.equals(AssetsTarget.TAGS) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.tagAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(WBenchGameResourcesManager.getObjectsFolder(folder), "_tags.json").toFile(), new TypeToken<GameResourceAssetsFolder<GameResourceObjectTagData>>() {
                }, null);
                this.tagAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.tagAssetsFolder = new GameResourceAssetsFolder<>("root");
                Log.get().exception(e);
            }
        }
        if (assetsTarget.equals(AssetsTarget.SKYBOXES) || assetsTarget.equals(AssetsTarget.ALL)) {
            try {
                this.skyBoxesAssetsFolder = JSONFileManaging.createSerializationRules().readFromFile(new JGemsPath(WBenchGameResourcesManager.getEnvironmentFolder(folder), "_skyBoxes.json").toFile(), new TypeToken<GameResourceAssetsFolder<GameResourceSkyboxAsset>>() {
                }, null);
                this.skyBoxesAssetsFolder.buildRelations();
            } catch (Exception e) {
                this.skyBoxesAssetsFolder = new GameResourceAssetsFolder<>("root");
                Log.get().exception(e);
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
                assetsFolder.addFolderThere(this.readTexturesFolderRecursive(rootFolder, file));
            } else if (this.isTextureFile(file)) {
                GameResourceTextureAsset asset = this.loadTextureAsset(rootFolder, file);
                if (asset != null) {
                    assetsFolder.addAssetThere(asset);
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
                assetsFolder.addFolderThere(this.readModelsFolderRecursive(rootFolder, file));
            } else if (this.isModelFile(file)) {
                GameResourceModelAsset asset = this.loadModelAsset(rootFolder, file);
                if (asset != null) {
                    assetsFolder.addAssetThere(asset);
                }
            }
        }
        return assetsFolder;
    }

    protected GameResourceAssetsFolder<GameResourceMapAsset> readMapsFolder(JGemsPath pathToGameFolder) {
        return this.readMapsFolder(pathToGameFolder.toFile());
    }

    protected GameResourceAssetsFolder<GameResourceMapAsset> readMapsFolder(File rootFile) {
        return this.readMapsFolderRecursive(rootFile, rootFile);
    }

    protected GameResourceAssetsFolder<GameResourceMapAsset> readMapsFolderRecursive(File rootFolder, File relativeFolder) {
        GameResourceAssetsFolder<GameResourceMapAsset> assetsFolder = new GameResourceAssetsFolder<>(relativeFolder.getName());
        File[] files = relativeFolder.listFiles();
        if (files == null) {
            return assetsFolder;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.addFolderThere(this.readMapsFolderRecursive(rootFolder, file));
            } else if (this.isMapDefFile(file)) {
                GameResourceMapAsset asset = this.loadMapAsset(rootFolder, file);
                if (asset != null) {
                    if (asset.getMapProject().getMapName() == null) {
                        Log.get().error("Failed to get map: " + file.getPath() + ". It's name invalid!");
                    } else {
                        assetsFolder.addAssetThere(asset);
                    }
                }
            }
        }
        return assetsFolder;
    }

    private GameResourceMapAsset loadMapAsset(File rootFolder, File fullPath) {
        try {
            final WBenchMapProject mapProject1 = WBench.get().getMapProjectManager().readMainFile(fullPath, true);
            return new GameResourceMapAsset(mapProject1);
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    private GameResourceModelAsset loadModelAsset(File rootFolder, File fullPath) {
        try {
            MeshGroup meshGroup = this.systemResources.createMeshGroupWithBindlessBufferAttachment(JGems3D.GetSource.EXTERNAL, new JGemsPath(fullPath.getPath()),true);
            final String relativePath = fullPath.getPath().substring(rootFolder.getPath().length());
            final GameResourceModelAsset modelAsset = new GameResourceModelAsset(fullPath.getName(), relativePath, meshGroup);
            this.modelsKeysCache.put(relativePath, modelAsset);
            return modelAsset;
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    private GameResourceTextureAsset loadTextureAsset(File rootFolder, File fullPath) {
        try {
            ITexture2DProgram texture2DProgram = this.systemResources.createTexture(JGems3D.GetSource.EXTERNAL, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(fullPath.getPath()), new ImageTexture.Properties(false, false, false, false, false));
            final String relativePath = fullPath.getPath().substring(rootFolder.getPath().length());
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

    public GameResourceAssetsFolder<GameResourceSkyboxAsset> getSkyBoxesAssetsFolder() {
        return this.skyBoxesAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceEntityObjectAsset> getEntityAssetsFolder() {
        return this.entityAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceObjectTagData> getTagAssetsFolder() {
        return this.tagAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceMapAsset> getMapAssetsFolder() {
        return this.mapAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourcePropObjectAsset> getPropAssetsFolder() {
        return this.propAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceModelAsset> getModelAssetsFolder() {
        return this.modelAssetsFolder;
    }

    public GameResourceAssetsFolder<GameResourceTextureAsset> getTextureAssetsFolder() {
        return this.textureAssetsFolder;
    }

    //public void readModelsJSON(JGemsPath pathToGameFolder) {
    //    try {
    //        File dir = this.createSystemFolders(pathToGameFolder);
    //        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
    //        this.models = jsonFileManaging.readFromFile(new File(dir, WBenchGameResourcesManager.MODEL_ASSETS_FOLDER), Models.class, null);
    //    } catch (IOException e) {
    //        throw new JGemsIOException(e);
    //    }
    //}

    //public void saveModelsJSON(JGemsPath pathToGameFolder) {
    //    try {
    //        File dir = this.createSystemFolders(pathToGameFolder);
    //        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
    //        jsonFileManaging.writeToFile(this.models, new File(dir, WBenchGameResourcesManager.MODEL_ASSETS_FOLDER), null);
    //    } catch (IOException e) {
    //        throw new JGemsIOException(e);
    //    }
    //}
}
