package javagems3d.graphics.objects.rendering.attributes.base;

import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RenderProperties implements ICopyable<RenderProperties> {
    public Map<String, Object> propertiesMap;
    private final CullingRules cullingRules;

    public RenderProperties(CullingRules cullingRules) {
        this.cullingRules = cullingRules;
        this.propertiesMap = new HashMap<>();
    }

    public RenderProperties setValueFloat(String key, double value) {
        this.propertiesMap.put(key, value);
        return this;
    }

    public RenderProperties setValueBool(String key, boolean value) {
        this.propertiesMap.put(key, value);
        return this;
    }

    public RenderProperties setValueInt(String key, int value) {
        this.propertiesMap.put(key, value);
        return this;
    }

    public boolean has(String key) {
        return this.propertiesMap.containsKey(key);
    }

    public double getFloat(String key) {
        return (double) this.propertiesMap.getOrDefault(key, -1.0f);
    }

    public boolean getBool(String key) {
        return (boolean) this.propertiesMap.getOrDefault(key, false);
    }

    public int getInt(String key) {
        return (int) this.propertiesMap.getOrDefault(key, -1);
    }

    public CullingRules getCullingRules() {
        return this.cullingRules;
    }

    protected void setPropertiesMap(@NotNull Map<String, Object> map) {
        this.propertiesMap = map;
    }

    protected Map<String, Object> getPropertiesMap() {
        return this.propertiesMap;
    }

    protected Map<String, Object> copyPropertiesMap() {
        return new HashMap<>(this.propertiesMap);
    }

    @Override
    public RenderProperties copy() {
        RenderProperties renderProperties = new RenderProperties(this.getCullingRules());
        renderProperties.setPropertiesMap(this.copyPropertiesMap());
        return renderProperties;
    }
}
