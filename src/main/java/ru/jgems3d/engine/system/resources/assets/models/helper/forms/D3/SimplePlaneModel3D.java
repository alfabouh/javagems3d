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

package ru.jgems3d.engine.system.resources.assets.models.helper.forms.D3;

import org.joml.Vector3f;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.helper.forms.BasicMesh;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplePlaneModel3D implements BasicMesh<Format3D> {
    private final Vector3f v1;
    private final Vector3f v2;
    private final Vector3f v3;
    private final Vector3f v4;

    public SimplePlaneModel3D(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    private List<Vector3f> reorderPositions(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        List<Vector3f> vertices = new ArrayList<>(Arrays.asList(v1, v2, v3, v4));

        Vector3f center = new Vector3f();
        for (Vector3f vector3f : vertices) {
            center.add(vector3f);
        }
        center.div(vertices.size());

        vertices.sort((e1, e2) -> {
            Vector3f vec1 = new Vector3f(e1).sub(center);
            Vector3f vec2 = new Vector3f(e2).sub(center);
            return Double.compare(Math.atan2(vec1.y, vec1.z), Math.atan2(vec2.y, vec2.x));
        });

        return vertices;
    }

    private Vector3f getPosition(List<Float> list, int s) {
        return new Vector3f(list.get(s * 3), list.get(s * 3 + 1), list.get(s * 3 + 2));
    }

    @Override
    public Model<Format3D> generateModel() {
        return new Model<>(new Format3D(), this.generateMesh(), Material.createDefault());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();
        List<Vector3f> list = this.reorderPositions(this.v1, this.v2, this.v3, this.v4);

        Vector3f v1 = list.get(0);
        Vector3f v2 = list.get(1);
        Vector3f v3 = list.get(2);
        Vector3f v4 = list.get(3);

        mesh.pushPosition(v1.x);
        mesh.pushPosition(v1.y);
        mesh.pushPosition(v1.z);
        mesh.pushTextureCoordinate(0.0f);
        mesh.pushTextureCoordinate(1.0f);

        mesh.pushPosition(v2.x);
        mesh.pushPosition(v2.y);
        mesh.pushPosition(v2.z);
        mesh.pushTextureCoordinate(1.0f);
        mesh.pushTextureCoordinate(1.0f);

        mesh.pushPosition(v3.x);
        mesh.pushPosition(v3.y);
        mesh.pushPosition(v3.z);
        mesh.pushTextureCoordinate(1.0f);
        mesh.pushTextureCoordinate(0.0f);

        mesh.pushPosition(v4.x);
        mesh.pushPosition(v4.y);
        mesh.pushPosition(v4.z);
        mesh.pushTextureCoordinate(0.0f);
        mesh.pushTextureCoordinate(0.0f);


        mesh.pushPosition(v4.x);
        mesh.pushPosition(v4.y);
        mesh.pushPosition(v4.z);
        mesh.pushTextureCoordinate(0.0f);
        mesh.pushTextureCoordinate(0.0f);

        mesh.pushPosition(v3.x);
        mesh.pushPosition(v3.y);
        mesh.pushPosition(v3.z);
        mesh.pushTextureCoordinate(1.0f);
        mesh.pushTextureCoordinate(0.0f);

        mesh.pushPosition(v2.x);
        mesh.pushPosition(v2.y);
        mesh.pushPosition(v2.z);
        mesh.pushTextureCoordinate(1.0f);
        mesh.pushTextureCoordinate(1.0f);

        mesh.pushPosition(v1.x);
        mesh.pushPosition(v1.y);
        mesh.pushPosition(v1.z);
        mesh.pushTextureCoordinate(0.0f);
        mesh.pushTextureCoordinate(1.0f);

        mesh.pushIndex(1);
        mesh.pushIndex(2);
        mesh.pushIndex(0);
        mesh.pushIndex(3);
        mesh.pushIndex(0);
        mesh.pushIndex(2);

        mesh.pushIndex(5);
        mesh.pushIndex(6);
        mesh.pushIndex(4);
        mesh.pushIndex(7);
        mesh.pushIndex(4);
        mesh.pushIndex(6);

        mesh.bakeMesh();
        return mesh;
    }
}
