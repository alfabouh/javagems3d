package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;

public interface IForwardRenderNode extends IRenderNode {
    final class Default extends IRenderNode.Template implements IForwardRenderNode {

        public Default(OpenGLRenderer openGLRenderer) {
            super(openGLRenderer);
        }

        @Override
        public void onRender(FrameTicking frameTicking) {

        }

        @Override
        public void createResources() {

        }

        @Override
        public void destroyResources() {

        }
    }
}
