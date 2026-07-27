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

package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public abstract class Shadow implements IResourceInit {
    private final IEnvironment environment;
    private Vector2i shadowMapResolution;

    public Shadow(@NotNull IEnvironment environment, @NotNull Vector2i shadowMapResolution) {
        this.environment = environment;
        this.shadowMapResolution = shadowMapResolution;
    }

    public void setShadowMapResolution(@NotNull Vector2i shadowMapResolution) {
        this.shadowMapResolution = shadowMapResolution;
    }

    public Vector2i getShadowMapResolution() {
        return this.shadowMapResolution;
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }
}
