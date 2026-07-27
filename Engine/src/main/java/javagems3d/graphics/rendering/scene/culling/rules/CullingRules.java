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

package javagems3d.graphics.rendering.scene.culling.rules;

import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;

public class CullingRules implements ICopyable<CullingRules> {
    private boolean ignoreDistanceCulling;
    private boolean ignoreFrustumCulling;

    public CullingRules(boolean ignoreDistanceCulling, boolean ignoreFrustumCulling) {
        this.ignoreDistanceCulling = ignoreDistanceCulling;
        this.ignoreFrustumCulling = ignoreFrustumCulling;
    }

    public CullingRules() {
        this(false, false);
    }

    public @NotNull static CullingRules get() {
        return new CullingRules();
    }

    public boolean isIgnoreDistanceCulling() {
        return ignoreDistanceCulling;
    }

    public void setIgnoreDistanceCulling(boolean ignoreDistanceCulling) {
        this.ignoreDistanceCulling = ignoreDistanceCulling;
    }

    public boolean isIgnoreFrustumCulling() {
        return ignoreFrustumCulling;
    }

    public void setIgnoreFrustumCulling(boolean ignoreFrustumCulling) {
        this.ignoreFrustumCulling = ignoreFrustumCulling;
    }

    public CullingRules copy() {
        return new CullingRules(this.isIgnoreDistanceCulling(), this.isIgnoreFrustumCulling());
    }
}