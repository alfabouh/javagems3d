package ru.BouH.engine.game.resources.assets.models.basic;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.forms.D2.PlaneModel2D;
import ru.BouH.engine.game.resources.assets.models.basic.forms.D2.VectorModel2D;
import ru.BouH.engine.game.resources.assets.models.basic.forms.D3.PlaneModel3D;
import ru.BouH.engine.game.resources.assets.models.basic.forms.D3.VectorModel3D;
import ru.BouH.engine.game.resources.assets.models.basic.forms.D3.WireBoxModel3D;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;

public class MeshHelper {
    public static Model<Format2D> generateVector2DModel(Vector2d v1, Vector2d v2) {
        VectorModel2D vectorModel2D = new VectorModel2D(v1, v2);
        return vectorModel2D.generateModel();
    }

    public static Model<Format2D> generatePlane2DModel(Vector2d v1, Vector2d v2, int zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, v2, zLevel);
        return planeModel2D.generateModel();
    }

    public static Model<Format2D> generatePlane2DModelInverted(Vector2d v1, Vector2d v2, int zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, v2, zLevel);
        return planeModel2D.generateModel();
    }

    public static Model<Format3D> generatePlane3DModel(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        PlaneModel3D planeModel3D = new PlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateModel();
    }

    public static Model<Format3D> generateVector3DModel(Vector3d v1, Vector3d v2) {
        VectorModel3D vectorModel3D = new VectorModel3D(v1, v2);
        return vectorModel3D.generateModel();
    }

    public static Model<Format3D> generateWirebox3DModel(Vector3d min, Vector3d max) {
        WireBoxModel3D wireBoxModel3D = new WireBoxModel3D(min, max);
        return wireBoxModel3D.generateModel();
    }

    public static Mesh generateVector2DMesh(Vector2d v1, Vector2d v2) {
        VectorModel2D vectorModel2D = new VectorModel2D(v1, v2);
        return vectorModel2D.generateMesh();
    }

    public static Mesh generatePlane2DMesh(Vector2d v1, Vector2d v2, int zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, v2, zLevel);
        return planeModel2D.generateMesh();
    }

    public static Mesh generatePlane2DMeshInverted(Vector2d v1, Vector2d v2, int zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, v2, zLevel);
        return planeModel2D.generateMesh();
    }

    public static Mesh generatePlane3DMesh(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        PlaneModel3D planeModel3D = new PlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateMesh();
    }

    public static Mesh generateVector3DMesh(Vector3d v1, Vector3d v2) {
        VectorModel3D vectorModel3D = new VectorModel3D(v1, v2);
        return vectorModel3D.generateMesh();
    }

    public static Mesh generateWirebox3DMesh(Vector3d min, Vector3d max) {
        WireBoxModel3D wireBoxModel3D = new WireBoxModel3D(min, max);
        return wireBoxModel3D.generateMesh();
    }
}
