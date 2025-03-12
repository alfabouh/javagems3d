package workbench.project;

import javagems3d.help.JGemsUtils;
import workbench.graphics.objects.templates.WBenchObjectTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectObjects {
    private final Map<String, Set<WBenchObjectTemplate>> entityGroups;

    public ProjectObjects() {
        this.entityGroups = new HashMap<>();
    }

    public void addEntity(String group, WBenchObjectTemplate wBenchObjectTemplate) {
        JGemsUtils.putObjectInMapOrUpdate(this.getEntityGroups(), group, new HashSet<WBenchObjectTemplate>() {{ add(wBenchObjectTemplate); }}, (ex, nw) -> {
            ex.add(wBenchObjectTemplate);
            return ex;
        }, wBenchObjectTemplate);
    }

    public void clear() {
        this.getEntityGroups().clear();
    }

    public Map<String, Set<WBenchObjectTemplate>> getEntityGroups() {
        return this.entityGroups;
    }
}
