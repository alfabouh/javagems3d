package ru.alfabouh.engine.system.localisation;

import java.util.HashMap;
import java.util.Map;

public class LangMap {
    private final Map<String, String> langMap;

    public LangMap() {
        this.langMap = new HashMap<>();
    }

    public void addPair(String key, String value) {
        this.getLangMap().put(key, value);
    }

    public String getValue(String key) {
        return this.getLangMap().get(key);
    }

    public boolean isKeyExist(String key) {
        return this.getLangMap().containsKey(key);
    }

    protected Map<String, String> getLangMap() {
        return this.langMap;
    }
}
