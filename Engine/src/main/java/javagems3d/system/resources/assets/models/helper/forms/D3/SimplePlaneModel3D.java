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

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.joml.Vector3f;

import javagems3d.system.resources.assets.models.helper.forms.BasicModelCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplePlaneModel3D implements BasicModelCreator<Model3D> {
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
    public Model3D generateModel() {
        return new Model3D(new Pose3D(), new MeshGroup(new MeshNode3D<>(this.generateMesh())));
    }

    @Override
    public RenderMesh generateMesh() {
        RenderMesh renderMesh = new RenderMesh();
        List<Vector3f> list = this.reorderPositions(this.v1, this.v2, this.v3, this.v4);

        Vector3f v1 = list.get(0);
        Vector3f v2 = list.get(1);
        Vector3f v3 = list.get(2);
        Vector3f v4 = list.get(3);

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        
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

        renderMesh.putVertexIndex(1);
        renderMesh.putVertexIndex(2);
        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(3);
        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(2);

        renderMesh.putVertexIndex(5);
        renderMesh.putVertexIndex(6);
        renderMesh.putVertexIndex(4);
        renderMesh.putVertexIndex(7);
        renderMesh.putVertexIndex(4);
        renderMesh.putVertexIndex(6);

        renderMesh.putVertexAttribute(vaPositions);
        renderMesh.putVertexAttribute(vaTextureCoordinates);

        renderMesh.bakeMesh();
        return renderMesh;
    }
}
