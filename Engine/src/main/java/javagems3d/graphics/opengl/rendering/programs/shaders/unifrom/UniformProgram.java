/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.rendering.programs.shaders.unifrom;

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

    public boolean setUniform(@NotNull UniformString uniformName, @NotNull UniformProgram.UFunction UFUnction) {
        return UFUnction.performUniform(this.getUniforms().get(uniformName));
    }

    public Map<UniformString, Integer> getUniforms() {
        return this.uniforms;
    }

    @FunctionalInterface
    public interface UFunction {
        boolean performUniform(int uniformId);
    }
}
