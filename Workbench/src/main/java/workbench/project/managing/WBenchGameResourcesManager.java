package workbench.project.managing;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WBenchGameResourcesManager {
    public static String SYS_ASSETS_FOLDER = "game_assets";
    public static String MODEL_ASSETS_FOLDER = "models";
    public static String TEXTURE_ASSETS_FOLDER = "textures";
    private final SystemResources systemResources;

    private AssetsFolder<ModelAsset> modelAssetAssetsFolder;
    private AssetsFolder<TextureAsset> textureAssetAssetsFolder;

    //private final Set<String> keysCache;

    public WBenchGameResourcesManager(SystemResources systemResources) {
        this.systemResources = systemResources;
        //this.keysCache = new HashSet<>();
    }

    public static JGemsPath getModelsFolder(JGemsPath pathToGameFolder) {
        return new JGemsPath(pathToGameFolder, WBenchGameResourcesManager.SYS_ASSETS_FOLDER, WBenchGameResourcesManager.MODEL_ASSETS_FOLDER);
    }

    public static JGemsPath getTexturesFolder(JGemsPath pathToGameFolder) {
        return new JGemsPath(pathToGameFolder, WBenchGameResourcesManager.SYS_ASSETS_FOLDER, WBenchGameResourcesManager.TEXTURE_ASSETS_FOLDER);
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

    public File createSystemFolders(File file) {
        File dir = new File(file, WBenchGameResourcesManager.SYS_ASSETS_FOLDER);
        for (String s : new String[] {WBenchGameResourcesManager.MODEL_ASSETS_FOLDER, WBenchGameResourcesManager.TEXTURE_ASSETS_FOLDER}) {
            File modelsFile = new File(dir, s);
            if (!modelsFile.exists()) {
                if (modelsFile.mkdirs()) {
                    Log.get().debug("Created folder: " + modelsFile.getPath());
                }
            }
        }
        return dir;
    }

    public void refreshTextures(JGemsPath pathToGameFolder) {
        this.createSystemFolders(pathToGameFolder.toFile());
        this.textureAssetAssetsFolder = this.readTexturesFolder(WBenchGameResourcesManager.getTexturesFolder(pathToGameFolder));
    }

    public void refreshModels(JGemsPath pathToGameFolder) {
        this.createSystemFolders(pathToGameFolder.toFile());
        this.modelAssetAssetsFolder = this.readModelsFolder(WBenchGameResourcesManager.getModelsFolder(pathToGameFolder));
        WBenchOpenGLRenderer.reloadModelResources();
    }

    protected AssetsFolder<TextureAsset> readTexturesFolder(JGemsPath pathToGameFolder) {
        return this.readTexturesFolder(pathToGameFolder.toFile());
    }

    protected AssetsFolder<TextureAsset> readTexturesFolder(File rootFile) {
        return this.readTexturesFolderRecursive(rootFile);
    }

    protected AssetsFolder<TextureAsset> readTexturesFolderRecursive(File folder) {
        AssetsFolder<TextureAsset> assetsFolder = new AssetsFolder<>(folder.getAbsolutePath());

        File[] files = folder.listFiles();
        if (files == null) {
            return assetsFolder;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.getAssetsFoldersInside().add(this.readTexturesFolderRecursive(file));
            } else if (this.isTextureFile(file)) {
                TextureAsset asset = this.loadTextureAsset(file);
                if (asset != null) {
                    assetsFolder.getAssetsThere().add(asset);
                }
            }
        }

        return assetsFolder;
    }

    protected AssetsFolder<ModelAsset> readModelsFolder(JGemsPath pathToGameFolder) {
        return this.readModelsFolder(pathToGameFolder.toFile());
    }

    protected AssetsFolder<ModelAsset> readModelsFolder(File rootFile) {
        return this.readModelsFolderRecursive(rootFile);
    }

    protected AssetsFolder<ModelAsset> readModelsFolderRecursive(File folder) {
        AssetsFolder<ModelAsset> assetsFolder = new AssetsFolder<>(folder.getAbsolutePath());

        File[] files = folder.listFiles();
        if (files == null) {
            return assetsFolder;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                assetsFolder.getAssetsFoldersInside().add(this.readModelsFolderRecursive(file));
            } else if (this.isModelFile(file)) {
                ModelAsset asset = this.loadModelAsset(file);
                if (asset != null) {
                    assetsFolder.getAssetsThere().add(asset);
                }
            }
        }

        return assetsFolder;
    }

    private ModelAsset loadModelAsset(File file) {
        try {
            MeshGroup meshGroup = this.systemResources.createMeshGroupWithBindlessBufferAttachment(JGems3D.GetSource.EXTERNAL, new JGemsPath(file.getPath()),true);
            //this.keysCache.add(file.getPath());
            return new ModelAsset(file.getName(), meshGroup);
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    private TextureAsset loadTextureAsset(File file) {
        try {
            ITexture2DProgram texture2DProgram = this.systemResources.createTexture(JGems3D.GetSource.EXTERNAL, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(file.getPath()),new ImageTexture.Properties(false, false, false, false, false));
            //this.keysCache.add(file.getPath());
            return new TextureAsset(file.getName(), texture2DProgram);
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    protected boolean isTextureFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp");
    }

    protected boolean isModelFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".gltf");
    }

    public AssetsFolder<ModelAsset> getModelAssetsFolder() {
        return this.modelAssetAssetsFolder;
    }

    public AssetsFolder<TextureAsset> getTextureAssetsFolder() {
        return this.textureAssetAssetsFolder;
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

    private interface IAsset {}

    public static class TextureAsset implements IAsset {
        private final String name;
        private final ITexture2DProgram texture2DProgram;

        public TextureAsset(String name, ITexture2DProgram texture2DProgram) {
            this.name = name;
            this.texture2DProgram = texture2DProgram;
        }

        public String getName() {
            return this.name;
        }

        public ITexture2DProgram getTexture2DProgram() {
            return this.texture2DProgram;
        }
    }

    public static class ModelAsset implements IAsset {
        private final String name;
        private final MeshGroup meshGroup;

        public ModelAsset(String name, MeshGroup meshGroup) {
            this.name = name;
            this.meshGroup = meshGroup;
        }

        public String getName() {
            return this.name;
        }

        public MeshGroup getMeshGroup() {
            return this.meshGroup;
        }
    }

    public static class AssetsFolder <T extends IAsset> {
        private final String path;
        private final List<T> assetsThere;
        private final List<AssetsFolder<T>> assetsFoldersInside;

        public AssetsFolder(String path) {
            this.path = path;
            this.assetsThere = new ArrayList<>();
            this.assetsFoldersInside = new ArrayList<>();
        }

        public List<T> getAssetsThere() {
            return this.assetsThere;
        }

        public List<AssetsFolder<T>> getAssetsFoldersInside() {
            return this.assetsFoldersInside;
        }

        public String getPath() {
            return this.path;
        }
    }
}
