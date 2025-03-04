package javagems3d.graphics.environment.fog;

import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class JGemsFogScene extends FogScene {
    @Override
    public void updateFogBuffer(ShaderStorageBufferObject shaderStorageBufferObject, ISkyBox skyBox, MemoryStack stack) {
        if (this.update) {
            FloatBuffer buffer = stack.mallocFloat(JGemsConfig.SYSTEM.FOG_BUFFER_PACK_SIZE);
            buffer.put(this.getColor().x * skyBox.getSun().getSunBrightness());
            buffer.put(this.getColor().y * skyBox.getSun().getSunBrightness());
            buffer.put(this.getColor().z * skyBox.getSun().getSunBrightness());
            buffer.put(!JGemsConfig.DEBUG.FULL_BRIGHT ? this.getDensity() : 0.0f);
            buffer.flip();
            ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, buffer);
            this.update = false;
        }
    }
}
