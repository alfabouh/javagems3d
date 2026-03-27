package api.scripting.coding.env.internal.util.resources.instances.models.animation;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import org.joml.Matrix4f;

import java.util.Arrays;

@JSCodingClass(binding = "JSAnimationFrame", description = "...")
public class JSAnimationFrame {
    @JSHideFromDoc private AnimationFrame animationFrame;

    @JSHideFromDoc
    public JSAnimationFrame(AnimationFrame animationFrame) {
        this.animationFrame = animationFrame;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSMatrix4f[] getBoneMatrices() {
        return Arrays.stream(this.animationFrame.getBoneMatrices()).map(JSMatrix4f::new).toArray(JSMatrix4f[]::new);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setBoneMatrices(JSMatrix4f[] boneMatrices) {
        this.animationFrame.setBoneMatrices(Arrays.stream(boneMatrices).map(JSMatrix4f::getJavaMatrix4f).toArray(Matrix4f[]::new));
    }

    @JSCodingFunctionOrMethod(description = "...")
    public int getOffset() {
        return this.animationFrame.getOffset();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setOffset(int offset) {
        this.animationFrame.setOffset(offset);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public AnimationFrame getJavaAnimationFrame() {
        return this.animationFrame;
    }
}
