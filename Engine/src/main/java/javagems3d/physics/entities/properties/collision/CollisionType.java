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

package javagems3d.physics.entities.properties.collision;

public record CollisionType(int mask) {
    public static final CollisionType PLAYER = new CollisionType(1 << 2);
    public static final CollisionType ST_BODY = new CollisionType(1 << 3);
    public static final CollisionType DN_BODY = new CollisionType(1 << 4);
    public static final CollisionType GHOST = new CollisionType(1 << 5);
    public static final CollisionType LIQUID = new CollisionType(1 << 6);
    public static final CollisionType NOTHING = new CollisionType(0x0);
    public static final CollisionType WORLD = new CollisionType(ST_BODY.mask() | DN_BODY.mask() | LIQUID.mask() | PLAYER.mask());
    public static final CollisionType UNIVERSAL = new CollisionType(0xffff);

    public boolean matchMask(int mask) {
        return (mask & this.mask()) != 0;
    }
}
