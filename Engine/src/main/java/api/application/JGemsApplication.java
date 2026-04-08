package api.application;

import api.application.events.IAppEventSubscriber;
import api.application.resources.IAppResources;
import api.application.scripts.IAppScriptContextRegistry;
import api.application.workbench.IWorkBenchSetup;
import api.system.scripting.JavaToJsAPI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.annotations.RequireEmptyConstructor;
import javagems3d.system.service.exceptions.JGemsAPIException;
import org.jetbrains.annotations.NotNull;

@RequireEmptyConstructor
public abstract class JGemsApplication implements IWorkBenchSetup {
    protected JGemsApplication() {
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