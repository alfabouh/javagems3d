/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.resources.assets.models.helper;

import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.models.helper.forms.D2.PlaneModel2D;
import javagems3d.system.resources.assets.models.helper.forms.D2.VectorModel2D;
import javagems3d.system.resources.assets.models.helper.forms.D3.PlaneModel3D;
import javagems3d.system.resources.assets.models.helper.forms.D3.SimplePlaneModel3D;
import javagems3d.system.resources.assets.models.helper.forms.D3.VectorModel3D;
import javagems3d.system.resources.assets.models.helper.forms.D3.WireBoxModel3D;

public abstract class MeshHelper {
    public static Model2D generatePlane2DModel(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, v2, zLevel);
        return planeModel2D.generateModel(null);
    }

    public static Model2D generatePlane2DModelInverted(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, v2, zLevel);
        return planeModel2D.generateModel(null);
    }

    public static Model2D generatePlane2DModel(Vector2f v1, float zLevel, Vector2f textureMin, Vector2f textureMax, Vector2f size) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, size, zLevel, textureMin, textureMax);
        return planeModel2D.generateModel(null);
    }

    public static Model2D generatePlane2DModelInverted(Vector2f v1, float zLevel, Vector2f textureMin, Vector2f textureMax, Vector2f size) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, new Vector2f(v1.x + textureMax.x, v1.y + textureMax.y), zLevel, textureMin.div(size), textureMax.div(size));
        return planeModel2D.generateModel(null);
    }

    public static Model3D generateSimplePlane3DModel(@Nullable ArbitraryArguments arguments, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        SimplePlaneModel3D planeModel3D = new SimplePlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateModel(arguments);
    }

    public static Model3D generatePlane3DModel(@Nullable ArbitraryArguments arguments, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        PlaneModel3D planeModel3D = new PlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateModel(arguments);
    }

    public static Model3D generateVector3DModel3f(Vector3f v1, Vector3f v2) {
        VectorModel3D vectorModel3D = new VectorModel3D(v1, v2);
        return vectorModel3D.generateModel(null);
    }

    public static Model3D generateWirebox3DModel(Vector3f min, Vector3f max) {
        WireBoxModel3D wireBoxModel3D = new WireBoxModel3D(min, max);
        return wireBoxModel3D.generateModel(null);
    }

    public static RenderMesh generateVector2fMesh(Vector2f v1, Vector2f v2) {
        VectorModel2D vectorModel2D = new VectorModel2D(v1, v2);
        return vectorModel2D.generateMesh(null);
    }

    public static RenderMesh generatePlane2DMesh(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(v1, v2, zLevel);
        return planeModel2D.generateMesh(null);
    }

    public static RenderMesh generatePlane2DMeshInverted(Vector2f v1, Vector2f v2, float zLevel) {
        PlaneModel2D planeModel2D = new PlaneModel2D(true, v1, v2, zLevel);
        return planeModel2D.generateMesh(null);
    }

    public static RenderMesh generatePlane3DMesh(@Nullable ArbitraryArguments arguments, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        PlaneModel3D planeModel3D = new PlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateMesh(arguments);
    }

    public static RenderMesh generateSimplePlane3DMesh(@Nullable ArbitraryArguments arguments, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        SimplePlaneModel3D planeModel3D = new SimplePlaneModel3D(v1, v2, v3, v4);
        return planeModel3D.generateMesh(arguments);
    }

    public static RenderMesh generateVector3DMesh3f(Vector3f v1, Vector3f v2) {
        VectorModel3D vectorModel3D = new VectorModel3D(v1, v2);
        return vectorModel3D.generateMesh(null);
    }

    public static RenderMesh generateWirebox3DMesh(Vector3f min, Vector3f max) {
        WireBoxModel3D wireBoxModel3D = new WireBoxModel3D(min, max);
        return wireBoxModel3D.generateMesh(null);
    }
}