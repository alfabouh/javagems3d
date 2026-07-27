/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.scripting.coding.env.internal.util.resources.cache;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import logger.Log;

@JSCodingClass(binding = "JSSystemResources", description = "Wrapper for accessing and managing the game's system resources from scripts, including memory cache operations.")
public class JSSystemResources {
    @JSHideFromDoc
    private final SystemResources systemResources;

    @JSHideFromDoc
    public JSSystemResources(SystemResources systemResources) {
        this.systemResources = systemResources;
    }

    @JSCodingFunctionOrMethod(description = "Returns the internal resource cache used for memory management.")
    public ResourceCache getMemoryResourceCache() {
        return this.systemResources.getResourceCache();
    }

    @JSCodingFunctionOrMethod(description = "Removes an object from the memory cache by its key.")
    public void clearObjectFromMemoryCacheByKey(String key) {
        this.getMemoryResourceCache().clearObjectFromCache(key);
    }

    @JSCodingFunctionOrMethod(description = "Registers an object in the memory cache if it implements caching interface.", paramNames = {"key", "object"})
    public void registerObjectInMemoryCache(String key, JSCanBeCachedInMemory object) {
        if (object instanceof ICached cached) {
            this.getMemoryResourceCache().registerInCache(key, cached);
        } else {
            Log.get().error("Cannot register in cache this type of object");
        }
    }

    @JSCodingFunctionOrMethod(description = "Retrieves an object from the memory cache by its key.", paramNames = {"key"})
    public JSCanBeCachedInMemory getObjectFromMemoryCacheByKey(String key) {
        return this.getJavaSystemResources().getResource(key);
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java SystemResources instance.")
    public SystemResources getJavaSystemResources() {
        return this.systemResources;
    }
}