/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.physics.entities.properties.collision;

public enum CollisionFilter {
    PLAYER(1 << 2),
    ST_BODY(1 << 3),
    DN_BODY(1 << 4),
    GHOST(1 << 5),
    LIQUID(1 << 6),
    NOTHING(0x0),
    ALL(0xffff);

    private final int mask;

    CollisionFilter(int mask) {
        this.mask = mask;
    }

    public boolean matchMask(int mask) {
        return (mask & this.getMask()) != 0;
    }

    public int getMask() {
        return this.mask;
    }
}
