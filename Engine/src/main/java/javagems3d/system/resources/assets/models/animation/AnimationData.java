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

package javagems3d.system.resources.assets.models.animation;

public class AnimationData {
    private Animation currentAnimation;
    private int currentFrameId;
    private int previousFrameId;
    private float animationFrameDelta;

    public AnimationData() {
        this(null);
    }

    public AnimationData(Animation animation) {
        this.currentAnimation = animation;
        this.previousFrameId = 0;
        this.animationFrameDelta = 0.0f;
    }

    public void nextFrame() {
        int nextFrame = this.getCurrentFrameId() + 1;
        if (nextFrame > this.getCurrentAnimation().frameList().size() - 1) {
            this.setFrame(0);
        } else {
            this.setFrame(nextFrame);
        }
    }

    public void setFrame(int frame) {
        this.previousFrameId = this.currentFrameId;
        this.currentFrameId = frame;
    }

    public double getFps() {
        return this.getCurrentAnimation().fps();
    }

    public float getAnimationFrameDelta() {
        return this.animationFrameDelta;
    }

    public void setAnimationFrameDelta(float animationFrameDelta) {
        this.animationFrameDelta = animationFrameDelta;
    }

    public AnimationFrame getCurrentAnimationFrame() {
        return this.getCurrentAnimation().frameList().get(this.getCurrentFrameId());
    }

    public AnimationFrame getPreviousAnimationFrame() {
        return this.getCurrentAnimation().frameList().get(this.getPreviousFrameId());
    }

    public int getPreviousFrameId() {
        return this.previousFrameId;
    }

    public int getCurrentFrameId() {
        return this.currentFrameId;
    }

    public void setAnimation(Animation animation) {
        this.currentFrameId = 0;
        this.previousFrameId = 0;
        this.currentAnimation = animation;
    }

    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }

    public boolean isValid() {
        return this.getCurrentAnimation() != null;
    }
}
