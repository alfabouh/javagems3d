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

package javagems3d.physics.world.triggers;

import org.joml.Vector3f;

public final class Zone {
    private final Vector3f location;
    private final Vector3f size;

    public Zone(Vector3f location, Vector3f size) {
        this.location = location;
        this.size = size;
    }

    public Vector3f getLocation() {
        return new Vector3f(this.location);
    }

    public Vector3f getSize() {
        return new Vector3f(this.size);
    }

    public String toString() {
        return this.getLocation() + " - " + this.getSize();
    }
}
