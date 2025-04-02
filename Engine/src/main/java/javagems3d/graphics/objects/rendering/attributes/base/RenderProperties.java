package javagems3d.graphics.objects.rendering.attributes.base;

import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class RenderProperties implements ICopyable<RenderProperties> {
    public Map<String, Object> propertiesMap;
    private final CullingRules cullingRules;

    public RenderProperties(CullingRules cullingRules) {
        this.cullingRules = cullingRules;
        this.propertiesMap = new HashMap<>();
        this.setDefaults();
    }

    protected abstract void setDefaults();

    public RenderProperties setValueFloat(String key, float value) {
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

    public float getFloat(String key) {
        return (float) this.propertiesMap.getOrDefault(key, -1.0f);
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
}
