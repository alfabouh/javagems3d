package workbench.project;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.collections.Pair;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectObjects {
    private final Map<String, Set<WBenchObjectTemplate>> entityGroups;
    private final Map<String, Set<WBenchObjectTemplate>> propGroups;
    private final Map<String, Set<WBenchMarkerTemplate>> markerGroups;
    private final Set<Pair<String, ICubeMapProgram>> skyBoxes;

    public ProjectObjects() {
        this.entityGroups = new HashMap<>();
        this.propGroups = new HashMap<>();
        this.markerGroups = new HashMap<>();
        this.skyBoxes = new HashSet<>();
    }

    public void addProp(String group, WBenchObjectTemplate wBenchObjectTemplate) {
        JGemsUtils.putObjectInMapOrUpdate(this.getPropGroups(), group, new HashSet<WBenchObjectTemplate>() {{ add(wBenchObjectTemplate); }}, (ex, nw) -> {
            ex.add(wBenchObjectTemplate);
            return ex;
        }, wBenchObjectTemplate);
    }

    public void addEntity(String group, WBenchObjectTemplate wBenchObjectTemplate) {
        JGemsUtils.putObjectInMapOrUpdate(this.getEntityGroups(), group, new HashSet<WBenchObjectTemplate>() {{ add(wBenchObjectTemplate); }}, (ex, nw) -> {
            ex.add(wBenchObjectTemplate);
            return ex;
        }, wBenchObjectTemplate);
    }

    public void addMarker(String group, WBenchMarkerTemplate wBenchObjectTemplate) {
        JGemsUtils.putObjectInMapOrUpdate(this.getMarkerGroups(), group, new HashSet<WBenchMarkerTemplate>() {{ add(wBenchObjectTemplate); }}, (ex, nw) -> {
            ex.add(wBenchObjectTemplate);
            return ex;
        }, wBenchObjectTemplate);
    }

    public void addSkyBox(String name, ICubeMapProgram cubeMapProgram) {
        this.getSkyBoxes().add(new Pair<>(name, cubeMapProgram));
    }

    public void clear() {
        this.getEntityGroups().clear();
        this.getPropGroups().clear();
        this.getMarkerGroups().clear();
        this.getSkyBoxes().clear();
    }

    public Set<Pair<String, ICubeMapProgram>> getSkyBoxes() {
        return this.skyBoxes;
    }

    public Map<String, Set<WBenchMarkerTemplate>> getMarkerGroups() {
        return this.markerGroups;
    }

    public Map<String, Set<WBenchObjectTemplate>> getEntityGroups() {
        return this.entityGroups;
    }

    public Map<String, Set<WBenchObjectTemplate>> getPropGroups() {
        return this.propGroups;
    }
}
