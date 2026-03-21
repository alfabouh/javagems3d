package javagems3d.system.external.gaming;

import api.application.workbench.manager.IAPIWBenchDataManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.external.gaming.def.misc.GameResourceSkyboxAsset;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import javagems3d.system.external.gaming.def.world.GameResourceEntityObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourceWorldObjectAsset;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.AbstractObjectsFolder;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class JGemsGaming {
    public static String SYS_SCRIPTS_FOLDER = "game_scripts";
    public static String SYS_ASSETS_FOLDER = "game_assets";
    public static String SYS_MAPS_FOLDER = "game_maps";
    public static String MODEL_ASSETS_FOLDER = "models";
    public static String TEXTURE_ASSETS_FOLDER = "textures";
    public static String OBJECT_ASSETS_FOLDER = "objects";
    public static String SCRIPTS_FOLDER = "scripts";
    public static String ENVIRONMENT_ASSETS_FOLDER = "environment";

    public String gameTitle;
    public String gameDescription;
    public String gameVersion;

    private final Map<String, JGemsPath> maps;
    private JGemsPath pathToGameFolder;

    public JGemsGaming() {
        this.maps = new HashMap<>();
    }

    public static JGemsPath getMapsFolder(JGemsPath absPath) {
        return new JGemsPath(absPath, SYS_MAPS_FOLDER);
    }

    public static JGemsPath getModelsFolder(JGemsPath absPath) {
        return new JGemsPath(absPath, SYS_ASSETS_FOLDER, MODEL_ASSETS_FOLDER);
    }

    public static JGemsPath getTexturesFolder(JGemsPath absPath) {
        return new JGemsPath(absPath, SYS_ASSETS_FOLDER, TEXTURE_ASSETS_FOLDER);
    }

    public static JGemsPath getObjectsFolder(JGemsPath absPath) {
        return new JGemsPath(absPath, SYS_ASSETS_FOLDER, OBJECT_ASSETS_FOLDER);
    }

    public static JGemsPath getEnvironmentFolder(JGemsPath absPath) {
        return new JGemsPath(absPath, SYS_ASSETS_FOLDER, ENVIRONMENT_ASSETS_FOLDER);
    }

    public static JGemsPath getPathToMainMapFile(JGemsPath absolutePath, String mapName) {
        return new JGemsPath(absolutePath, mapName + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE);
    }
    public static JGemsPath getPathToDataMapFile(JGemsPath absolutePath, String mapName) {
        return new JGemsPath(absolutePath, mapName + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_FILE);
    }

    public static JGemsPath getScriptsFolder(@NotNull JGemsPath absPath) {
        return new JGemsPath(absPath, SYS_SCRIPTS_FOLDER);
    }

    private void seekForMainFile(@NotNull JGemsPath absolutePathToFiles) throws JGemsIOException {
        File projectFolder = absolutePathToFiles.toFile();
        if (!projectFolder.exists() || !projectFolder.isDirectory()) {
            throw new JGemsIOException("Project folder does not exist: " + projectFolder);
        }
        File[] files = projectFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_PROJECT_FILE));
        if (files == null || files.length != 1) {
            throw new JGemsIOException("Couldn't find WBenchGameProject file: " + projectFolder + " (" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_PROJECT_FILE + ")");
        }
        File mainFile = files[0];
        try (Reader reader = Files.newBufferedReader(mainFile.toPath())) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            this.gameTitle = json.has("gameTitle") ? json.get("gameTitle").getAsString() : null;
            this.gameDescription = json.has("gameInfo") ? json.get("gameInfo").getAsString() : null;
            this.gameVersion = json.has("version") ? json.get("version").getAsString() : null;
        } catch (IOException e) {
            throw new JGemsIOException("Error reading main project file: " + mainFile, e);
        } catch (JsonSyntaxException e) {
            throw new JGemsIOException("Invalid JSON in main project file: " + mainFile, e);
        }
    }

    public void loadExternalGameFiles(@NotNull IAPIWBenchDataManager apiDataManager, @NotNull JGemsPath absolutePathToFiles) {
        Log.get().info("Loading external game files: " + absolutePathToFiles);
        this.seekForMainFile(absolutePathToFiles);
        Log.get().info("Found game: " + this);
        this.loadObjectAssetsInAPI(apiDataManager, absolutePathToFiles);
        this.readMapFolders(absolutePathToFiles);

        this.pathToGameFolder = absolutePathToFiles;
        Log.get().info("Loading external game files: SUCCESS");
    }

    private void readMapFolders(@NotNull JGemsPath absPath) {
        File mapsFolder = JGemsGaming.getMapsFolder(absPath).toFile();
        if (!mapsFolder.exists() || !mapsFolder.isDirectory()) {
            return;
        }

        File[] directories = mapsFolder.listFiles(File::isDirectory);
        if (directories == null) {
            return;
        }
        for (File dir : directories) {
            File[] mapFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE));
            if (mapFiles == null || mapFiles.length < 1) {
                continue;
            }
            File mapMainFile = mapFiles[0];
            String mapId = dir.getName();
            Log.get().debug("Got map: " + mapMainFile.getAbsolutePath());
            this.maps.put(mapId, new JGemsPath(mapMainFile.getAbsolutePath()));
        }
    }

    private void loadObjectAssetsInAPI(@NotNull IAPIWBenchDataManager apiDataManager, @NotNull JGemsPath absolutePathToFiles) {
        this.loadEntityAssetsInAPI(apiDataManager, absolutePathToFiles);
        this.loadPropAssetsInAPI(apiDataManager, absolutePathToFiles);
        this.loadSkyBoxesInAPI(apiDataManager, absolutePathToFiles);
    }

    private void loadSkyBoxesInAPI(@NotNull IAPIWBenchDataManager apiDataManager, @NotNull JGemsPath absolutePathToFiles) {
        GameResourceAssetsFolder<GameResourceSkyboxAsset> skyBoxesAsset = null;
        try {
            skyBoxesAsset = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(JGemsGaming.getEnvironmentFolder(absolutePathToFiles), "_skyBoxes.json").toFile(), new TypeToken<>() {
            }, null);
            skyBoxesAsset.buildRelations();
        } catch (Exception e) {
            throw new JGemsIOException("Couldn't load file! ", e);
        }
        this.convertSkyBoxes(apiDataManager, skyBoxesAsset, e -> {
            apiDataManager.addResourceSkyCubeMap(e.second().name(), new ICubeMapProgram.CMTextures(JGemsGaming.getTexturesFolder(absolutePathToFiles), e.second().getCmTextures()));
        });
    }

    private void loadEntityAssetsInAPI(@NotNull IAPIWBenchDataManager apiDataManager, @NotNull JGemsPath absolutePathToFiles) {
        GameResourceAssetsFolder<GameResourceEntityObjectAsset> entityAssetsFolder = null;
        try {
            entityAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(JGemsGaming.getObjectsFolder(absolutePathToFiles), "_entities.json").toFile(), new TypeToken<>() {
            }, null);
            entityAssetsFolder.buildRelations();
        } catch (Exception e) {
            throw new JGemsIOException("Couldn't load file! ", e);
        }
        this.convertWorldObject(apiDataManager, entityAssetsFolder, e -> {
            apiDataManager.addResourceEntity(e.first().getHierarchy(), e.second().getID(), e.second().getModelAssetRelativePath() == null ? null : new JGemsPathSource(new JGemsPath(JGemsGaming.getModelsFolder(absolutePathToFiles), e.second().getModelAssetRelativePath()), ISource.Source.OUTSIDE_JAR));
        });
    }

    private void convertSkyBoxes(@NotNull IAPIWBenchDataManager apiDataManager, @NotNull AbstractObjectsFolder<GameResourceSkyboxAsset> folder, Consumer<Pair<AbstractObjectsFolder<GameResourceSkyboxAsset>, GameResourceSkyboxAsset>> convertor) {
        try {
            for (GameResourceSkyboxAsset asset : folder.getObjectsThere()) {
                Log.get().debug("Loader got: " + folder.getHierarchy() + "/" + asset.name());
                convertor.accept(new Pair<>(folder, asset));
                Log.get().debug("Success");
            }
            for (AbstractObjectsFolder<GameResourceSkyboxAsset> folderInside : folder.getFoldersThere()) {
                this.convertSkyBoxes(apiDataManager, folderInside, convertor);
            }
        } catch (Exception e) {
            throw new JGemsIOException("Couldn't load entity assets!", e);
        }
    }

    private <T extends GameResourceWorldObjectAsset> void convertWorldObject(@NotNull IAPIWBenchDataManager apiDataManager, @NotNull AbstractObjectsFolder<T> folder, Consumer<Pair<AbstractObjectsFolder<T>, T>> convertor) {
        try {
            for (T asset : folder.getObjectsThere()) {
                Log.get().debug("Loader got: " + folder.getHierarchy() + "/" + asset.getID());
                if (asset.getModelAssetRelativePath() == null) {
                    Log.get().warn(folder.getHierarchy() + "/" + asset.getID() + " - NULL MODEL");
                }
                convertor.accept(new Pair<>(folder, asset));
                Log.get().debug("Success");
            }
            for (AbstractObjectsFolder<T> folderInside : folder.getFoldersThere()) {
                this.convertWorldObject(apiDataManager, folderInside, convertor);
            }
        } catch (Exception e) {
            throw new JGemsIOException("Couldn't load entity assets!", e);
        }
    }

    private void loadPropAssetsInAPI(@NotNull IAPIWBenchDataManager apiDataManager, @NotNull JGemsPath absolutePathToFiles) {
        GameResourceAssetsFolder<GameResourcePropObjectAsset> propAssetsFolder = null;
        try {
            propAssetsFolder = TagsContainer.createJSONFileManaging().readFromFile(new JGemsPath(JGemsGaming.getObjectsFolder(absolutePathToFiles), "_props.json").toFile(), new TypeToken<GameResourceAssetsFolder<GameResourcePropObjectAsset>>() {
            }, null);
            propAssetsFolder.buildRelations();
        } catch (Exception e) {
            throw new JGemsIOException("Couldn't load file! ", e);
        }
        this.convertWorldObject(apiDataManager, propAssetsFolder, e -> {
            apiDataManager.addResourceProp(e.first().getHierarchy(), e.second().getID(), e.second().getModelAssetRelativePath() == null ? null : new JGemsPathSource(new JGemsPath(JGemsGaming.getModelsFolder(absolutePathToFiles), e.second().getModelAssetRelativePath()), ISource.Source.OUTSIDE_JAR));
        });
    }

    public JGemsPath getPathToGameFolder() {
        return this.pathToGameFolder;
    }

    @Override
    public String toString() {
        return "JGemsGaming{" +
                "gameTitle='" + gameTitle + '\'' +
                ", gameDescription='" + gameDescription + '\'' +
                ", gameVersion='" + gameVersion + '\'' +
                '}';
    }

    public Map<String, JGemsPath> getMaps() {
        return this.maps;
    }
}
