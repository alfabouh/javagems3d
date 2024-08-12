package ru.jgems3d.toolbox.map_sys.save;

import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.logger.managers.LoggingManager;
import ru.jgems3d.toolbox.map_sys.SerializeHelper;
import ru.jgems3d.toolbox.map_sys.save.container.TBoxMapContainer;

import java.io.File;
import java.io.IOException;

public class TBoxMapSaver {
    public static void saveEditorToJSON(TBoxMapContainer TBoxMapContainer, File file) throws IOException {
        File toSave = new File(file, TBoxMapContainer.getSaveMapProperties().getMapName());
        toSave.mkdirs();

        SerializeHelper.saveToJSON(toSave, "map_prop.json", TBoxMapContainer.getSaveMapProperties());
        SerializeHelper.saveToBytes(toSave, "objects.ser", TBoxMapContainer.getSaveObjectsSet());

        SystemLogging.get().getLogManager().log("Saved map path: " + toSave.getName());
        LoggingManager.showWindowInfo("Successfully saved map!");
    }
}
