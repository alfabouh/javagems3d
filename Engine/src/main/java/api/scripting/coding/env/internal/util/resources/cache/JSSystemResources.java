package api.scripting.coding.env.internal.util.resources.cache;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import logger.Log;

@JSCodingClass(binding = "JSSystemResources", description = "...")
public class JSSystemResources {
    @JSHideFromDoc
    private final SystemResources systemResources;

    @JSHideFromDoc
    public JSSystemResources(SystemResources systemResources) {
        this.systemResources = systemResources;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public ResourceCache getMemoryResourceCache() {
        return this.systemResources.getResourceCache();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clearObjectFromMemoryCacheByKey(String key) {
        this.getMemoryResourceCache().clearObjectFromCache(key);
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"key", "object"})
    public void registerObjectInMemoryCache(String key, JSCanBeCachedInMemory object) {
        if (object instanceof ICached cached) {
            this.getMemoryResourceCache().registerInCache(key, cached);
        } else {
            Log.get().error("Cannot register in cache this type of object");
        }
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"key"})
    public JSCanBeCachedInMemory getObjectFromMemoryCacheByKey(String key) {
        return this.getJavaSystemResources().getResource(key);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public SystemResources getJavaSystemResources() {
        return this.systemResources;
    }
}
