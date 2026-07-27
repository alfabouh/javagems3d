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

package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import org.jetbrains.annotations.NotNull;
import workbench.resources.shaders.WBenchShaderManager;

public final class WBenchGlobalShadersInitializer extends ShadersInitializer<WBenchShaderManager> {
    public WBenchShaderManager imgui;
    public WBenchShaderManager debug;

    public WBenchGlobalShadersInitializer() {
    }

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
    }

    protected void initObjects(ResourceCache resourceCache) {
        this.imgui = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/imgui"), ISource.Source.INSIDE_JAR));
        this.debug = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "debug"), ISource.Source.INSIDE_JAR));
    }

    @Override
    protected WBenchShaderManager createShaderObject(@NotNull JGemsPathSource shaderPath, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary) {
        return new WBenchShaderManager(new ShadersContainer(shaderPath, shaderStaticConstants, shaderLibrary));
    }
}