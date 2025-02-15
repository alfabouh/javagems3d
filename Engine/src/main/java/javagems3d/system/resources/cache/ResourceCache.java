/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.cache;

import javagems3d.JGemsHelper;
import javagems3d.system.service.path.JGemsPath;

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
        JGemsHelper.getLogger().info("Created cache: " + this);
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
        JGemsHelper.getLogger().info("Cleaned cache: " + this + ". Group " + clazz.getName());
    }

    public void clearCache() {
        if (this.cache.isEmpty()) {
            return;
        }
        this.cache.forEach((o, e) -> e.onClearingCache(this));
        this.cache.clear();
        JGemsHelper.getLogger().info("Cleaned cache: " + this);
    }

    public Map<String, ICached> getCache() {
        return this.cache;
    }

    public void addObjectInBuffer(JGemsPath key, ICached object) {
        this.addObjectInBuffer(key.getFullPath(), object);
    }

    public void addObjectInBuffer(String key, ICached object) {
        if (object == null) {
            JGemsHelper.getLogger().error("Couldn't add NULL object in system cache: " + key + this);
            return;
        }
        if (this.cache.containsKey(key)) {
            return;
        }
        JGemsHelper.getLogger().debug("Put object " + key + " in system cache " + this);
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
            JGemsHelper.getLogger().error("Object " + key + " doesn't exist in system cache " + this);
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