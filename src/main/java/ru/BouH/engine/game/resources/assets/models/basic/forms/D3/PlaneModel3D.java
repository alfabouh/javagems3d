package ru.BouH.engine.game.resources.assets.models.basic.forms.D3;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaneModel3D implements BasicMesh<Format3D> {
    private final Vector3d v1;
    private final Vector3d v2;
    private final Vector3d v3;
    private final Vector3d v4;

    public PlaneModel3D(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    private List<Vector3d> reorderPositions(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        List<Vector3d> vertices = new ArrayList<>(Arrays.asList(v1, v2, v3, v4));

        Vector3d center = new Vector3d();
        for (Vector3d vector3d : vertices) {
            center.add(vector3d);
        }
        center.div(vertices.size());

        vertices.sort((e1, e2) -> {
            Vector3d vec1 = new Vector3d(e1).sub(center);
            Vector3d vec2 = new Vector3d(e2).sub(center);
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
        List<Vector3d> list = this.reorderPositions(this.v1, this.v2, this.v3, this.v4);

        Vector3d v1 = list.get(0);
        Vector3d v2 = list.get(1);
        Vector3d v3 = list.get(2);
        Vector3d v4 = list.get(3);

        mesh.putPositionValue((float) v1.x);
        mesh.putPositionValue((float) v1.y);
        mesh.putPositionValue((float) v1.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue((float) v2.x);
        mesh.putPositionValue((float) v2.y);
        mesh.putPositionValue((float) v2.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue((float) v3.x);
        mesh.putPositionValue((float) v3.y);
        mesh.putPositionValue((float) v3.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue((float) v4.x);
        mesh.putPositionValue((float) v4.y);
        mesh.putPositionValue((float) v4.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(1.0f);


        mesh.putPositionValue((float) v4.x);
        mesh.putPositionValue((float) v4.y);
        mesh.putPositionValue((float) v4.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue((float) v3.x);
        mesh.putPositionValue((float) v3.y);
        mesh.putPositionValue((float) v3.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue((float) v2.x);
        mesh.putPositionValue((float) v2.y);
        mesh.putPositionValue((float) v2.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue((float) v1.x);
        mesh.putPositionValue((float) v1.y);
        mesh.putPositionValue((float) v1.z);
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

        Vector3d edge1 = new Vector3d(v2).sub(v1);
        Vector3d edge2 = new Vector3d(v3).sub(v1);
        Vector2d deltaUV1 = new Vector2d(1.0f, 0.0f).sub(new Vector2d(0.0f, 0.0f));
        Vector2d deltaUV2 = new Vector2d(1.0f, 1.0f).sub(new Vector2d(0.0f, 0.0f));

        float f = (float) (1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y));

        float tan1x = (float) (f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x));
        float tan1y = (float) (f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y));
        float tan1z = (float) (f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z));
        float biTan1x = (float) (f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x));
        float biTan1y = (float) (f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y));
        float biTan1z = (float) (f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z));

        Vector3d edge3 = new Vector3d(v3).sub(v1);
        Vector3d edge4 = new Vector3d(v4).sub(v1);
        Vector2d deltaUV3 = new Vector2d(1.0f, 1.0f).sub(new Vector2d(0.0f, 0.0f));
        Vector2d deltaUV4 = new Vector2d(0.0f, 1.0f).sub(new Vector2d(0.0f, 0.0f));

        float f0 = (float) (1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y));

        float tan2x = (float) (f0 * (deltaUV4.y * edge3.x - deltaUV3.y * edge4.x));
        float tan2y = (float) (f0 * (deltaUV4.y * edge3.y - deltaUV3.y * edge4.y));
        float tan2z = (float) (f0 * (deltaUV4.y * edge3.z - deltaUV3.y * edge4.z));

        float biTan2x = (float) (f0 * (-deltaUV4.x * edge3.x + deltaUV3.x * edge4.x));
        float biTan2y = (float) (f0 * (-deltaUV4.x * edge3.y + deltaUV3.x * edge4.y));
        float biTan2z = (float) (f0 * (-deltaUV4.x * edge3.z + deltaUV3.x * edge4.z));

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
