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

public class WireBoxModel3D implements BasicModelCreator<Format3D> {
    private final Vector3f min;
    private final Vector3f max;

    public WireBoxModel3D(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Model<Format3D> generateModel() {
        return new Model<>(new Format3D(), new MeshGroup(new MeshGroup.MeshGroupNode(this.generateMesh(), Material.createDefault())));
    }

    @Override
    public RenderMesh generateMesh() {
        RenderMesh renderMesh = new RenderMesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);

        vaPositions.put(this.min.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.min.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.min.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.max.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.max.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.max.z);

        vaPositions.put(this.min.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.max.z);

        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(1);

        renderMesh.putVertexIndex(1);
        renderMesh.putVertexIndex(2);

        renderMesh.putVertexIndex(2);
        renderMesh.putVertexIndex(3);

        renderMesh.putVertexIndex(3);
        renderMesh.putVertexIndex(0);

        renderMesh.putVertexIndex(4);
        renderMesh.putVertexIndex(5);

        renderMesh.putVertexIndex(5);
        renderMesh.putVertexIndex(6);

        renderMesh.putVertexIndex(6);
        renderMesh.putVertexIndex(7);

        renderMesh.putVertexIndex(7);
        renderMesh.putVertexIndex(4);

        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(4);

        renderMesh.putVertexIndex(1);
        renderMesh.putVertexIndex(5);

        renderMesh.putVertexIndex(2);
        renderMesh.putVertexIndex(6);

        renderMesh.putVertexIndex(3);
        renderMesh.putVertexIndex(7);

        renderMesh.putVertexAttribute(vaPositions);

        renderMesh.bakeMesh();
        return renderMesh;
    }
}