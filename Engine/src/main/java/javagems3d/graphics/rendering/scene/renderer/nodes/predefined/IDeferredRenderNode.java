package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.IndirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.RawColorSceneRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.SSAORenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.ShaderRenderingTarget;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

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
            this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.localFilter(sceneObjects));
            this.getIndirectGeometryRenderProcessor().onRender(frameTicking);
            this.getSSAORenderProcessor().onRender(frameTicking);
            this.getRawColorSceneRenderProcessor().onRender(frameTicking);
        }

        private Set<SceneObject> localFilter(Set<SceneObject> set) {
            return set.stream().filter(e -> e.getShaderManager().getShaderTarget().equals(ShaderRenderingTarget.INDIRECT_DEFERRED_RENDERING)).collect(Collectors.toSet());
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
