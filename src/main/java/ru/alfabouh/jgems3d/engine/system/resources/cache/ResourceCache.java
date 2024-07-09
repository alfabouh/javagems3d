package ru.alfabouh.jgems3d.engine.system.resources.cache;

import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.*;
import java.util.stream.Collectors;

public class ResourceCache {
    private final String cacheName;
    private final Map<String, ICached> cache;

    public ResourceCache(String cacheName) {
        SystemLogging.get().getLogManager().warn("Created cache: " + this);
        this.cache = new LinkedHashMap<>();
        this.cacheName = cacheName;
    }

    public void cleanGroupInCache(Class<? extends ICached> clazz) {
        Iterator<ICached> cachedIterator = this.cache.values().iterator();
        while (cachedIterator.hasNext()) {
            ICached cached = cachedIterator.next();
            if (clazz.isInstance(cached)) {
                cached.onCleaningCache(this);
                cachedIterator.remove();
            }
        }
        SystemLogging.get().getLogManager().log("Cleaned cache: " + this + ". Group " + clazz.getName());
    }

    public void cleanCache() {
        if (this.cache.isEmpty()) {
            return;
        }
        this.cache.forEach((o, e) -> e.onCleaningCache(this));
        this.cache.clear();
        SystemLogging.get().getLogManager().log("Cleaned cache: " + this);
    }

    public Map<String, ICached> getCache() {
        return this.cache;
    }

    public void addObjectInBuffer(CacheResource cacheResource) {
        this.addObjectInBuffer(cacheResource.getKey(), cacheResource.getResource());
    }

    public void addObjectInBuffer(String key, ICached object) {
        if (object == null) {
            SystemLogging.get().getLogManager().warn("Couldn't add NULL object in system cache: " + key + this);
            return;
        }
        if (this.cache.containsKey(key)) {
            return;
        }
        SystemLogging.get().getLogManager().log("Put object " + key + " in system cache " + this);
        this.cache.put(key, object);
    }

    @SuppressWarnings("unchecked")
    public <T extends ICached> List<T> getAllCachedObjectsCollection(Class<T> tClass) {
        return this.cache.values().stream().filter(tClass::isInstance).map(e -> (T) e).collect(Collectors.toList());
    }

    public ICached getCachedObject(String key) {
        ICached cached = this.cache.get(key);
        if (!this.checkObjectInCache(key)) {
            SystemLogging.get().getLogManager().warn("Object " + key + " doesn't exist in system cache " + this);
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

    @Override
    public String toString() {
        return "(" + this.cacheName + ")";
    }
}