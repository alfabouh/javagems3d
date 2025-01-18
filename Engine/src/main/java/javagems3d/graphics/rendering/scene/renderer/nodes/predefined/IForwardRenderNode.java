package javagems3d.graphics.rendering.scene.renderer.nodes.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public interface IForwardRenderNode extends IRenderNode {
    void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects);
    Collection<SceneObject> getForwardRenderingObjects();

    final class Default extends IRenderNode.Template implements IForwardRenderNode {
        private Collection<SceneObject> forwardRenderingObjects;

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

        @Override
        public void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects) {
            this.forwardRenderingObjects = forwardRenderingObjects;
        }

        @Override
        public Collection<SceneObject> getForwardRenderingObjects() {
            return this.forwardRenderingObjects;
        }
    }
}
