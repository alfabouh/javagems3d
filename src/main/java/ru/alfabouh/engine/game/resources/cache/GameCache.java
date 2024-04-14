package ru.alfabouh.engine.game.resources.cache;

import ru.alfabouh.engine.audio.sound.SoundBuffer;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.game.resources.assets.models.mesh.MeshDataGroup;

import java.util.HashMap;
import java.util.Map;

public class GameCache {
    private final Map<String, ICached> cache;

    public GameCache() {
        this.cache = new HashMap<>();
    }

    public void cleanCache() {
        this.cache.forEach((o, e) -> e.onCleaningCache(this));
        Game.getGame().getLogManager().log("Cleaned cache");
    }

    public void addObjectInBuffer(CacheResource cacheResource) {
        this.addObjectInBuffer(cacheResource.getKey(), cacheResource.getResource());
    }

    public void addObjectInBuffer(String key, ICached object) {
        if (object == null) {
            Game.getGame().getLogManager().warn("Couldn't add NULL object in game cache: " + key);
            return;
        }
        if (this.cache.containsKey(key)) {
            return;
        }
        Game.getGame().getLogManager().log("Put object " + key + " in game cache");
        this.cache.put(key, object);
    }

    public SoundBuffer getCachedSound(String key) {
        ICached cached = this.getCachedObject(key);
        if (cached != null && !(cached instanceof SoundBuffer)) {
            Game.getGame().getLogManager().warn("Object " + key + " is not a SoundBuffer2 in game cache!");
            return null;
        }
        return (SoundBuffer) cached;
    }

    public TextureSample getCachedTexture(String key) {
        ICached cached = this.getCachedObject(key);
        if (cached != null && !(cached instanceof TextureSample)) {
            Game.getGame().getLogManager().warn("Object " + key + " is not a TextureSample in game cache!");
            return null;
        }
        return (TextureSample) cached;
    }

    public MeshDataGroup getCachedMeshDataGroup(String key) {
        ICached cached = this.getCachedObject(key);
        if (cached != null && !(cached instanceof MeshDataGroup)) {
            Game.getGame().getLogManager().warn("Object " + key + " is not a MeshDataGroup in game cache!");
            return null;
        }
        return (MeshDataGroup) cached;
    }

    public ICached getCachedObject(String key) {
        ICached cached = this.cache.get(key);
        if (!this.checkObjectInCache(key)) {
            Game.getGame().getLogManager().warn("Object " + key + " doesn't exist in game cache!");
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