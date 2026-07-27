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

package javagems3d.graphics.objects;

import javagems3d.system.resources.assets.models.animation.AnimationData;

@SuppressWarnings("all")
public interface IAnimated {
    AnimationData getAnimationData();
    AnimationData setAnimationByID(int id);
    void setAnimationData(AnimationData animationData);

    default float animationSpeedMultiplier() {
        return 1.0f;
    }

    default void setAnimationDataFrame(int i) {
        this.getAnimationData().setFrame(i);
    }

    default AnimationData initAnimation() {
        return this.setAnimationByID(0);
    }

    default boolean isAnimated() {
        return this.getAnimationData() != null && this.getAnimationData().isValid();
    }
}
