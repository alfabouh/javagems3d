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

import org.joml.Vector3f;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.forms.BasicMesh;
import javagems3d.system.resources.assets.models.mesh.Mesh;

public class VectorModel3D implements BasicMesh<Format3D> {
    private final Vector3f v1;
    private final Vector3f v2;

    public VectorModel3D(Vector3f v1, Vector3f v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Model<Format3D> generateModel() {
        return new Model<>(new Format3D(), this.generateMesh(), Material.createDefault());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();
        mesh.pushPosition(this.v1.x);
        mesh.pushPosition(this.v1.y);
        mesh.pushPosition(this.v1.z);

        mesh.pushPosition(this.v2.x);
        mesh.pushPosition(this.v2.y);
        mesh.pushPosition(this.v2.z);

        mesh.pushIndex(0);
        mesh.pushIndex(1);

        mesh.bakeMesh();
        return mesh;
    }
}
