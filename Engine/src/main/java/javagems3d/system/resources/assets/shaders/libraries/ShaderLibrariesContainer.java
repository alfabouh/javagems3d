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

package javagems3d.system.resources.assets.shaders.libraries;

import javagems3d.system.resources.assets.shaders.base.ShaderType;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public final class ShaderLibrariesContainer {
    private final JGemsStringSource path;
    private final ShaderLibrary vertexShaderLibrary;
    private final ShaderLibrary fragmentShaderLibrary;
    private final ShaderLibrary geometryShaderLibrary;

    public ShaderLibrariesContainer(@NotNull JGemsPathSource shaderPath) {
        this.path = new JGemsStringSource(shaderPath);

        ShaderLibrary geometricShader1 = null;
        ShaderLibrary vertexShader1 = null;
        ShaderLibrary fragmentShader1 = null;

        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.FRAGMENT)) {
            fragmentShader1 = new ShaderLibrary(shaderPath, ShaderType.FRAGMENT);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.VERTEX)) {
            vertexShader1 = new ShaderLibrary(shaderPath, ShaderType.VERTEX);
        }
        if (ShaderLibrary.checkIfShaderExistsInJar(shaderPath, ShaderType.GEOMETRIC)) {
            geometricShader1 = new ShaderLibrary(shaderPath, ShaderType.GEOMETRIC);
        }

        this.vertexShaderLibrary = vertexShader1;
        this.fragmentShaderLibrary = fragmentShader1;
        this.geometryShaderLibrary = geometricShader1;

        Log.get().trace("Initialized shader library: " + this);
    }

    public ShaderLibrary getVertexShaderLibrary() {
        return this.vertexShaderLibrary;
    }

    public ShaderLibrary getFragmentShaderLibrary() {
        return this.fragmentShaderLibrary;
    }

    public ShaderLibrary getGeometryShaderLibrary() {
        return this.geometryShaderLibrary;
    }

    public ShaderLibrary getShaderLibraryByType(ShaderType shaderType) {
        switch (shaderType) {
            case VERTEX: {
                return this.getVertexShaderLibrary();
            }
            case FRAGMENT: {
                return this.getFragmentShaderLibrary();
            }
            case GEOMETRIC: {
                return this.getGeometryShaderLibrary();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getStringSource().toString();
    }

    public JGemsStringSource getStringSource() {
        return this.path;
    }
}
