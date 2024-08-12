package ru.jgems3d.engine.system.resources.cache;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.path.JGemsPath;

import java.util.*;
import java.util.stream.Collectors;

public class ResourceCache {
    private final String cacheName;
    private final Map<String, ICached> cache;

    public ResourceCache(String cacheName) {
        this.cacheName = cacheName;
        JGemsHelper.getLogger().log("Created cache: " + this);
        this.cache = new LinkedHashMap<>();
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
        JGemsHelper.getLogger().log("Cleaned cache: " + this + ". Group " + clazz.getName());
    }

    public void cleanCache() {
        if (this.cache.isEmpty()) {
            return;
        }
        this.cache.forEach((o, e) -> e.onCleaningCache(this));
        this.cache.clear();
        JGemsHelper.getLogger().log("Cleaned cache: " + this);
    }

    public Map<String, ICached> getCache() {
        return this.cache;
    }

    public void addObjectInBuffer(JGemsPath key, ICached object) {
        this.addObjectInBuffer(key.getFullPath(), object);
    }

    public void addObjectInBuffer(String key, ICached object) {
        if (object == null) {
            JGemsHelper.getLogger().warn("Couldn't add NULL object in system cache: " + key + this);
            return;
        }
        if (this.cache.containsKey(key)) {
            return;
        }
        JGemsHelper.getLogger().log("Put object " + key + " in system cache " + this);
        this.cache.put(key, object);
    }

    @SuppressWarnings("unchecked")
    public <T extends ICached> List<T> getAllCachedObjectsCollection(Class<T> tClass) {
        return this.cache.values().stream().filter(tClass::isInstance).map(e -> (T) e).collect(Collectors.toList());
    }

    public ICached getCachedObject(JGemsPath key) {
        return this.getCachedObject(key.getFullPath());
    }

    public ICached getCachedObject(String key) {
        ICached cached = this.cache.get(key);
        if (!this.checkObjectInCache(key)) {
            JGemsHelper.getLogger().warn("Object " + key + " doesn't exist in system cache " + this);
            return null;
        }
        return cached;
    }

    public boolean checkObjectInCache(JGemsPath key) {
        return this.checkObjectInCache(key.getFullPath());
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