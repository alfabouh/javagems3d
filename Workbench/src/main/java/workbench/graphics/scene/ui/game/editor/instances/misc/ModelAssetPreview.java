/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package workbench.graphics.scene.ui.game.editor.instances.misc;

import javagems3d.JGems3D;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;

public final class ModelAssetPreview implements IAnimated, IPreviewWrapperObject<GameResourceModelAsset> {
    private final GameResourceModelAsset modelAsset;
    private AnimationData animationData;
    private float animationSpeed;
    private double lastTick;
    private float animationProgress;

    public ModelAssetPreview(@NotNull GameResourceModelAsset modelAsset) {
        this.modelAsset = modelAsset;
        this.animationData = null;
        this.animationSpeed = 1.0f;
        this.lastTick = JGems3D.glfwTime();
    }

    public GameResourceModelAsset getAsset() {
        return this.modelAsset;
    }

    @Override
    public AnimationData getAnimationData() {
        return this.animationData;
    }

    @Override
    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    @Override
    public float animationSpeedMultiplier() {
        return this.animationSpeed;
    }

    public void updateAnimation() {
        if (!this.isAnimated()) {
            return;
        }
        double fps = this.getAnimationData().getCurrentAnimation().fps();
        if (fps <= 0.0d) {
            fps = JGemsConfig.SYSTEM.DEFAULT_ANIM_FPS;
        }
        fps *= this.animationSpeedMultiplier();
        double deltaTime = JGems3D.glfwTime() - this.lastTick;
        this.animationProgress += (float) (deltaTime * fps);
        if (this.animationProgress >= 1.0f) {
            this.nextAnimationFrame();
            this.animationProgress %= 1.0f;
        }
        this.getAnimationData().setAnimationFrameDelta(1.0f - this.animationProgress);
        this.lastTick = JGems3D.glfwTime();
    }

    @Override
    public AnimationData setAnimationByID(int id) {
        if (!this.getAsset().meshGroup().isAnimatedStructure()) {
            return null;
        }
        if (id < 0) {
            this.setAnimationData(null);
            return null;
        }
        if (id >= this.getAsset().meshGroup().getAnimationsList().size()) {
            Log.get().error("Couldn't set animation for: " + this);
            return null;
        }

        AnimationData animationData = new AnimationData(this.getAsset().meshGroup().getAnimationsList().get(id));
        this.setAnimationData(animationData);
        this.nextAnimationFrame();
        return animationData;
    }

    public void nextAnimationFrame() {
        if (this.isAnimated()) {
            this.getAnimationData().nextFrame();
        }
    }
}
