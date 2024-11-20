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

import javagems3d.system.resources.assets.models.mesh.DirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import org.joml.Vector3f;
import javagems3d.system.resources.old.MaterialOld;
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
        return new Model<>(new Format3D(), this.generateMesh(), MaterialOld.createDefault());
    }

    @Override
    public DirectRenderMesh generateMesh() {
        DirectRenderMesh directRenderMesh = new DirectRenderMesh();

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

        directRenderMesh.putVertexIndex(0);
        directRenderMesh.putVertexIndex(1);

        directRenderMesh.putVertexIndex(1);
        directRenderMesh.putVertexIndex(2);

        directRenderMesh.putVertexIndex(2);
        directRenderMesh.putVertexIndex(3);

        directRenderMesh.putVertexIndex(3);
        directRenderMesh.putVertexIndex(0);

        directRenderMesh.putVertexIndex(4);
        directRenderMesh.putVertexIndex(5);

        directRenderMesh.putVertexIndex(5);
        directRenderMesh.putVertexIndex(6);

        directRenderMesh.putVertexIndex(6);
        directRenderMesh.putVertexIndex(7);

        directRenderMesh.putVertexIndex(7);
        directRenderMesh.putVertexIndex(4);

        directRenderMesh.putVertexIndex(0);
        directRenderMesh.putVertexIndex(4);

        directRenderMesh.putVertexIndex(1);
        directRenderMesh.putVertexIndex(5);

        directRenderMesh.putVertexIndex(2);
        directRenderMesh.putVertexIndex(6);

        directRenderMesh.putVertexIndex(3);
        directRenderMesh.putVertexIndex(7);

        directRenderMesh.addVertexAttributeInMesh(vaPositions);

        directRenderMesh.bakeMesh();
        return directRenderMesh;
    }
}