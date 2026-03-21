package javagems3d.system.resources.assets.shaders.constants;

import java.util.HashMap;
import java.util.Map;

public final class ShaderStaticConstants {
    private final Map<String, String> cnstMap;

    public ShaderStaticConstants() {
        this.cnstMap = new HashMap<>();
    }

    public void createConstant(String key, String value) {
        this.getCnstMap().put(key.replaceAll("CONST.", ""), value);
    }

    public String getValue(String key) {
        return this.getCnstMap().get(key);
    }

    public void clear() {
        this.getCnstMap().clear();
    }

    public Map<String, String> getCnstMap() {
        return this.cnstMap;
    }
}