package ru.jgems3d.toolbox.map_sys.read;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.service.file.JGemsPath;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.toolbox.map_sys.SerializeHelper;
import ru.jgems3d.toolbox.map_sys.save.container.TBoxMapContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.map_sys.save.objects.SaveObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class TBoxMapReader {
    @SuppressWarnings("unchecked")
    public static TBoxMapContainer readMapFolder(File file) {
        MapProperties mapProperties = null;
        HashSet<SaveObject> saveObjectSet = null;
        try {
            mapProperties = SerializeHelper.readFromJSON(file, "map_prop.json", MapProperties.class);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        try {
            saveObjectSet = SerializeHelper.readFromBytes(file, "objects.ser", HashSet.class);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        TBoxMapContainer TBoxMapContainer = new TBoxMapContainer(mapProperties);
        TBoxMapContainer.setSaveObjectSet(saveObjectSet);

        SystemLogging.get().getLogManager().log("Read map file: " + file);
        return TBoxMapContainer;
    }

    @SuppressWarnings("unchecked")
    public static TBoxMapContainer readMapFolderFromJAR(JGemsPath pathToMap) throws IOException, ClassNotFoundException {
        MapProperties mapProperties = null;
        HashSet<SaveObject> saveObjectSet = null;
        try (InputStream stream = JGems3D.loadFileFromJar(new JGemsPath(pathToMap + "/map_prop.json"))){
            mapProperties = SerializeHelper.readFromJSON(stream, MapProperties.class); //TODO
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        try (InputStream stream = JGems3D.loadFileFromJar(new JGemsPath(pathToMap + "/objects.ser"))) {
            saveObjectSet = SerializeHelper.readFromBytes(stream, HashSet.class);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        TBoxMapContainer TBoxMapContainer = new TBoxMapContainer(mapProperties);
        TBoxMapContainer.setSaveObjectSet(saveObjectSet);

        SystemLogging.get().getLogManager().log("Read map file(from jar): " + pathToMap);
        return TBoxMapContainer;
    }
}
