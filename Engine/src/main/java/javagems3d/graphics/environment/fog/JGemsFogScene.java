package javagems3d.graphics.environment.fog;

import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class JGemsFogScene extends FogScene {
    @Override
    public void updateFogBuffer(ShaderStorageBufferObject shaderStorageBufferObject, ISkyBox skyBox, ILightScene lightScene, MemoryStack stack) {
        FloatBuffer buffer = stack.mallocFloat(JGemsConfig.SYSTEM.FOG_BUFFER_PACK_SIZE);
        buffer.put(this.getFogColor().x * lightScene.getSunLight().getSunBrightness());
        buffer.put(this.getFogColor().y * lightScene.getSunLight().getSunBrightness());
        buffer.put(this.getFogColor().z * lightScene.getSunLight().getSunBrightness());
        buffer.put(!JGemsConfig.DEBUG.FULL_BRIGHT && JGemsConfig.DEBUG.FOG ? this.getFogDensity() : 0.0f);
        buffer.flip();
        ShaderStorageBufferProgram.updateSubDataSSBO(shaderStorageBufferObject, 0L, buffer);
    }
}
