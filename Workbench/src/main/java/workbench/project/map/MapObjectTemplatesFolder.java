package workbench.project.map;

import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.templates.WBenchTemplate;
import javagems3d.system.service.files.VirtualObjectsFolder;

public class MapObjectTemplatesFolder<T extends WBenchTemplate> extends VirtualObjectsFolder<T> {
    public MapObjectTemplatesFolder(@NotNull String name) {
        super(name);
    }
}