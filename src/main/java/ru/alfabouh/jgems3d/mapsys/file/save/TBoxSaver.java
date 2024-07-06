package ru.alfabouh.jgems3d.mapsys.file.save;

import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.managers.LoggingManager;
import ru.alfabouh.jgems3d.mapsys.file.SerializeHelper;
import ru.alfabouh.jgems3d.mapsys.file.save.container.SaveContainer;

import java.io.File;
import java.io.IOException;

public class TBoxSaver {
    public static void saveEditorToJSON(SaveContainer saveContainer, File file) throws IOException {
        File toSave = new File(file, saveContainer.getSaveMapProperties().getMapName());
        toSave.mkdirs();

        SerializeHelper.saveToJSON(toSave, "map_prop.json", saveContainer.getSaveMapProperties());
        SerializeHelper.saveToBytes(toSave, "objects.ser", saveContainer.getSaveObjectsSet());

        SystemLogging.get().getLogManager().log("Saved map file: " + toSave.getName());
        LoggingManager.showWindowInfo("Successfully saved map!");
    }
}
