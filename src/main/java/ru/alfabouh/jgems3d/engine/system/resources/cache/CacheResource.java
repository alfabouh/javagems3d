package ru.alfabouh.jgems3d.engine.system.resources.cache;

public class CacheResource {
    private final String key;
    private final ICached resource;

    public CacheResource(String key, ICached resource) {
        this.key = key;
        this.resource = resource;
    }

    public String getKey() {
        return this.key;
    }

    public ICached getResource() {
        return this.resource;
    }
}
