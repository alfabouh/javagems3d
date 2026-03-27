package api.scripting.coding.env.internal.util.resources.instances.models.poly.bound;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSBoxAABB", description = "...")
public class JSBoxAABB {
    @JSHideFromDoc private final CullingAABB cullingAABB;

    @JSHideFromDoc
    public JSBoxAABB(CullingAABB cullingAABB) {
        this.cullingAABB = cullingAABB;
    }

    public JSBoxAABB(JSVector3f aabbMin, JSVector3f aabbMax) {
        this.cullingAABB = new CullingAABB(aabbMin.getJavaVector3f(), aabbMax.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSMatrix4f createAABBTransformMatrix() {
        return new JSMatrix4f(this.cullingAABB.createAABBTransformMatrix());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getAabbMin() {
        return new JSVector3f(this.cullingAABB.getAabbMin());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setAabbMin(JSVector3f aabbMin) {
        this.cullingAABB.setAabbMin(aabbMin.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getAabbMax() {
        return new JSVector3f(this.cullingAABB.getAabbMax());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setAabbMax(JSVector3f aabbMax) {
        this.cullingAABB.setAabbMax(aabbMax.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public CullingAABB getJavaCullingAABB() {
        return this.cullingAABB;
    }
}
