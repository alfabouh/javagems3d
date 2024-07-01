package ru.alfabouh.jgems3d.engine.system.resources.cache;

import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.HashMap;
import java.util.Map;

public class ResourceCache {
    private final Map<String, ICached> cache;

    public ResourceCache() {
        this.cache = new HashMap<>();
    }

    public void cleanCache() {
        this.cache.forEach((o, e) -> e.onCleaningCache(this));
        this.cache.clear();
        SystemLogging.get().getLogManager().log("Cleaned cache");
    }

    public Map<String, ICached> getCache() {
        return this.cache;
    }

    public void addObjectInBuffer(CacheResource cacheResource) {
        this.addObjectInBuffer(cacheResource.getKey(), cacheResource.getResource());
    }

    public void addObjectInBuffer(String key, ICached object) {
        if (object == null) {
            SystemLogging.get().getLogManager().warn("Couldn't add NULL object in system cache: " + key);
            return;
        }
        if (this.cache.containsKey(key)) {
            return;
        }
        SystemLogging.get().getLogManager().log("Put object " + key + " in system cache");
        this.cache.put(key, object);
    }

    public ICached getCachedObject(String key) {
        ICached cached = this.cache.get(key);
        if (!this.checkObjectInCache(key)) {
            SystemLogging.get().getLogManager().warn("Object " + key + " doesn't exist in system cache!");
            return null;
        }
        return cached;
    }

    @SuppressWarnings("all")
    public boolean checkObjectInCache(String key) {
        ICached cached = this.cache.get(key);
        if (cached == null) {
            return false;
        }
        return true;
    }
}