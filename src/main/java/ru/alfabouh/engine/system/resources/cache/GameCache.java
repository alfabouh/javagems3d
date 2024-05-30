package ru.alfabouh.engine.system.resources.cache;

import ru.alfabouh.engine.audio.sound.SoundBuffer;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.system.resources.assets.models.mesh.MeshDataGroup;

import java.util.HashMap;
import java.util.Map;

public class GameCache {
    private final Map<String, ICached> cache;

    public GameCache() {
        this.cache = new HashMap<>();
    }

    public void cleanCache() {
        this.cache.forEach((o, e) -> e.onCleaningCache(this));
        this.cache.clear();
        JGems.get().getLogManager().log("Cleaned cache");
    }

    public Map<String, ICached> getCache() {
        return this.cache;
    }

    public void addObjectInBuffer(CacheResource cacheResource) {
        this.addObjectInBuffer(cacheResource.getKey(), cacheResource.getResource());
    }

    public void addObjectInBuffer(String key, ICached object) {
        if (object == null) {
            JGems.get().getLogManager().warn("Couldn't add NULL object in system cache: " + key);
            return;
        }
        if (this.cache.containsKey(key)) {
            return;
        }
        JGems.get().getLogManager().log("Put object " + key + " in system cache");
        this.cache.put(key, object);
    }

    public SoundBuffer getCachedSound(String key) {
        ICached cached = this.getCachedObject(key);
        if (cached != null && !(cached instanceof SoundBuffer)) {
            JGems.get().getLogManager().warn("Object " + key + " is not a SoundBuffer2 in system cache!");
            return null;
        }
        return (SoundBuffer) cached;
    }

    public TextureSample getCachedTexture(String key) {
        ICached cached = this.getCachedObject(key);
        if (cached != null && !(cached instanceof TextureSample)) {
            JGems.get().getLogManager().warn("Object " + key + " is not a TextureSample in system cache!");
            return null;
        }
        return (TextureSample) cached;
    }

    public MeshDataGroup getCachedMeshDataGroup(String key) {
        ICached cached = this.getCachedObject(key);
        if (cached != null && !(cached instanceof MeshDataGroup)) {
            JGems.get().getLogManager().warn("Object " + key + " is not a MeshDataGroup in system cache!");
            return null;
        }
        return (MeshDataGroup) cached;
    }

    public ICached getCachedObject(String key) {
        ICached cached = this.cache.get(key);
        if (!this.checkObjectInCache(key)) {
            JGems.get().getLogManager().warn("Object " + key + " doesn't exist in system cache!");
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