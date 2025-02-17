package api.newer.system;

import api.newer.application.JGemsApplication;
import api.newer.application.events.AppEventSubscriber;
import api.newer.application.resources.AppResources;
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

    //***********************************
    void setAppEventSubscriber(AppEventSubscriber appEventSubscriber) {
        this.appEventSubscriber = appEventSubscriber;
    }

    void setId(String id) {
        this.id = id;
    }

    void setAppResources(AppResources appResources) {
        this.appResources = appResources;
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
    public AppEventSubscriber getAppEventSubscriber() {
        return this.appEventSubscriber;
    }

    public String getId() {
        return this.id;
    }

    public AppResources getAppResources() {
        return this.appResources;
    }
    //***********************************
}