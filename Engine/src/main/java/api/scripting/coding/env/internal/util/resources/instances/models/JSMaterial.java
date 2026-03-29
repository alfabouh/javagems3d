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

@JSCodingClass(binding = "JSMaterial", description = "Wrapper for Material, exposing material properties, textures, colors, and transparency.")
public class JSMaterial {
    @JSHideFromDoc
    private final Material material;

    @JSHideFromDoc
    public JSMaterial(Material material) {
        this.material = material;
    }

    @JSCodingFunctionOrMethod(description = "Check if the material has transparency.")
    public boolean hasTransparency() {
        return this.material.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "Get the opacity value of the material (0..1).")
    public float getOpacity() {
        return this.material.getOpacity();
    }

    @JSCodingFunctionOrMethod(description = "Get the diffuse texture map of the material.")
    public JSTexture2D getDiffuseMap() {
        return new JSTexture2D(this.material.getDiffuseMap());
    }

    @JSCodingFunctionOrMethod(description = "Get the diffuse color (RGBA) of the material.")
    public JSVector4f getDiffuseColor() {
        return new JSVector4f(this.material.getDiffuseColor().color());
    }

    @JSCodingFunctionOrMethod(description = "Get the emission texture map.")
    public JSTexture2D getEmissionMap() {
        return new JSTexture2D(this.material.getEmissionMap());
    }

    @JSCodingFunctionOrMethod(description = "Get the emission color of the material.")
    public JSVector3f getEmissionColor() {
        return this.material.getEmissionColor() == null ? new JSVector3f() : new JSVector3f(this.material.getEmissionColor().color());
    }

    @JSCodingFunctionOrMethod(description = "Get the metallic-roughness texture map.")
    public JSTexture2D getMetallicRoughnessMap() {
        return new JSTexture2D(this.material.getMetallicRoughnessMap());
    }

    @JSCodingFunctionOrMethod(description = "Get the normals texture map.")
    public JSTexture2D getNormalsMap() {
        return new JSTexture2D(this.material.getNormalsMap());
    }

    @JSCodingFunctionOrMethod(description = "Get the metallic factor (0..1) of the material.")
    public float getMetallicFactor() {
        return this.material.getMetallicFactor();
    }

    @JSCodingFunctionOrMethod(description = "Get the roughness factor (0..1) of the material.")
    public float getRoughnessFactor() {
        return this.material.getRoughnessFactor();
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java Material object (unsafe).")
    public Material getJavaMaterial() {
        return this.material;
    }
}