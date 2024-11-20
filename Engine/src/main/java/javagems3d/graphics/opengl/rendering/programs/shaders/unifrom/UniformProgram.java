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
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;

import java.nio.FloatBuffer;
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

    public boolean setUniform(@NotNull UniformString uniformName, @NotNull UniformProgram.UniformAction uniformAction) {
        return uniformAction.performUniform(this.getUniforms().get(uniformName));
    }

    public Map<UniformString, Integer> getUniforms() {
        return this.uniforms;
    }

    @FunctionalInterface
    public interface UniformAction {
        boolean performUniform(int uniformId);
    }
}
