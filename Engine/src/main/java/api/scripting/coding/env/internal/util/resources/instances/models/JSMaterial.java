package api.scripting.coding.env.internal.util.resources.instances.models;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.math.JSVector4f;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor3;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;

@JSCodingClass(binding = "JSMaterial", description = "...")
public class JSMaterial {
    @JSHideFromDoc private final Material material;

    @JSHideFromDoc
    public JSMaterial(Material material) {
        this.material = material;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean hasTransparency() {
        return this.material.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public float getOpacity() {
        return this.material.getOpacity();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSTexture2D getDiffuseMap() {
        return new JSTexture2D(this.material.getDiffuseMap());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector4f getDiffuseColor() {
        return new JSVector4f(this.material.getDiffuseColor().color());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSTexture2D getEmissionMap() {
        return new JSTexture2D(this.material.getEmissionMap());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector3f getEmissionColor() {
        return this.material.getEmissionColor() == null ? new JSVector3f() : new JSVector3f(this.material.getEmissionColor().color());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSTexture2D getMetallicRoughnessMap() {
        return new JSTexture2D(this.material.getMetallicRoughnessMap());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSTexture2D getNormalsMap() {
        return new JSTexture2D(this.material.getNormalsMap());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public float getMetallicFactor() {
        return this.material.getMetallicFactor();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public float getRoughnessFactor() {
        return this.material.getRoughnessFactor();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public Material getJavaMaterial() {
        return this.material;
    }
}
