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

package javagems3d.system.resources.assets.models.helper.forms.D2;

import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.flat.MeshGui;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.models.helper.forms.BasicModelCreator;

public class VectorModel2D implements BasicModelCreator<Model2D> {
    private final Vector2f v1;
    private final Vector2f v2;

    public VectorModel2D(Vector2f v1, Vector2f v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Model2D generateModel(@Nullable ArbitraryArguments arguments) {
        return new Model2D(new Pose2D(), new MeshGui(this.generateMesh(arguments)));
    }

    @Override
    public RenderMesh generateMesh(@Nullable ArbitraryArguments arguments) {
        RenderMesh renderMesh = new RenderMesh();
        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);

        vaPositions.put(this.v1.x);
        vaPositions.put(this.v1.y);

        vaPositions.put(this.v2.x);
        vaPositions.put(this.v2.y);

        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(1);

        renderMesh.putVertexAttribute(vaPositions);
        renderMesh.putVertexAttribute(vaTextureCoordinates);
        renderMesh.bakeMesh();
        return renderMesh;
    }
}
