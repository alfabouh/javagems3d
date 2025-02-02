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

package javagems3d.system.resources.assets.models.helper.forms.D2;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.flat.MeshGui;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.pose.Pose2D;
import org.joml.Vector2f;

import javagems3d.system.resources.assets.models.helper.forms.BasicModelCreator;

public class PlaneModel2D implements BasicModelCreator<Model2D> {
    private final Vector2f v1;
    private final Vector2f v2;
    private final Vector2f textureMin;
    private final Vector2f textureMax;
    private final float zLevel;
    private final boolean inverted;

    public PlaneModel2D(Vector2f v1, Vector2f v2, float zLevel) {
        this(false, v1, v2, zLevel, new Vector2f(0.0f), new Vector2f(1.0f));
    }

    public PlaneModel2D(Vector2f v1, Vector2f v2, float zLevel, Vector2f textureMin, Vector2f textureMax) {
        this(false, v1, v2, zLevel, textureMin, textureMax);
    }

    public PlaneModel2D(boolean inverted, Vector2f v1, Vector2f v2, float zLevel) {
        this(inverted, v1, v2, zLevel, new Vector2f(0.0f), new Vector2f(1.0f));
    }

    public PlaneModel2D(boolean inverted, Vector2f v1, Vector2f v2, float zLevel, Vector2f textureMin, Vector2f textureMax) {
        this.v1 = v1;
        this.v2 = v2;
        this.zLevel = zLevel;
        this.inverted = inverted;
        this.textureMin = textureMin;
        this.textureMax = textureMax;
    }


    @Override
    public Model2D generateModel() {
        return new Model2D(new Pose2D(), new MeshGui(this.generateMesh()));
    }

    @Override
    public RenderMesh generateMesh() {
        RenderMesh renderMesh = new RenderMesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);

        vaPositions.put(this.v1.x);
        vaPositions.put(this.v1.y);
        vaPositions.put(this.zLevel);
        if (this.inverted) {
            vaTextureCoordinates.put(this.textureMin.x);
            vaTextureCoordinates.put(this.textureMax.y);
        } else {
            vaTextureCoordinates.put(this.textureMin.x);
            vaTextureCoordinates.put(this.textureMin.y);
        }

        vaPositions.put(this.v1.x);
        vaPositions.put(this.v2.y);
        vaPositions.put(this.zLevel);
        if (this.inverted) {
            vaTextureCoordinates.put(this.textureMin.x);
            vaTextureCoordinates.put(this.textureMin.y);
        } else {
            vaTextureCoordinates.put(this.textureMin.x);
            vaTextureCoordinates.put(this.textureMax.y);
        }

        vaPositions.put(this.v2.x);
        vaPositions.put(this.v2.y);
        vaPositions.put(this.zLevel);
        if (this.inverted) {
            vaTextureCoordinates.put(this.textureMax.x);
            vaTextureCoordinates.put(this.textureMin.y);
        } else {
            vaTextureCoordinates.put(this.textureMax.x);
            vaTextureCoordinates.put(this.textureMax.y);
        }

        vaPositions.put(this.v2.x);
        vaPositions.put(this.v1.y);
        vaPositions.put(this.zLevel);
        if (this.inverted) {
            vaTextureCoordinates.put(this.textureMax.x);
            vaTextureCoordinates.put(this.textureMax.y);
        } else {
            vaTextureCoordinates.put(this.textureMax.x);
            vaTextureCoordinates.put(this.textureMin.y);
        }

        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(1);
        renderMesh.putVertexIndex(2);
        renderMesh.putVertexIndex(3);
        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(2);

        renderMesh.putVertexAttribute(vaPositions);
        renderMesh.putVertexAttribute(vaTextureCoordinates);

        renderMesh.bakeMesh();
        return renderMesh;
    }
}
