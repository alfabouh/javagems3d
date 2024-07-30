package ru.jgems3d.engine.system.resources.assets.models.mesh.data.render;

@SuppressWarnings("all")
public class MeshRenderAttributes {
    private boolean lightOpaque;
    private boolean shadowCaster;
    private boolean hasTransparency;
    private float alphaDiscardValue;
    private boolean isBright;
    private float renderDistance;
    private boolean shouldInterpolateMovement;

    public MeshRenderAttributes() {
        this(true, false);
    }

    public MeshRenderAttributes(boolean shadowCaster, boolean hasTransparency) {
        this.shadowCaster = shadowCaster;
        this.hasTransparency = hasTransparency;
        this.lightOpaque = true;
        this.isBright = false;
        this.renderDistance = -1.0f;
        this.alphaDiscardValue = 0.0f;
        this.shouldInterpolateMovement = true;
    }

    public void setLightOpaque(boolean lightOpaque) {
        this.lightOpaque = lightOpaque;
    }

    public boolean isLightOpaque() {
        return this.lightOpaque;
    }

    public boolean isShouldInterpolateMovement() {
        return this.shouldInterpolateMovement;
    }

    public MeshRenderAttributes setShouldInterpolateMovement(boolean shouldInterpolateMovement) {
        this.shouldInterpolateMovement = shouldInterpolateMovement;
        return this;
    }

    public boolean isBright() {
        return this.isBright;
    }

    public MeshRenderAttributes setBright(boolean bright) {
        isBright = bright;
        return this;
    }

    public boolean isHasTransparency() {
        return this.hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public MeshRenderAttributes setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public float getAlphaDiscardValue() {
        return this.alphaDiscardValue;
    }

    public MeshRenderAttributes setAlphaDiscard(float f) {
        this.alphaDiscardValue = f;
        return this;
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public MeshRenderAttributes setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    public MeshRenderAttributes copy() {
        MeshRenderAttributes meshRenderData = new MeshRenderAttributes(this.isShadowCaster(), this.isHasTransparency());
        meshRenderData.setBright(this.isBright());
        meshRenderData.setRenderDistance(this.getRenderDistance());
        meshRenderData.setAlphaDiscard(this.getAlphaDiscardValue());
        meshRenderData.setShouldInterpolateMovement(this.isShouldInterpolateMovement());
        return meshRenderData;
    }
}
