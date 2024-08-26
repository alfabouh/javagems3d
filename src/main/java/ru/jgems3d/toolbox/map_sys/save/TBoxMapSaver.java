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

package ru.jgems3d.toolbox.map_sys.save;

import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.logger.managers.LoggingManager;
import ru.jgems3d.toolbox.map_sys.SerializeHelper;
import ru.jgems3d.toolbox.map_sys.save.container.TBoxMapContainer;

import java.io.File;
import java.io.IOException;

public class TBoxMapSaver {
    public static void saveMap(TBoxMapContainer TBoxMapContainer, File file) throws IOException {
        File toSave = new File(file, TBoxMapContainer.getSaveMapProperties().getMapName());
        toSave.mkdirs();

        SerializeHelper.saveToJSON(toSave, "map_prop.json", TBoxMapContainer.getSaveMapProperties());
        SerializeHelper.saveToBytes(toSave, "objects.ser", TBoxMapContainer.getSaveObjectsSet());

        SystemLogging.get().getLogManager().log("Saved map path: " + file);
        LoggingManager.showWindowInfo("Successfully saved map!");
    }
}
