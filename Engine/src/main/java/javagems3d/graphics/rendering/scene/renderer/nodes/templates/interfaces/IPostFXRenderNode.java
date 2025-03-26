package javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;

public interface IPostFXRenderNode extends IRenderNode {
    FBOTexture2DProgram getInColorBuffer();
    FBOTexture2DProgram getOutColorBuffer();
}
