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

package workbench.project.map;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import java.util.*;

public class MapObjectTemplatesManager {
    private final MapObjectTemplatesFolder<WBenchObjectTemplate> entities;
    private final MapObjectTemplatesFolder<WBenchObjectTemplate> props;
    private final MapObjectTemplatesFolder<WBenchMarkerTemplate> markers;
    private final Map<String, SkyBoxTemplate> skyBoxes;
    private final Map<ICubeMapProgram, SkyBoxTemplate> skyBoxesCache;

    public MapObjectTemplatesManager() {
        this.entities = new MapObjectTemplatesFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.props = new MapObjectTemplatesFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.markers = new MapObjectTemplatesFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.skyBoxes = new LinkedHashMap<>();
        this.skyBoxesCache = new HashMap<>();
    }

    public void putPropWithRawPath(@NotNull String path, @NotNull WBenchObjectTemplate wBenchObjectTemplate) {
        this.getProps().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void putEntityWithRawPath(@NotNull String path, @NotNull WBenchObjectTemplate wBenchObjectTemplate) {
        this.getEntities().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void putMarkerWithRawPath(@NotNull String path, @NotNull WBenchMarkerTemplate wBenchObjectTemplate) {
        this.getMarkers().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void addSkyBox(String name, SkyBoxTemplate cubeMapProgram) {
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        this.getSkyBoxes().put(name, cubeMapProgram);
        this.getSkyBoxesCache().put(cubeMapProgram.getCubeMapProgram(), cubeMapProgram);
    }

    public void clear() {
        this.getEntities().reset();
        this.getProps().reset();
        this.getMarkers().reset();
        this.getSkyBoxes().clear();
        this.getSkyBoxesCache().clear();
    }

    public Map<ICubeMapProgram, SkyBoxTemplate> getSkyBoxesCache() {
        return this.skyBoxesCache;
    }

    public Map<String, SkyBoxTemplate> getSkyBoxes() {
        return this.skyBoxes;
    }

    public MapObjectTemplatesFolder<WBenchObjectTemplate> getEntities() {
        return this.entities;
    }

    public MapObjectTemplatesFolder<WBenchObjectTemplate> getProps() {
        return this.props;
    }

    public MapObjectTemplatesFolder<WBenchMarkerTemplate> getMarkers() {
        return this.markers;
    }

    public static class SkyBoxTemplate {
        private final String nameId;
        private final ICubeMapProgram.CMTextures cmTextures;
        private ICubeMapProgram cubeMapProgram;

        public SkyBoxTemplate(String nameId, ICubeMapProgram.CMTextures cmTextures) {
            if (!nameId.startsWith("/")) {
                nameId = "/" + nameId;
            }
            this.nameId = nameId;
            this.cmTextures = cmTextures;
        }

        public String getNameId() {
            return this.nameId;
        }

        public ICubeMapProgram.CMTextures getCmTextures() {
            return this.cmTextures;
        }

        public ICubeMapProgram getCubeMapProgram() {
            return this.cubeMapProgram;
        }

        public SkyBoxTemplate setCubeMapProgram(ICubeMapProgram cubeMapProgram) {
            this.cubeMapProgram = cubeMapProgram;
            return this;
        }
    }
}
