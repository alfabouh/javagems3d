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

package workbench.resources.initialization.game;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

public class WBenchGameEditorModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup markerDefault;
    public MeshGroup markerCursor;
    public MeshGroup markerAabb;
    public MeshGroup markerCube;
    public MeshGroup markerCubeDir;

    public WBenchGameEditorModelAssetsInitializer() {
    }

    @Override
    public void load(SystemResources systemResources) {
        this.createDefaults(systemResources);
    }

    private void createDefaults(SystemResources systemResources) {
        this.markerDefault = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerCursor = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_cursor/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerAabb = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_aabb/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerCube = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_cube/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerCubeDir = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_cube_dir/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.NORMAL;
    }
}
