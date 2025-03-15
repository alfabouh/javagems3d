package javagems3d.temp.map_sys.save;

import logger.SystemLogging;
import logger.managers.LoggingManager;
import javagems3d.temp.map_sys.SerializeHelper;
import javagems3d.temp.map_sys.save.container.TBoxMapContainer;

import java.io.File;
import java.io.IOException;

public class TBoxMapSaver {
    public static void saveMap(TBoxMapContainer TBoxMapContainer, File file) throws IOException {
        File toSave = new File(file, TBoxMapContainer.getSaveMapProperties().getMapName());
        toSave.mkdirs();

        SerializeHelper.saveToJSON(toSave, "map_prop.json", TBoxMapContainer.getSaveMapProperties());
        SerializeHelper.saveToBytes(toSave, "objects.ser", TBoxMapContainer.getSaveObjectsSet());

        SystemLogging.get().getLogManager().info("Saved mapping path: " + file);
        LoggingManager.showWindowInfo("Successfully saved mapping");
    }
}
