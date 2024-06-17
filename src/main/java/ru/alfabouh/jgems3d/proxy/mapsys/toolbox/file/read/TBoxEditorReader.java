package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.read;

import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.SerializeHelper;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.container.SaveContainer;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveMapProperties;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveModeledObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class TBoxEditorReader {
    @SuppressWarnings("unchecked")
    public static SaveContainer readMapFolder(File file) throws IOException, ClassNotFoundException {
        SaveMapProperties saveMapProperties = SerializeHelper.readFromJSON(file, "map_prop.json", SaveMapProperties.class);
        HashSet<SaveModeledObject> saveObjectSet = SerializeHelper.readFromBytes(file, "objects.ser", HashSet.class);

        SaveContainer saveContainer = new SaveContainer(saveMapProperties);
        saveContainer.setSaveObjectSet(saveObjectSet);

        SystemLogging.get().getLogManager().log("Read map file: " + file);
        return saveContainer;
    }
}
