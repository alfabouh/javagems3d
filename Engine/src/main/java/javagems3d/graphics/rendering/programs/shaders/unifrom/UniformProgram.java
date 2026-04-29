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
