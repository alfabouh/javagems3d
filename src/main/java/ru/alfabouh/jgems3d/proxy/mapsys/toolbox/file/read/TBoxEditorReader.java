package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.read;

import com.google.gson.JsonSyntaxException;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.SerializeHelper;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.container.SaveContainer;
import ru.alfabouh.jgems3d.toolbox.render.scene.container.MapProperties;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class TBoxEditorReader {
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
}
