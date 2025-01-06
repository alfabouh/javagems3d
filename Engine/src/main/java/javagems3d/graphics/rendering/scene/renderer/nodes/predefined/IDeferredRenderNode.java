package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.predefined.IndirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.ShaderRenderingTarget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.Set;
import java.util.stream.Collectors;

public interface IDeferredRenderNode extends IRenderNode {
    @NotNull FBOTexture2DProgram getOutFboGBuffer();
    @NotNull FBOTexture2DProgram getOutFboSSAOBuffer();
    @NotNull FBOTexture2DProgram getOutFboColorBuffer();

    final class Default extends IRenderNode.Template implements IDeferredRenderNode {
        private final IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
        private FBOTexture2DProgram gBuffer;
        private FBOTexture2DProgram ssao;
        private FBOTexture2DProgram color;

        public Default(OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
            this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(openGLRenderer);
        }

        public @NotNull FBOTexture2DProgram getOutFboGBuffer() {
            return this.gBuffer;
        }

        @Override
        public @NotNull FBOTexture2DProgram getOutFboSSAOBuffer() {
            return this.ssao;
        }

        @Override
        public @NotNull FBOTexture2DProgram getOutFboColorBuffer() {
            return this.color;
        }

        @Override
        public void onRender(FrameTicking frameTicking) {
            Set<SceneObject> sceneObjects = JGemsOpenGLRenderer.getFilteredSetToRender(this.getOpenGLRenderer().getSceneWorld().getSceneObjects());
            this.getOutFboGBuffer().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
            this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(this.localFilter(sceneObjects));
            this.getIndirectGeometryRenderProcessor().onRender(frameTicking);
            this.getOutFboGBuffer().unBindFBO();
        }

        private Set<SceneObject> localFilter(Set<SceneObject> set) {
            return set.stream().filter(e -> e.getShaderManager().getShaderTarget().equals(ShaderRenderingTarget.INDIRECT_DEFERRED_RENDERING)).collect(Collectors.toSet());
        }

        @Override
        public void createResources() {
            this.getIndirectGeometryRenderProcessor().createResources();
            this.gBuffer = new FBOTexture2DProgram(true);
            T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
                add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB, GL46.GL_RGB);
                add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RGB, GL46.GL_RGB);
            }};
            this.gBuffer.createFrameBuffer2DTexture(this.getOpenGLRenderer().getRenderingResolution(), gBuffer, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        }

        @Override
        public void destroyResources() {
            this.getIndirectGeometryRenderProcessor().destroyResources();
            if (this.gBuffer != null) {
                this.gBuffer.clearFBO();
            }
        }

        public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
            return this.indirectGeometryRenderProcessor;
        }
    }
}
