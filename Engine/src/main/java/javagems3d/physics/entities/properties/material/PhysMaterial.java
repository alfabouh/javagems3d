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

package javagems3d.physics.entities.properties.material;

public record PhysMaterial(float friction, float l_damping, float a_damping, float m_density, float restitution) {

    public static final float NO_MASS = 0.0f;
    public static final float DEFAULT_FRICTION = 1.5f;
    public static final float DEFAULT_L_DAMPING = 0.5f;
    public static final float DEFAULT_A_DAMPING = 0.5f;
    public static final float DEFAULT_MASS_DENSITY = 3.0f;
    public static final float DEFAULT_RESTITUTION = 0.25f;

    public static PhysMaterial createDefaultMaterial() {
        return new PhysMaterial(PhysMaterial.DEFAULT_FRICTION, PhysMaterial.DEFAULT_L_DAMPING, PhysMaterial.DEFAULT_A_DAMPING, PhysMaterial.DEFAULT_MASS_DENSITY, PhysMaterial.DEFAULT_RESTITUTION);
    }

    public static final class MatList {
        public static final PhysMaterial groundMat = new PhysMaterial(100.0f, PhysMaterial.DEFAULT_L_DAMPING, PhysMaterial.DEFAULT_A_DAMPING, PhysMaterial.NO_MASS, PhysMaterial.DEFAULT_RESTITUTION);
    }
}
