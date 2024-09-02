/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.assets.models.mesh.data.render;

/**
 * These are the attributes that affect the rendering of the mesh in the JGems pipeline
 */
@SuppressWarnings("all")
public class MeshRenderAttributes {
    private boolean lightOpaque;
    private boolean shadowCaster;

    private float alphaDiscardValue;
    private float objectOpacity;

    private boolean isBright;
    private float renderDistance;
    private boolean shouldInterpolateMovement;
    private boolean disableFaceCulling;

    public MeshRenderAttributes() {
        this(true);
    }

    public MeshRenderAttributes(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        this.lightOpaque = true;
        this.isBright = false;
        this.renderDistance = -1.0f;
        this.alphaDiscardValue = 0.0f;
        this.objectOpacity = 1.0f;
        this.shouldInterpolateMovement = true;
        this.disableFaceCulling = false;
    }

    public MeshRenderAttributes faceCullingDisabled(boolean disableFaceCulling) {
        this.disableFaceCulling = disableFaceCulling;
        return this;
    }

    public MeshRenderAttributes setAlphaDiscard(float f) {
        this.alphaDiscardValue = f;
        return this;
    }

    public float getObjectOpacity() {
        return this.objectOpacity;
    }

    public MeshRenderAttributes setObjectOpacity(float objectOpacity) {
        this.objectOpacity = objectOpacity;
        return this;
    }

    public boolean isBright() {
        return this.isBright;
    }

    public MeshRenderAttributes setBright(boolean bright) {
        isBright = bright;
        return this;
    }

    public boolean isShadowCaster() {
        return this.shadowCaster;
    }

    public MeshRenderAttributes setShadowCaster(boolean shadowCaster) {
        this.shadowCaster = shadowCaster;
        return this;
    }

    public boolean isLightOpaque() {
        return this.lightOpaque;
    }

    public MeshRenderAttributes setLightOpaque(boolean lightOpaque) {
        this.lightOpaque = lightOpaque;
        return this;
    }

    public boolean isShouldInterpolateMovement() {
        return this.shouldInterpolateMovement;
    }

    public MeshRenderAttributes setShouldInterpolateMovement(boolean shouldInterpolateMovement) {
        this.shouldInterpolateMovement = shouldInterpolateMovement;
        return this;
    }

    public float getAlphaDiscardValue() {
        return this.alphaDiscardValue;
    }

    public boolean isDisabledFaceCulling() {
        return this.disableFaceCulling;
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public MeshRenderAttributes setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
        return this;
    }

    public MeshRenderAttributes copy() {
        MeshRenderAttributes meshRenderData = new MeshRenderAttributes(this.isShadowCaster());
        meshRenderData.setBright(this.isBright());
        meshRenderData.setRenderDistance(this.getRenderDistance());
        meshRenderData.setAlphaDiscard(this.getAlphaDiscardValue());
        meshRenderData.setShouldInterpolateMovement(this.isShouldInterpolateMovement());
        return meshRenderData;
    }
}
