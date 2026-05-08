package javagems3d.graphics.environment.particles.scene;

import javagems3d.graphics.environment.particles.IParticlesManager;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.particles.GroupedParticlesIndirectRenderer;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.Collection;
import java.util.function.Consumer;

public interface IParticlesScene {
    void createResources(OpenGLRenderer openGLRenderer);
    void destroyResources();

    void update(IRenderWorld renderWorld);

    GroupedParticlesIndirectRenderer getParticlesIndirectRendererScene();
    GroupedParticlesIndirectRenderer getParticlesIndirectRendererTransparency();
    Consumer<JGemsShaderManager> getDefaultConsumerForParticlesScene();

    default void passObjectInMainSceneSSBO(Collection<ParticleFX> filteredParticlesToRender) {
        this.getParticlesIndirectRendererScene().setIndirectMeshObjects(filteredParticlesToRender);
    }

    default void passObjectInTransparencySSBO() {
        this.getParticlesIndirectRendererTransparency().setIndirectMeshObjects(this.getParticlesIndirectRendererScene().getRejected());
    }

    IParticlesManager getParticlesManager();

    default void recreateResources(OpenGLRenderer openGLRenderer) {
        this.destroyResources();
        this.createResources(openGLRenderer);
    }
}
