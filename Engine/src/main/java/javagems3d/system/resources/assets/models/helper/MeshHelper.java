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

package javagems3d.system.resources.assets.models.helper;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format2D;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.forms.D2.PlaneModel2D;
import javagems3d.system.resources.assets.models.helper.forms.D2.VectorModel2D;
import javagems3d.system.resources.assets.models.helper.forms.D3.PlaneModel3D;
import javagems3d.system.resources.assets.models.helper.forms.D3.SimplePlaneModel3D;
import javagems3d.system.resources.assets.models.helper.forms.D3.VectorModel3D;
import javagems3d.system.resources.assets.models.helper.forms.D3.WireBoxModel3D;
import javagems3d.system.resources.assets.models.mesh.Mesh;

public abstract class MeshHelper {
    public static Model<Format2D> generatePlane2DModel(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, v2, zLevel);
        return planeModel2D.generateModel();
    }

    public static Model<Format2D> generatePlane2DModelInverted(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, v2, zLevel);
        return planeModel2D.generateModel();
    }

    public static Model<Format2D> generatePlane2DModel(Vector2f v1, float zLevel, Vector2f textureMin, Vector2f textureMax, Vector2f size) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, size, zLevel, textureMin, textureMax);
        return planeModel2D.generateModel();
    }

    public static Model<Format2D> generatePlane2DModelInverted(Vector2f v1, float zLevel, Vector2f textureMin, Vector2f textureMax, Vector2f size) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, new Vector2f(v1.x + textureMax.x, v1.y + textureMax.y), zLevel, textureMin.div(size), textureMax.div(size));
        return planeModel2D.generateModel();
    }

    public static Model<Format3D> generateSimplePlane3DModel(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        SimplePlaneModel3D planeModel3D = new SimplePlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateModel();
    }

    public static Model<Format3D> generatePlane3DModel(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        PlaneModel3D planeModel3D = new PlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateModel();
    }

    public static Model<Format3D> generateVector3DModel3f(Vector3f v1, Vector3f v2) {
        VectorModel3D vectorModel3D = new VectorModel3D(v1, v2);
        return vectorModel3D.generateModel();
    }

    public static Model<Format3D> generateWirebox3DModel(Vector3f min, Vector3f max) {
        WireBoxModel3D wireBoxModel3D = new WireBoxModel3D(min, max);
        return wireBoxModel3D.generateModel();
    }

    public static Mesh generateVector2fMesh(Vector2f v1, Vector2f v2) {
        VectorModel2D vectorModel2D = new VectorModel2D(v1, v2);
        return vectorModel2D.generateMesh();
    }

    public static Mesh generatePlane2DMesh(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, v2, zLevel);
        return planeModel2D.generateMesh();
    }

    public static Mesh generatePlane2DMeshInverted(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, v2, zLevel);
        return planeModel2D.generateMesh();
    }

    public static Mesh generatePlane3DMesh(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        PlaneModel3D planeModel3D = new PlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateMesh();
    }

    public static Mesh generateSimplePlane3DMesh(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        SimplePlaneModel3D planeModel3D = new SimplePlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateMesh();
    }

    public static Mesh generateVector3DMesh3f(Vector3f v1, Vector3f v2) {
        VectorModel3D vectorModel3D = new VectorModel3D(v1, v2);
        return vectorModel3D.generateMesh();
    }

    public static Mesh generateWirebox3DMesh(Vector3f min, Vector3f max) {
        WireBoxModel3D wireBoxModel3D = new WireBoxModel3D(min, max);
        return wireBoxModel3D.generateMesh();
    }
}
