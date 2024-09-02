/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.temp.map_sys.read;

import javagems3d.JGems3D;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import javagems3d.temp.map_sys.save.objects.SaveObject;
import logger.SystemLogging;
import javagems3d.temp.map_sys.SerializeHelper;
import javagems3d.temp.map_sys.save.container.TBoxMapContainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class TBoxMapReader {
    @SuppressWarnings("unchecked")
    public static TBoxMapContainer readMap(File file) throws IOException, ClassNotFoundException {
        MapProperties mapProperties;
        HashSet<SaveObject> saveObjectSet;
        mapProperties = SerializeHelper.readFromJSON(file, "map_prop.json", MapProperties.class);
        saveObjectSet = SerializeHelper.readFromBytes(file, "objects.ser", HashSet.class);
        TBoxMapContainer TBoxMapContainer = new TBoxMapContainer(mapProperties);
        TBoxMapContainer.setSaveObjectSet(saveObjectSet);

        SystemLogging.get().getLogManager().log("Read map path: " + file);
        return TBoxMapContainer;
    }

    @SuppressWarnings("unchecked")
    public static TBoxMapContainer readMapFromJAR(JGemsPath pathToMap) throws IOException, ClassNotFoundException {
        MapProperties mapProperties;
        HashSet<SaveObject> saveObjectSet;
        try (InputStream stream = JGems3D.loadFileFromJar(new JGemsPath(pathToMap + "/map_prop.json"))) {
            mapProperties = SerializeHelper.readFromJSON(stream, MapProperties.class); //TODO
        }
        try (InputStream stream = JGems3D.loadFileFromJar(new JGemsPath(pathToMap + "/objects.ser"))) {
            saveObjectSet = SerializeHelper.readFromBytes(stream, HashSet.class);
        }
        TBoxMapContainer TBoxMapContainer = new TBoxMapContainer(mapProperties);
        TBoxMapContainer.setSaveObjectSet(saveObjectSet);

        SystemLogging.get().getLogManager().log("Read map path(from jar): " + pathToMap);
        return TBoxMapContainer;
    }
}
