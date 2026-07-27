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
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.physics.entities.properties.material.PhysMaterial;

@JSCodingClass(binding = "JSPhysMaterial", description = "Physical material defining friction, damping and density.")
public record JSPhysMaterial(float friction, float linearDamping, float angularDamping, float density, float restitution) {

    @JSCodingConstructor(description = "Create material", paramNames = {"friction", "linearDamping", "angularDamping", "density"})
    public JSPhysMaterial(float friction, float linearDamping, float angularDamping, float density, float restitution) {
        this.friction = friction;
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;
        this.density = density;
        this.restitution = restitution;
    }

    @JSCodingFunctionOrMethod(description = "Create default material")
    public static JSPhysMaterial createDefault() {
        PhysMaterial mat = PhysMaterial.createDefaultMaterial();
        return new JSPhysMaterial(mat.friction(), mat.l_damping(), mat.a_damping(), mat.m_density(), mat.restitution());
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public PhysMaterial getJavaMaterial() {
        return new PhysMaterial(this.friction, this.linearDamping, this.angularDamping, this.density, this.restitution);
    }
}