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

package workbench.resources.initialization.map;

import api.application.workbench.manager.APIWBenchDataManager;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.external.gaming.JGemsGameInstance;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.VirtualObjectsFolder;
import workbench.WBench;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import javagems3d.system.external.gaming.def.misc.GameResourceSkyboxAsset;
import workbench.project.map.MapObjectTemplatesManager;

import java.util.*;

public class WBenchMapEditorTextureAssetsInitializer implements IAssetsInitializer {
    public static <E extends GameResourceSkyboxAsset> void parseTreeS(SystemResources systemResources, VirtualObjectsFolder<E> folder, MapObjectTemplatesManager manager) {
        for (E asset : folder.getObjectsThere()) {
            final String path = ((folder.getParent() == null ? "" : folder.getHierarchy() + "/") + asset.name());
            ICubeMapProgram cubeMapProgram = systemResources.createCubeMapTexture(null, new CubeMapsLoader.CubeMapTexturesContainer(JGemsGameInstance.getTexturesFolder(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath()), asset.getCmTextures()), new CubeMapTexture.Properties(true));
            manager.addSkyBox(path, new MapObjectTemplatesManager.SkyBoxTemplate(path, asset.getCmTextures()).setCubeMapProgram(cubeMapProgram));
        }
        for (VirtualObjectsFolder<E> child : folder.getFoldersThere()) {
            WBenchMapEditorTextureAssetsInitializer.parseTreeS(systemResources, child, manager);
        }
    }

    public void load(SystemResources systemResources) {
        final APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();
        final GameResourceAssetsFolder<GameResourceSkyboxAsset> skyboxAssets = WBench.get().getGameProjectManager().getGameResourcesManager().getSkyBoxesAssetsFolder();
        WBenchMapEditorTextureAssetsInitializer.parseTreeS(systemResources, skyboxAssets, WBench.get().getMapProjectManager().getMapObjectTemplates());
        Map<String, ICubeMapProgram.CMTextures> map = apiwBenchDataManager.getSkyBoxesMap();
        for (Map.Entry<String, ICubeMapProgram.CMTextures> entry : map.entrySet()) {
            ICubeMapProgram cubeMapProgram = systemResources.createCubeMapTexture(null, new CubeMapsLoader.CubeMapTexturesContainer(entry.getValue()), new CubeMapTexture.Properties(true));
            if (cubeMapProgram != null) {
                WBench.get().getMapProjectManager().getMapObjectTemplates().addSkyBox(entry.getKey(), new MapObjectTemplatesManager.SkyBoxTemplate(entry.getKey(), entry.getValue()).setCubeMapProgram(cubeMapProgram));
            }
        }
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.HIGH;
    }
}
