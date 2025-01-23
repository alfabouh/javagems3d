package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;

public interface ITransparencyRenderNode extends IRenderNode {
    final class Default extends IRenderNode.Template implements ITransparencyRenderNode {

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
