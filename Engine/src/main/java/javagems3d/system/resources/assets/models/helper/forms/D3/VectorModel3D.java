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

package javagems3d.system.resources.assets.models.helper.forms.D3;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import org.joml.Vector3f;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.forms.BasicModelCreator;

public class VectorModel3D implements BasicModelCreator<Format3D> {
    private final Vector3f v1;
    private final Vector3f v2;

    public VectorModel3D(Vector3f v1, Vector3f v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Model<Format3D> generateModel() {
        return new Model<>(new Format3D(), new MeshGroup(new MeshGroup.MeshGroupNode(this.generateMesh(), Material.createDefault())));
    }

    @Override
    public RenderMesh generateMesh() {
        RenderMesh renderMesh = new RenderMesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);

        vaPositions.put(this.v1.x);
        vaPositions.put(this.v1.y);
        vaPositions.put(this.v1.z);

        vaPositions.put(this.v2.x);
        vaPositions.put(this.v2.y);
        vaPositions.put(this.v2.z);

        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(1);

        renderMesh.putVertexAttribute(vaPositions);

        renderMesh.bakeMesh();
        return renderMesh;
    }
}
