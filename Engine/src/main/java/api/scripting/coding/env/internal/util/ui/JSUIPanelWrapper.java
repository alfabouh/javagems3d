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

package api.scripting.coding.env.internal.util.ui;

import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.Nullable;

@JSHideFromDoc
public class JSUIPanelWrapper implements PanelUI {
    private @Nullable Runnable onConstruct;
    private @Nullable Runnable onDestruct;
    private @Nullable Runnable drawPanel;
    private @Nullable Runnable onWindowResize;
    @JSHideFromDoc
    private String panelId;

    @JSHideFromDoc
    public JSUIPanelWrapper(String panelId) {
        this.panelId = panelId;
    }

    public JSUIPanelWrapper setOnConstruct(@Nullable Runnable onConstruct) {
        this.onConstruct = onConstruct;
        return this;
    }

    public JSUIPanelWrapper setOnDestruct(@Nullable Runnable onDestruct) {
        this.onDestruct = onDestruct;
        return this;
    }

    public JSUIPanelWrapper setDrawPanel(@Nullable Runnable drawPanel) {
        this.drawPanel = drawPanel;
        return this;
    }

    public JSUIPanelWrapper setOnWindowResize(@Nullable Runnable onWindowResize) {
        this.onWindowResize = onWindowResize;
        return this;
    }

    @Override
    public void onConstruct(JGemsUI ui) {
        if (this.onConstruct != null) {
            this.onConstruct.run();
        }
    }

    @Override
    public void onDestruct(JGemsUI ui) {
        if (this.onDestruct != null) {
            this.onDestruct.run();
        }
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        if (this.drawPanel != null) {
            this.drawPanel.run();
        }
    }

    @JSCodingFunctionOrMethod(description = "Panel ID")
    @Override
    public String getPanelID() {
        return this.panelId;
    }

    @Override
    public void onWindowResize(IWindow window) {
        if (this.onWindowResize != null) {
            this.onWindowResize.run();
        }
    }
}
