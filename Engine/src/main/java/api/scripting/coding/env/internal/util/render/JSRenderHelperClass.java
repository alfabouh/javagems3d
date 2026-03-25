package api.scripting.coding.env.internal.util.render;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel2D;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model2D;
import org.lwjgl.opengl.GL46;

@JSCodingClass(binding = "JSSample", description = "...")
public class JSRenderHelperClass implements JSGlobalVarFactory<JSRenderHelperClass> {
    @JSCodingField(description = "...") public static final int DIFFUSE_CODE = 1 << 2;
    @JSCodingField(description = "...") public static final int NORMALS_CODE = 1 << 3;
    @JSCodingField(description = "...") public static final int EMISSION_CODE = 1 << 4;
    @JSCodingField(description = "...") public static final int METALLIC_ROUGHNESS_CODE = 1 << 5;

    @JSCodingFunctionOrMethod(description = "...")
    public int getMaxTextureUnits() {
        return GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel2D(JSModel2D model2D, int renderMode) {
        JGemsHelper.render().renderModel2D(model2D.getJavaModel2D(), renderMode);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel2D(JSModel2D model2D) {
        JGemsHelper.render().renderModel2D(model2D.getJavaModel2D(), GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel3D(JSModel3D model2D) {
        //JGemsHelper.render().renderModel3D(model2D.getJavaModel3D(), GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public void fun() {
    }

    @JSHideFromDoc
    @Override
    public JSRenderHelperClass newGlobalVar() {
        return new JSRenderHelperClass();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "JSRenderHelper";
    }
}
