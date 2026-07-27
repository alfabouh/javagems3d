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

package javagems3d.graphics.environment.lights;

import javagems3d.graphics.objects.entities.SceneEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.physics.world.basic.IWorldTicked;

public abstract class Light implements IWorldTicked {
    private final Vector3f offset;
    private final Vector3f lightColor;
    private final Vector3f lightPos;
    private boolean isActive;

    public Light() {
        this(new Vector3f(0.0f), new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public Light(Vector3f lightPos, Vector3f lightColor) {
        this(lightPos, lightColor, new Vector3f(0.0f));
    }

    public Light(Vector3f lightPos) {
        this(lightPos, new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public Light(@NotNull Vector3f lightPos, @NotNull Vector3f lightColor, @NotNull Vector3f offset) {
        this.lightColor = new Vector3f(lightColor);
        this.lightPos = new Vector3f(lightPos);
        this.offset = new Vector3f(offset);
        this.on();
    }

    public Light(SceneEntity abstractSceneEntity) {
        this(abstractSceneEntity.getRenderPosition(), new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public Light(SceneEntity abstractSceneEntity, Vector3f lightColor) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, new Vector3f(0.0f));
    }

    public Light(SceneEntity abstractSceneEntity, Vector3f lightColor, Vector3f offset) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, offset);
    }

    public Light on() {
        this.isActive = true;
        return this;
    }

    public Light off() {
        this.isActive = false;
        return this;
    }

    public abstract LightType getLightType();

    public Light setLightColor(Vector3f lightColor) {
        this.lightColor.set(lightColor);
        return this;
    }

    public Light setLightPosition(Vector3f lightPos) {
        this.lightPos.set(lightPos);
        return this;
    }

    public Light setOffset(Vector3f offset) {
        this.offset.set(offset);
        return this;
    }

    public boolean canBeAttached() {
        return this instanceof ILightAttachable;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public Vector3f getLightColor() {
        return new Vector3f(this.lightColor);
    }

    public Vector3f getLightPosition() {
        return new Vector3f(this.lightPos).add(this.getOffset());
    }

    public Vector3f getOffset() {
        return new Vector3f(this.offset);
    }

    @Override
    public String toString() {
        return this.getLightType().toString();
    }
}
