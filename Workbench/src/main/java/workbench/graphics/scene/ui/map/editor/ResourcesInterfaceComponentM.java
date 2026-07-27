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

package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.scenes.resources.InterfaceResourcesObjectsM;

public class ResourcesInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final InterfaceResourcesObjectsM interfaceResourcesObjectsM;

    public ResourcesInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.interfaceResourcesObjectsM = new InterfaceResourcesObjectsM(mapEditorInterface);
    }

    public void clear() {
        this.getSceneResourcesObjectsM().reset();
    }

    public void resourcesContent() {
        this.getSceneResourcesObjectsM().render();
    }

    public InterfaceResourcesObjectsM getSceneResourcesObjectsM() {
        return this.interfaceResourcesObjectsM;
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
