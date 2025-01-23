package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;

public interface IPostFXRenderNode extends IRenderNode {
    final class Default extends IRenderNode.Template implements IPostFXRenderNode {

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
