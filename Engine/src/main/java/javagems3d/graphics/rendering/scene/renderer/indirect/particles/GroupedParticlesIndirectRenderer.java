package javagems3d.graphics.rendering.scene.renderer.indirect.particles;

import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.ParticleIndirectCommandsProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.IndirectSceneObjectsRenderer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GroupedParticlesIndirectRenderer extends IndirectParticlesRenderer {
    public GroupedParticlesIndirectRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull IndirectSceneObjectsRenderer.IRenderingFunction renderingFunction, @NotNull ShaderStorageBufferObject indirectSSBO, @NotNull ShaderStorageBufferObject propertiesSSBO, @NotNull Pipeline pipeline) {
        super(openGLRenderer, renderingFunction, indirectSSBO, propertiesSSBO, pipeline);
    }

    public void processAndRender(@Nullable ArbitraryArguments metaData) {
        if (!this.getRejected().isEmpty()) {
            this.getRejected().clear();
        }
        if (this.getIndirectMeshObjects() == null) {
            return;
        }
        IndirectBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        Map<ShaderManager, Set<ParticleFX>> map = this.groupObjects(this.getIndirectMeshObjects());
        for (Map.Entry<ShaderManager, Set<ParticleFX>> particles : map.entrySet()) {
            this.fillSSBOWithParticleInformation(particles.getValue());
            if (!particles.getValue().isEmpty()) {
                IndirectCommandsProgram indirectCommandsProgram = this.createCommands(renderBuffer, particles.getValue());
                this.render(particles.getKey(), indirectCommandsProgram, renderBuffer, metaData);
                indirectCommandsProgram.destroyBuffer();
            }
        }
    }

    protected IndirectCommandsProgram createCommands(IndirectBufferProgram renderBuffer, Collection<ParticleFX> sceneObjects) {
        ParticleIndirectCommandsProgram particleIndirectCommandsProgram = new ParticleIndirectCommandsProgram(renderBuffer);
        particleIndirectCommandsProgram.createBuffer();
        particleIndirectCommandsProgram.buildCommands(ParticleFX.getParticlesMeshBuffer(), sceneObjects.size());
        return particleIndirectCommandsProgram;
    }

    protected Map<ShaderManager, Set<ParticleFX>> groupObjects(@NotNull Collection<ParticleFX> sceneObjects) {
        return sceneObjects.stream().collect(Collectors.groupingBy(e -> this.getPipeline() == Pipeline.TRANSPARENCY ? e.getParticleFXRenderConfig().getTransparencyShader() : e.getParticleFXRenderConfig().getMainSceneShader(), HashMap::new, Collectors.toSet()));
    }
}
