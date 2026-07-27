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

package launcher;

import javagems3d.JGems3D;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import launcher.run.EngineRun;
import launcher.run.WorkbenchRun;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JavaGemsLauncher {
    public static void launchEngine(@NotNull JGemsLaunchArgsRegistry jGemsLaunchArgsRegistry) {
        new EngineRun().run(jGemsLaunchArgsRegistry);
    }

    public static void launchWorkbench(@NotNull JGemsLaunchArgsRegistry jGemsLaunchArgsRegistry) {
        new WorkbenchRun().run(jGemsLaunchArgsRegistry);
    }

    public static void launchJGemsIsolatedProcess(String[] args) {
        JGems3D.IsolatedProcessLauncher.EXEC(args);
    }

    public static void launch(String[] args, @Nullable Class<?> apiAppClass) {
        JGemsLaunchArgsRegistry.INSTANCE.read(args);
        if (apiAppClass != null) {
            JGemsLaunchArgsRegistry.INSTANCE.putManually(JGemsLaunchArgsRegistry.DEFAULT_ARGS.API_APP_CLASSPATH.argument(), apiAppClass.getCanonicalName());
        }
        if (JGemsLaunchArgsRegistry.INSTANCE.getValue(JGemsLaunchArgsRegistry.DEFAULT_ARGS.WORKBENCH) == Boolean.TRUE) {
            JavaGemsLauncher.launchWorkbench(JGemsLaunchArgsRegistry.INSTANCE);
            return;
        }
        JavaGemsLauncher.launchEngine(JGemsLaunchArgsRegistry.INSTANCE);
    }
}
