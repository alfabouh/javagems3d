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

package javagems3d.system.resources.managing.resources;

import javagems3d.JGems3D;
import javagems3d.system.resources.cache.ResourceCache;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class JGemsSystemResources extends SystemResources {
    public JGemsSystemResources(@NotNull ResourceCache resourceCache) {
        super(resourceCache);
    }

    @Override
    protected @Nullable Consumer<ResLoadSysMessage> getMessagesConsumer() {
        return (e) -> {
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(e.color(), e.text());
            if (e.resLoadSysMessageType().equals(ResLoadSysMessageType.ERR)) {
                Log.get().error(e.text());
            } else {
                Log.get().trace(e.text());
            }
        };
    }
}
