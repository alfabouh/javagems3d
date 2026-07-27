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

package api.scripting.coding.env.internal.util.world.physical.entity.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.physics.entities.properties.collision.CollisionType;

@JSCodingClass(binding = "JSCollisionType", description = "Collision groups and filters")
public enum JSCollisionType {
    PLAYER(CollisionType.PLAYER),
    STATIC_BODY(CollisionType.ST_BODY),
    DYNAMIC_BODY(CollisionType.DN_BODY),
    GHOST(CollisionType.GHOST),
    LIQUID(CollisionType.LIQUID),
    NOTHING(CollisionType.NOTHING),
    UNIVERSAL(CollisionType.UNIVERSAL);

    private final CollisionType type;

    JSCollisionType(CollisionType type) {
        this.type = type;
    }

    @JSCodingFunctionOrMethod(description = "Get mask")
    public int getMask() {
        return this.type.mask();
    }

    @JSCodingFunctionOrMethod(description = "Check mask", paramNames = {"mask"})
    public boolean match(int mask) {
        return this.type.matchMask(mask);
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public CollisionType getJavaType() {
        return this.type;
    }
}