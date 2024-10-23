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
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.forms.BasicModelCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaneModel3D implements BasicModelCreator<Format3D> {
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

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultPointers.POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultPointers.TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultPointers.NORMALS);
        FloatVertexAttribute vaTangents = new FloatVertexAttribute(DefaultPointers.TANGENTS);
        FloatVertexAttribute vaBiTangents = new FloatVertexAttribute(DefaultPointers.BI_TANGENTS);

        Vector3f v1 = list.get(0);
        Vector3f v2 = list.get(1);
        Vector3f v3 = list.get(2);
        Vector3f v4 = list.get(3);

        vaPositions.put(v1.x);
        vaPositions.put(v1.y);
        vaPositions.put(v1.z);
        vaTextureCoordinates.put(0.0f);
        vaTextureCoordinates.put(1.0f);

        vaPositions.put(v2.x);
        vaPositions.put(v2.y);
        vaPositions.put(v2.z);
        vaTextureCoordinates.put(1.0f);
        vaTextureCoordinates.put(1.0f);

        vaPositions.put(v3.x);
        vaPositions.put(v3.y);
        vaPositions.put(v3.z);
        vaTextureCoordinates.put(1.0f);
        vaTextureCoordinates.put(0.0f);

        vaPositions.put(v4.x);
        vaPositions.put(v4.y);
        vaPositions.put(v4.z);
        vaTextureCoordinates.put(0.0f);
        vaTextureCoordinates.put(0.0f);


        vaPositions.put(v4.x);
        vaPositions.put(v4.y);
        vaPositions.put(v4.z);
        vaTextureCoordinates.put(0.0f);
        vaTextureCoordinates.put(0.0f);

        vaPositions.put(v3.x);
        vaPositions.put(v3.y);
        vaPositions.put(v3.z);
        vaTextureCoordinates.put(1.0f);
        vaTextureCoordinates.put(0.0f);

        vaPositions.put(v2.x);
        vaPositions.put(v2.y);
        vaPositions.put(v2.z);
        vaTextureCoordinates.put(1.0f);
        vaTextureCoordinates.put(1.0f);

        vaPositions.put(v1.x);
        vaPositions.put(v1.y);
        vaPositions.put(v1.z);
        vaTextureCoordinates.put(0.0f);
        vaTextureCoordinates.put(1.0f);

        mesh.putVertexIndex(1);
        mesh.putVertexIndex(2);
        mesh.putVertexIndex(0);
        mesh.putVertexIndex(3);
        mesh.putVertexIndex(0);
        mesh.putVertexIndex(2);

        mesh.putVertexIndex(5);
        mesh.putVertexIndex(6);
        mesh.putVertexIndex(4);
        mesh.putVertexIndex(7);
        mesh.putVertexIndex(4);
        mesh.putVertexIndex(6);

        Vector3f vAB = this.getPosition(vaPositions.getValues(), 1).sub(this.getPosition(vaPositions.getValues(), 0));
        Vector3f vAD = this.getPosition(vaPositions.getValues(), 3).sub(this.getPosition(vaPositions.getValues(), 0));
        Vector3f vN = vAB.cross(vAD).normalize();

        for (int i = 0; i < 4; i++) {
            vaNormals.put(vN.x);
            vaNormals.put(vN.y);
            vaNormals.put(vN.z);
        }

        for (int i = 0; i < 4; i++) {
            vaNormals.put(-vN.x);
            vaNormals.put(-vN.y);
            vaNormals.put(-vN.z);
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
            vaTangents.put(tan1x);
            vaTangents.put(tan1y);
            vaTangents.put(tan1z);

            vaBiTangents.put(biTan1x);
            vaBiTangents.put(biTan1y);
            vaBiTangents.put(biTan1z);

            vaTangents.put(tan2x);
            vaTangents.put(tan2y);
            vaTangents.put(tan2z);

            vaBiTangents.put(biTan2x);
            vaBiTangents.put(biTan2y);
            vaBiTangents.put(biTan2z);
        }

        for (int i = 0; i < 4; i++) {
            vaTangents.put(-tan1x);
            vaTangents.put(-tan1y);
            vaTangents.put(-tan1z);

            vaBiTangents.put(-biTan1x);
            vaBiTangents.put(-biTan1y);
            vaBiTangents.put(-biTan1z);

            vaTangents.put(-tan2x);
            vaTangents.put(-tan2y);
            vaTangents.put(-tan2z);

            vaBiTangents.put(-biTan2x);
            vaBiTangents.put(-biTan2y);
            vaBiTangents.put(-biTan2z);
        }

        mesh.addVertexAttributeInMesh(vaPositions);
        mesh.addVertexAttributeInMesh(vaTextureCoordinates);
        mesh.addVertexAttributeInMesh(vaNormals);
        mesh.addVertexAttributeInMesh(vaTangents);
        mesh.addVertexAttributeInMesh(vaBiTangents);

        mesh.bakeMesh();
        return mesh;
    }
}
