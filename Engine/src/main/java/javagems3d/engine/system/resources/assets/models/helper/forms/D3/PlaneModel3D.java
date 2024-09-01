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

package javagems3d.engine.system.resources.assets.models.helper.forms.D3;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.engine.system.resources.assets.material.Material;
import javagems3d.engine.system.resources.assets.models.Model;
import javagems3d.engine.system.resources.assets.models.formats.Format3D;
import javagems3d.engine.system.resources.assets.models.helper.forms.BasicMesh;
import javagems3d.engine.system.resources.assets.models.mesh.Mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaneModel3D implements BasicMesh<Format3D> {
    private final Vector3f v1;
    private final Vector3f v2;
    private final Vector3f v3;
    private final Vector3f v4;

    public PlaneModel3D(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
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

        Vector3f vAB = this.getPosition(mesh.getAttributePositions(), 1).sub(this.getPosition(mesh.getAttributePositions(), 0));
        Vector3f vAD = this.getPosition(mesh.getAttributePositions(), 3).sub(this.getPosition(mesh.getAttributePositions(), 0));
        Vector3f vN = vAB.cross(vAD).normalize();

        for (int i = 0; i < 4; i++) {
            mesh.pushNormal(vN.x);
            mesh.pushNormal(vN.y);
            mesh.pushNormal(vN.z);
        }

        for (int i = 0; i < 4; i++) {
            mesh.pushNormal(-vN.x);
            mesh.pushNormal(-vN.y);
            mesh.pushNormal(-vN.z);
        }

        Vector3f edge1 = new Vector3f(v2).sub(v1);
        Vector3f edge2 = new Vector3f(v3).sub(v1);
        Vector2f deltaUV1 = new Vector2f(1.0f, 0.0f).sub(new Vector2f(0.0f, 0.0f));
        Vector2f deltaUV2 = new Vector2f(1.0f, 1.0f).sub(new Vector2f(0.0f, 0.0f));

        float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

        float tan1x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
        float tan1y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
        float tan1z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);
        float biTan1x = f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x);
        float biTan1y = f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y);
        float biTan1z = f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z);

        Vector3f edge3 = new Vector3f(v3).sub(v1);
        Vector3f edge4 = new Vector3f(v4).sub(v1);
        Vector2f deltaUV3 = new Vector2f(1.0f, 1.0f).sub(new Vector2f(0.0f, 0.0f));
        Vector2f deltaUV4 = new Vector2f(0.0f, 1.0f).sub(new Vector2f(0.0f, 0.0f));

        float f0 = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

        float tan2x = f0 * (deltaUV4.y * edge3.x - deltaUV3.y * edge4.x);
        float tan2y = f0 * (deltaUV4.y * edge3.y - deltaUV3.y * edge4.y);
        float tan2z = f0 * (deltaUV4.y * edge3.z - deltaUV3.y * edge4.z);

        float biTan2x = f0 * (-deltaUV4.x * edge3.x + deltaUV3.x * edge4.x);
        float biTan2y = f0 * (-deltaUV4.x * edge3.y + deltaUV3.x * edge4.y);
        float biTan2z = f0 * (-deltaUV4.x * edge3.z + deltaUV3.x * edge4.z);

        for (int i = 0; i < 4; i++) {
            mesh.pushTangent(tan1x);
            mesh.pushTangent(tan1y);
            mesh.pushTangent(tan1z);

            mesh.pushBiTangent(biTan1x);
            mesh.pushBiTangent(biTan1y);
            mesh.pushBiTangent(biTan1z);

            mesh.pushTangent(tan2x);
            mesh.pushTangent(tan2y);
            mesh.pushTangent(tan2z);

            mesh.pushBiTangent(biTan2x);
            mesh.pushBiTangent(biTan2y);
            mesh.pushBiTangent(biTan2z);
        }

        for (int i = 0; i < 4; i++) {
            mesh.pushTangent(-tan1x);
            mesh.pushTangent(-tan1y);
            mesh.pushTangent(-tan1z);

            mesh.pushBiTangent(-biTan1x);
            mesh.pushBiTangent(-biTan1y);
            mesh.pushBiTangent(-biTan1z);

            mesh.pushTangent(-tan2x);
            mesh.pushTangent(-tan2y);
            mesh.pushTangent(-tan2z);

            mesh.pushBiTangent(-biTan2x);
            mesh.pushBiTangent(-biTan2y);
            mesh.pushBiTangent(-biTan2z);
        }

        mesh.bakeMesh();
        return mesh;
    }
}
