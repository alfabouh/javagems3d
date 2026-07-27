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

package api.scripting.coding.env.internal.game.init.events.rendering;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.resources.instances.font.JSFont;
import api.scripting.coding.env.internal.util.resources.instances.font.JSFontStyles;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshBuffer;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshGroup;
import api.scripting.coding.env.internal.util.resources.instances.sound.JSOggSound;
import api.scripting.coding.env.internal.util.resources.instances.sound.JSSoundFormats;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2DProperties;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTextureCubeMap;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTextureCubeMapProperties;
import api.scripting.coding.env.internal.util.ui.JSUIDrawer;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

import java.awt.*;

@JSCodingClass(binding = "JSRenderUIEvent", description = "...")
public class JSRenderUIEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    private boolean cancel;

    @JSCodingField(description = "...")
    public JSUIDrawer uiDrawer;

    @JSCodingField(description = "...")
    public JSFrameTicking frameTicking;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSRenderUIEvent() {
    }

    @JSHideFromDoc
    public JSRenderUIEvent(JSUIDrawer jsuiDrawer, JSFrameTicking frameTicking) {
        this.uiDrawer = jsuiDrawer;
        this.frameTicking = frameTicking;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSUIDrawer getUiDrawer() {
        return this.uiDrawer;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSFrameTicking getFrameTicking() {
        return this.frameTicking;
    }

    @JSCodingFunctionOrMethod(description = "Check whether this event is cancelled.")
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @JSCodingFunctionOrMethod(description = "Set event cancellation state. If true, default behavior will not execute.", paramNames = {"cancelled"})
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSRenderUIEvent";
    }
}
