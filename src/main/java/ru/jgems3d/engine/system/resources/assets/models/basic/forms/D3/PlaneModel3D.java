package ru.jgems3d.engine.system.resources.assets.models.basic.forms.D3;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.forms.BasicMesh;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

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

        mesh.putPositionValue(v1.x);
        mesh.putPositionValue(v1.y);
        mesh.putPositionValue(v1.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue(v2.x);
        mesh.putPositionValue(v2.y);
        mesh.putPositionValue(v2.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue(v3.x);
        mesh.putPositionValue(v3.y);
        mesh.putPositionValue(v3.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue(v4.x);
        mesh.putPositionValue(v4.y);
        mesh.putPositionValue(v4.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(1.0f);


        mesh.putPositionValue(v4.x);
        mesh.putPositionValue(v4.y);
        mesh.putPositionValue(v4.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue(v3.x);
        mesh.putPositionValue(v3.y);
        mesh.putPositionValue(v3.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue(v2.x);
        mesh.putPositionValue(v2.y);
        mesh.putPositionValue(v2.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue(v1.x);
        mesh.putPositionValue(v1.y);
        mesh.putPositionValue(v1.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putIndexValue(1);
        mesh.putIndexValue(2);
        mesh.putIndexValue(0);
        mesh.putIndexValue(3);
        mesh.putIndexValue(0);
        mesh.putIndexValue(2);

        mesh.putIndexValue(5);
        mesh.putIndexValue(6);
        mesh.putIndexValue(4);
        mesh.putIndexValue(7);
        mesh.putIndexValue(4);
        mesh.putIndexValue(6);

        Vector3f vAB = this.getPosition(mesh.getAttributePositions(), 1).sub(this.getPosition(mesh.getAttributePositions(), 0));
        Vector3f vAD = this.getPosition(mesh.getAttributePositions(), 3).sub(this.getPosition(mesh.getAttributePositions(), 0));
        Vector3f vN = vAB.cross(vAD).normalize();

        for (int i = 0; i < 4; i++) {
            mesh.putNormalValue(vN.x);
            mesh.putNormalValue(vN.y);
            mesh.putNormalValue(vN.z);
        }

        for (int i = 0; i < 4; i++) {
            mesh.putNormalValue(-vN.x);
            mesh.putNormalValue(-vN.y);
            mesh.putNormalValue(-vN.z);
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
            mesh.putTangentValue(tan1x);
            mesh.putTangentValue(tan1y);
            mesh.putTangentValue(tan1z);

            mesh.putBitangentValue(biTan1x);
            mesh.putBitangentValue(biTan1y);
            mesh.putBitangentValue(biTan1z);

            mesh.putTangentValue(tan2x);
            mesh.putTangentValue(tan2y);
            mesh.putTangentValue(tan2z);

            mesh.putBitangentValue(biTan2x);
            mesh.putBitangentValue(biTan2y);
            mesh.putBitangentValue(biTan2z);
        }

        for (int i = 0; i < 4; i++) {
            mesh.putTangentValue(-tan1x);
            mesh.putTangentValue(-tan1y);
            mesh.putTangentValue(-tan1z);

            mesh.putBitangentValue(-biTan1x);
            mesh.putBitangentValue(-biTan1y);
            mesh.putBitangentValue(-biTan1z);

            mesh.putTangentValue(-tan2x);
            mesh.putTangentValue(-tan2y);
            mesh.putTangentValue(-tan2z);

            mesh.putBitangentValue(-biTan2x);
            mesh.putBitangentValue(-biTan2y);
            mesh.putBitangentValue(-biTan2z);
        }

        mesh.bakeMesh();
        return mesh;
    }
}
