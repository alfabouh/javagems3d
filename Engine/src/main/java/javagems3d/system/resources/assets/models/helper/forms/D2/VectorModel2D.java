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

package javagems3d.system.resources.assets.models.helper.forms.D2;

import javagems3d.system.resources.assets.models.mesh.DirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import org.joml.Vector2f;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.helper.forms.BasicModelCreator;

public class VectorModel2D implements BasicModelCreator<Format2D> {
    private final Vector2f v1;
    private final Vector2f v2;

    public VectorModel2D(Vector2f v1, Vector2f v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Model<Format2D> generateModel() {
        return new Model<>(new Format2D(), this.generateMesh());
    }

    @Override
    public DirectRenderMesh generateMesh() {
        DirectRenderMesh directRenderMesh = new DirectRenderMesh();
        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);

        vaPositions.put(this.v1.x);
        vaPositions.put(this.v1.y);

        vaPositions.put(this.v2.x);
        vaPositions.put(this.v2.y);

        directRenderMesh.putVertexIndex(0);
        directRenderMesh.putVertexIndex(1);

        directRenderMesh.addVertexAttributeInMesh(vaPositions);
        directRenderMesh.addVertexAttributeInMesh(vaTextureCoordinates);
        directRenderMesh.bakeMesh();
        return directRenderMesh;
    }
}
