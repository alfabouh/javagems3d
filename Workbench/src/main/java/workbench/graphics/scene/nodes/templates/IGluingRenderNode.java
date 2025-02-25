package workbench.graphics.scene.nodes.templates;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;

public interface IGluingRenderNode extends IRenderNode {
    FBOTexture2DProgram getInColorTransparencyBuffer();
    FBOTexture2DProgram getInColorSceneBuffer();
    FBOTexture2DProgram getOutColorBuffer();
}
