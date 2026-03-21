package api.scripting.coding.env.internal.util.resources.cache;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.misc.JSString;
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
    private JSSystemResources(SystemResources systemResources) {
        this.systemResources = systemResources;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public ResourceCache getMemoryResourceCache() {
        return this.systemResources.getResourceCache();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clearObjectFromMemoryCacheByKey(JSString key) {
        this.getMemoryResourceCache().clearObjectFromCache(key.string());
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"key", "object"})
    public void registerObjectInMemoryCache(JSString key, JSCanBeCachedInMemory object) {
        if (object instanceof ICached cached) {
            this.getMemoryResourceCache().registerInCache(key.string(), cached);
        } else {
            Log.get().error("Cannot register in cache this type of object");
        }
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"key"})
    public JSCanBeCachedInMemory getObjectFromMemoryCacheByKey(JSString key) {
        return this.getJavaSystemResources().getResource(key.string());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public SystemResources getJavaSystemResources() {
        return this.systemResources;
    }
}
