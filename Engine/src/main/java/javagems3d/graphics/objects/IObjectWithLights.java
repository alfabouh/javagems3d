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

import javagems3d.graphics.environment.lights.ILightAttachable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

public interface IObjectWithLights {
    default void addLightAttachment(ILightAttachable light) {
        if (light == null) {
            return;
        }
        light.attachTo(this);
        this.getAttachedLights().add(light);
    }

    default void removeLightAttachment(ILightAttachable light) {
        if (light == null) {
            return;
        }
        light.detach();
        this.getAttachedLights().remove(light);
    }

    @NotNull Set<ILightAttachable> getAttachedLights();

    Vector3f getPositionToAttachLights();
    Vector3f getRotationAngle();

    default boolean isLightAttached(ILightAttachable light) {
        return this.getAttachedLights().contains(light);
    }

    default boolean hasLights() {
        return !this.getAttachedLights().isEmpty();
    }
}
