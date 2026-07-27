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

package javagems3d.graphics.rendering.programs.shaders.unifrom;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;

import java.util.HashMap;
import java.util.Map;

public class UniformProgram {
    private final int programId;
    private final Map<UniformString, Integer> uniforms;

    public UniformProgram(int programId) {
        this.programId = programId;
        this.uniforms = new HashMap<>();
    }

    public boolean createUniform(UniformString uniformName) {
        int uniformLocation = GL46.glGetUniformLocation(this.programId, uniformName.toString());
        this.getUniforms().put(uniformName, uniformLocation);
        return uniformLocation >= 0;
    }

    public boolean setUniform(@NotNull UniformString uniformName, @NotNull UniformProgram.UFunction function) {
        return function.performUniform(this.getUniforms().get(uniformName));
    }

    public Map<UniformString, Integer> getUniforms() {
        return this.uniforms;
    }

    @FunctionalInterface
    public interface UFunction {
        boolean performUniform(int uniformId);
    }
}
