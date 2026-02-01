package workbench.project.map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.collections.AbstractObjectsFolder;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.objects.templates.WBenchTemplate;

public class MapObjectTemplatesManager {
    private final MapObjectTemplatesFolder<WBenchObjectTemplate> entities;
    private final MapObjectTemplatesFolder<WBenchObjectTemplate> props;
    private final MapObjectTemplatesFolder<WBenchMarkerTemplate> markers;
    private final BiMap<String, ICubeMapProgram> skyBoxes;

    public MapObjectTemplatesManager() {
        this.entities = new MapObjectTemplatesFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.props = new MapObjectTemplatesFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.markers = new MapObjectTemplatesFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.skyBoxes = HashBiMap.create();
    }

    public void putPropWithRawPath(@NotNull String path, @NotNull WBenchObjectTemplate wBenchObjectTemplate) {
        this.getProps().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void putEntityWithRawPath(@NotNull String path, @NotNull WBenchObjectTemplate wBenchObjectTemplate) {
        this.getEntities().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void putMarkerWithRawPath(@NotNull String path, @NotNull WBenchMarkerTemplate wBenchObjectTemplate) {
        this.getMarkers().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void addSkyBox(String name, ICubeMapProgram cubeMapProgram) {
        this.getSkyBoxes().put(name, cubeMapProgram);
    }

    public void clear() {
        this.getEntities().reset();
        this.getProps().reset();
        this.getMarkers().reset();
        this.getSkyBoxes().clear();
    }

    public BiMap<String, ICubeMapProgram> getSkyBoxes() {
        return this.skyBoxes;
    }

    public MapObjectTemplatesFolder<WBenchObjectTemplate> getEntities() {
        return this.entities;
    }

    public MapObjectTemplatesFolder<WBenchObjectTemplate> getProps() {
        return this.props;
    }

    public MapObjectTemplatesFolder<WBenchMarkerTemplate> getMarkers() {
        return this.markers;
    }
}
