package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save;

import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.proxy.logger.managers.LoggingManager;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.SerializeHelper;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.container.SaveContainer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class TBoxEditorSaver {
    public static void saveEditorToJSON(SaveContainer saveContainer, File file) throws IOException {
        File toSave = new File(file, saveContainer.getSaveMapProperties().getMapName());
        toSave.mkdirs();

        SerializeHelper.saveToJSON(toSave, "map_prop.json", saveContainer.getSaveMapProperties());
        SerializeHelper.saveToBytes(toSave, "objects.ser", saveContainer.getSaveObjectSet());

        SystemLogging.get().getLogManager().log("Saved map file: " + toSave.getName());
        LoggingManager.showWindowInfo("Successfully saved map!");
    }
}
