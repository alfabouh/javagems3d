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

package workbench.resources;

import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.SystemResources;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.resources.frame.LoadingInterfaceSwing;

import java.util.function.Consumer;

public class WBenchResources extends SystemResources {
    public WBenchResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected @Nullable Consumer<ResLoadSysMessage> getMessagesConsumer() {
        return (e) -> {
            if (LoadingInterfaceSwing.valid()) {
                LoadingInterfaceSwing.setResource(e.text());
            }
            if (e.resLoadSysMessageType().equals(ResLoadSysMessageType.ERR)) {
                Log.get().error(e.text());
            } else {
                Log.get().trace(e.text());
            }
        };
    }
}
