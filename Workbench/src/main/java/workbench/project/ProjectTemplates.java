package workbench.project;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.collections.Pair;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.objects.templates.WBenchTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectTemplates {
    private final Map<String, TemplatesTable<WBenchObjectTemplate>> entityGroups;
    private final Map<String, TemplatesTable<WBenchObjectTemplate>> propGroups;
    private final Map<String, TemplatesTable<WBenchMarkerTemplate>> markerGroups;
    private final BiMap<String, ICubeMapProgram> skyBoxes;

    public ProjectTemplates() {
        this.entityGroups = new HashMap<>();
        this.propGroups = new HashMap<>();
        this.markerGroups = new HashMap<>();
        this.skyBoxes = HashBiMap.create();
    }

    public void addProp(String group, WBenchObjectTemplate wBenchObjectTemplate) {
        JGemsUtils.putObjectInMapOrUpdate(this.getPropGroups(), group, new TemplatesTable<>(wBenchObjectTemplate), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, wBenchObjectTemplate);
    }

    public void addEntity(String group, WBenchObjectTemplate wBenchObjectTemplate) {
        JGemsUtils.putObjectInMapOrUpdate(this.getEntityGroups(), group, new TemplatesTable<>(wBenchObjectTemplate), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, wBenchObjectTemplate);
    }

    public void addMarker(String group, WBenchMarkerTemplate wBenchObjectTemplate) {
        JGemsUtils.putObjectInMapOrUpdate(this.getMarkerGroups(), group, new TemplatesTable<>(wBenchObjectTemplate), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, wBenchObjectTemplate);
    }

    public void addSkyBox(String name, ICubeMapProgram cubeMapProgram) {
        this.getSkyBoxes().put(name, cubeMapProgram);
    }

    public void clear() {
        this.getEntityGroups().clear();
        this.getPropGroups().clear();
        this.getMarkerGroups().clear();
        this.getSkyBoxes().clear();
    }

    public BiMap<String, ICubeMapProgram> getSkyBoxes() {
        return this.skyBoxes;
    }

    public Map<String, TemplatesTable<WBenchMarkerTemplate>> getMarkerGroups() {
        return this.markerGroups;
    }

    public Map<String, TemplatesTable<WBenchObjectTemplate>> getEntityGroups() {
        return this.entityGroups;
    }

    public Map<String, TemplatesTable<WBenchObjectTemplate>> getPropGroups() {
        return this.propGroups;
    }

    public static class TemplatesTable<T extends WBenchTemplate> {
        private final Map<String, T> templateMap;

        @SuppressWarnings("all")
        public TemplatesTable(WBenchTemplate wBenchTemplate) {
            this();
            this.getTemplateMap().put(wBenchTemplate.getObjectId().getNameId(), (T) wBenchTemplate);
        }

        public TemplatesTable() {
            this.templateMap = new HashMap<>();
        }

        @SuppressWarnings("all")
        public void add(WBenchTemplate wBenchTemplate) {
            this.getTemplateMap().put(wBenchTemplate.getObjectId().getNameId(), (T) wBenchTemplate);
        }

        public T find(String id) {
            return this.getTemplateMap().get(id);
        }

        public Map<String, T> getTemplateMap() {
            return this.templateMap;
        }
    }
}
