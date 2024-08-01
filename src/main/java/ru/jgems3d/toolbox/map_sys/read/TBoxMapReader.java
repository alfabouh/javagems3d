package ru.jgems3d.toolbox.map_sys.read;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.toolbox.map_sys.SerializeHelper;
import ru.jgems3d.toolbox.map_sys.save.container.SaveContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.map_sys.save.objects.SaveObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class TBoxMapReader {
    @SuppressWarnings("unchecked")
    public static SaveContainer readMapFolder(File file) throws IOException, ClassNotFoundException {
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
        SaveContainer saveContainer = new SaveContainer(mapProperties);
        saveContainer.setSaveObjectSet(saveObjectSet);

        SystemLogging.get().getLogManager().log("Read map file: " + file);
        return saveContainer;
    }

    @SuppressWarnings("unchecked")
    public static SaveContainer readMapFolderFromJAR(String mapName) throws IOException, ClassNotFoundException {
        MapProperties mapProperties = null;
        HashSet<SaveObject> saveObjectSet = null;
        try {
            mapProperties = SerializeHelper.readFromJSON(JGems3D.loadFileJar(new JGPath("/assets/jgems/maps/" + mapName + "/map_prop.json")), MapProperties.class); //TODO
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        try {
            saveObjectSet = SerializeHelper.readFromBytes(JGems3D.loadFileJar(new JGPath("/assets/jgems/maps/" + mapName + "/objects.ser")), HashSet.class);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        SaveContainer saveContainer = new SaveContainer(mapProperties);
        saveContainer.setSaveObjectSet(saveObjectSet);

        SystemLogging.get().getLogManager().log("Read map file(from jar): " + mapName);
        return saveContainer;
    }
}