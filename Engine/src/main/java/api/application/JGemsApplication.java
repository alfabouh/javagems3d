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

package api.application;

import api.application.events.IAppEventSubscriber;
import api.application.resources.IAppResources;
import api.application.scripts.IAppScriptContextRegistry;
import api.application.workbench.IWorkBenchSetup;
import api.scripting.JavaToJsAPI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import javagems3d.system.service.annotations.RequireEmptyConstructor;
import javagems3d.system.service.exceptions.JGemsAPIException;
import org.jetbrains.annotations.NotNull;

@RequireEmptyConstructor
public abstract class JGemsApplication implements IWorkBenchSetup {
    protected JGemsApplication(JGemsLaunchArgsRegistry args) {
    }

    public void preInit(@NotNull JGemsCore engineSystem) {
    }

    public void postInit(@NotNull JGemsCore engineSystem) {
    }

    public abstract void initScripts(@NotNull IAppScriptContextRegistry appScriptContextRegistry);

    public abstract void initEvents(@NotNull IAppEventSubscriber appEventSubscriber);

    public abstract void initResources(@NotNull IAppResources appResources);

    public abstract @NotNull BindingManager getBindingManager();

    public @NotNull PanelUI getMainMenuPanel() {
        if (JavaToJsAPI.uiContainer.getMainMenuPanel() == null) {
            throw new JGemsAPIException("Main Menu is NULL!");
        }
        return JavaToJsAPI.uiContainer.getMainMenuPanel().second();
    }

    public abstract @NotNull Window.WindowProperties getWindowProperties();
}