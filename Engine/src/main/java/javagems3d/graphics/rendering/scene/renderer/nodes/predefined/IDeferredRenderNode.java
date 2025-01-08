package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.ShadingTable;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.IndirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.RawColorSceneRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.SSAORenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public interface IDeferredRenderNode extends IRenderNode {
    @NotNull FBOTexture2DProgram getOutFboGBuffer();
    @NotNull FBOTexture2DProgram getOutFboColorBuffer();

    final class Default extends IRenderNode.Template implements IDeferredRenderNode {
        private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
        private SSAORenderProcessor ssaoRenderProcessor;
        private RawColorSceneRenderProcessor rawColorSceneRenderProcessor;

        public Default(OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
        }

        public @NotNull FBOTexture2DProgram getOutFboGBuffer() {
            return this.getIndirectGeometryRenderProcessor().getGBuffer();
        }

        @Override
        public @NotNull FBOTexture2DProgram getOutFboColorBuffer() {
            return this.getRawColorSceneRenderProcessor().getColorBuffer();
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            Set<SceneObject> sceneObjects = JGemsOpenGLRenderer.getFilteredSetToRender(this.getOpenGLRenderer().getSceneWorld().getSceneObjects());
            this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.canBeRenderedInIndirectStage(sceneObjects));
            this.getIndirectGeometryRenderProcessor().onRender(frameTicking);
            this.getSSAORenderProcessor().onRender(frameTicking);
            this.getRawColorSceneRenderProcessor().onRender(frameTicking);
        }

        private Set<SceneObject> canBeRenderedInIndirectStage(Set<SceneObject> set) {
            return set.stream().filter(e -> e.getObjectRenderConfiguration().getShadingTable().getRenderingSceneShaderTarget() == ShadingTable.Stage.DEFERRED_INDIRECT).collect(Collectors.toSet());
        }

        public void initProcessors() {
            this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(this.getOpenGLRenderer());
            this.ssaoRenderProcessor = new SSAORenderProcessor(this.getOpenGLRenderer(), this.getIndirectGeometryRenderProcessor(), JGemsResourceManager.globalShaderAssets.world_ssao);
            this.rawColorSceneRenderProcessor = new RawColorSceneRenderProcessor(this.getOpenGLRenderer(), this.getIndirectGeometryRenderProcessor(), this.getSSAORenderProcessor(), JGemsResourceManager.globalShaderAssets.world_deferred);
        }

        @Override
        public void createResources() {
            this.initProcessors();
            this.getSSAORenderProcessor().createResources();
            this.getIndirectGeometryRenderProcessor().createResources();
            this.getRawColorSceneRenderProcessor().createResources();
        }

        @Override
        public void destroyResources() {
            this.getSSAORenderProcessor().destroyResources();
            this.getIndirectGeometryRenderProcessor().destroyResources();
            this.getRawColorSceneRenderProcessor().destroyResources();
        }

        public RawColorSceneRenderProcessor getRawColorSceneRenderProcessor() {
            return this.rawColorSceneRenderProcessor;
        }

        public SSAORenderProcessor getSSAORenderProcessor() {
            return this.ssaoRenderProcessor;
        }

        public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
            return this.indirectGeometryRenderProcessor;
        }
    }
}
