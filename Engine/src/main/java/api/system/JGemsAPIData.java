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

package api.system;

import api.application.JGemsApplication;
import api.application.events.AppEventSubscriber;
import api.application.resources.AppResources;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.core.JGemsCore;

public final class JGemsAPIData {
    private String id;
    private AppResources appResources;
    private AppEventSubscriber appEventSubscriber;
    private JGemsApplication jGemsApplication;

    public JGemsAPIData() {
    }

    void setApplication(JGemsApplication application) {
        this.jGemsApplication = application;
    }

    //***********************************
    public void preInit(JGemsCore engineSystem) {
        this.jGemsApplication.preInit(engineSystem);
    }

    public void postInit(JGemsCore engineSystem) {
        this.jGemsApplication.postInit(engineSystem);
    }
    //***********************************

    public Window.WindowProperties getWindowProperties() {
        return this.jGemsApplication.getWindowProperties();
    }

    public BindingManager getBindingManager() {
        return this.jGemsApplication.getBindingManager();
    }

    public PanelUI getMainMenuPanel() {
        return this.jGemsApplication.getMainMenuPanel();
    }
    //***********************************

    //***********************************
    public AppEventSubscriber getAppEventSubscriber() {
        return this.appEventSubscriber;
    }

    //***********************************
    void setAppEventSubscriber(AppEventSubscriber appEventSubscriber) {
        this.appEventSubscriber = appEventSubscriber;
    }

    public String getId() {
        return this.id;
    }

    void setId(String id) {
        this.id = id;
    }

    public AppResources getAppResources() {
        return this.appResources;
    }

    void setAppResources(AppResources appResources) {
        this.appResources = appResources;
    }
    //***********************************
}