package javagems3d.system.resources.cache;

import javagems3d.system.service.path.JGemsPath;
import logger.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceCache {
    private final String cacheName;
    private final Map<String, ICached> cache;

    public ResourceCache(String cacheName) {
        this.cacheName = cacheName;
        Log.get().info("Created cache: " + this);
        this.cache = new LinkedHashMap<>();
    }

    public void clearGroupInCache(Class<? extends ICached> clazz) {
        Iterator<ICached> cachedIterator = this.cache.values().iterator();
        while (cachedIterator.hasNext()) {
            ICached cached = cachedIterator.next();
            if (clazz.isInstance(cached)) {
                cached.onClearingCache(this);
                cachedIterator.remove();
            }
        }
        Log.get().info("Cleaned cache: " + this + ". Group " + clazz.getName());
    }

    public void clearCache() {
        if (this.cache.isEmpty()) {
            return;
        }
        this.cache.forEach((o, e) -> e.onClearingCache(this));
        this.cache.clear();
        Log.get().info("Cleaned cache: " + this);
    }

    public Map<String, ICached> getCache() {
        return this.cache;
    }

    public void addObjectInBuffer(JGemsPath key, ICached object) {
        this.addObjectInBuffer(key.getFullPath(), object);
    }

    public void addObjectInBuffer(String key, ICached object) {
        if (object == null) {
            Log.get().error("Couldn't add NULL object in system cache: " + key + this);
            return;
        }
        if (this.cache.containsKey(key)) {
            return;
        }
        Log.get().debug("Put object " + key + " in system cache " + this);
        this.cache.put(key, object);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAllCachedObjectsCollection(Class<T> tClass) {
        return this.cache.values().stream().filter(tClass::isInstance).map(e -> (T) e).collect(Collectors.toList());
    }

    public ICached getCachedObject(JGemsPath key) {
        return this.getCachedObject(key.getFullPath());
    }

    public ICached getCachedObject(String key) {
        ICached cached = this.cache.get(key);
        if (!this.checkObjectInCache(key)) {
            Log.get().error("Object " + key + " doesn't exist in system cache " + this);
            return null;
        }
        return cached;
    }

    @SuppressWarnings("all")
    public <T extends ICached> T getCachedObjectUnSafeCast(String key) {
        return (T) this.getCachedObject(key);
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