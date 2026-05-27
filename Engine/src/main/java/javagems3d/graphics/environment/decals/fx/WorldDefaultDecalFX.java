package javagems3d.graphics.environment.decals.fx;

import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WorldDefaultDecalFX extends DecalFX {
    private boolean dead;
    private final float lifeTime;
    private @Nullable JGemsTimedAction lifeTimer;
    private boolean unDestructible;

    public WorldDefaultDecalFX(@NotNull DecalMaterial material, @NotNull DecalTextureProperties decalTextureProperties, float lifeTime, int terrainLayer) {
        super(material, decalTextureProperties, terrainLayer);
        this.dead = false;
        this.lifeTime = lifeTime;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        if (this.lifeTime > 0.0f) {
            IRenderWorld renderWorld = (IRenderWorld) iWorld;
            this.lifeTimer = renderWorld.createTimer();
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.lifeTimer != null) {
            if (this.lifeTimer.resetTimerAfterReachedSeconds(this.lifeTime)) {
                this.setDead();
            }
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        if (this.lifeTimer != null) {
            this.lifeTimer.dispose();
        }
    }

    public WorldDefaultDecalFX setPosition(Vector3f position) {
        return (WorldDefaultDecalFX) super.setPosition(position);
    }

    public WorldDefaultDecalFX setRotation(Vector3f rotation) {
        return (WorldDefaultDecalFX) super.setRotation(rotation);
    }

    public WorldDefaultDecalFX setScale(Vector3f scale) {
        return (WorldDefaultDecalFX) super.setScale(scale);
    }

    public boolean isUnDestructible() {
        return this.unDestructible;
    }

    public WorldDefaultDecalFX setUnDestructible(boolean unDestructible) {
        this.unDestructible = unDestructible;
        return this;
    }

    @Override
    public boolean unDestructible() {
        return this.unDestructible;
    }

    @Override
    public void setDead() {
        if (!this.unDestructible()) {
            this.dead = true;
        }
    }

    @Override
    public boolean isDead() {
        return this.dead || (this.getAttachedTo() != null && this.getAttachedTo().isDead());
    }
}
