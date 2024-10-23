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

import javagems3d.system.resources.assets.models.mesh.attributes.pointer.DefaultPointers;
import javagems3d.system.resources.assets.models.mesh.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.Mesh;
import org.joml.Vector3f;
import javagems3d.system.resources.assets.material.Material;
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
        return new Model<>(new Format3D(), this.generateMesh(), Material.createDefault());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultPointers.POSITIONS);

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

        mesh.putVertexIndex(0);
        mesh.putVertexIndex(1);

        mesh.putVertexIndex(1);
        mesh.putVertexIndex(2);

        mesh.putVertexIndex(2);
        mesh.putVertexIndex(3);

        mesh.putVertexIndex(3);
        mesh.putVertexIndex(0);

        mesh.putVertexIndex(4);
        mesh.putVertexIndex(5);

        mesh.putVertexIndex(5);
        mesh.putVertexIndex(6);

        mesh.putVertexIndex(6);
        mesh.putVertexIndex(7);

        mesh.putVertexIndex(7);
        mesh.putVertexIndex(4);

        mesh.putVertexIndex(0);
        mesh.putVertexIndex(4);

        mesh.putVertexIndex(1);
        mesh.putVertexIndex(5);

        mesh.putVertexIndex(2);
        mesh.putVertexIndex(6);

        mesh.putVertexIndex(3);
        mesh.putVertexIndex(7);

        mesh.addVertexAttributeInMesh(vaPositions);

        mesh.bakeMesh();
        return mesh;
    }
}