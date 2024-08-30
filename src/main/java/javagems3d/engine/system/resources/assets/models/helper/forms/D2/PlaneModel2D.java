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

package javagems3d.engine.system.resources.assets.models.helper.forms.D2;

import org.joml.Vector2f;
import javagems3d.engine.system.resources.assets.models.Model;
import javagems3d.engine.system.resources.assets.models.formats.Format2D;
import javagems3d.engine.system.resources.assets.models.helper.forms.BasicMesh;
import javagems3d.engine.system.resources.assets.models.mesh.Mesh;

public class PlaneModel2D implements BasicMesh<Format2D> {
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
    public Model<Format2D> generateModel() {
        return new Model<>(new Format2D(), this.generateMesh());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();

        mesh.pushPosition(this.v1.x);
        mesh.pushPosition(this.v1.y);
        mesh.pushPosition(this.zLevel);
        if (this.inverted) {
            mesh.pushTextureCoordinate(this.textureMin.x);
            mesh.pushTextureCoordinate(this.textureMax.y);
        } else {
            mesh.pushTextureCoordinate(this.textureMin.x);
            mesh.pushTextureCoordinate(this.textureMin.y);
        }

        mesh.pushPosition(this.v1.x);
        mesh.pushPosition(this.v2.y);
        mesh.pushPosition(this.zLevel);
        if (this.inverted) {
            mesh.pushTextureCoordinate(this.textureMin.x);
            mesh.pushTextureCoordinate(this.textureMin.y);
        } else {
            mesh.pushTextureCoordinate(this.textureMin.x);
            mesh.pushTextureCoordinate(this.textureMax.y);
        }

        mesh.pushPosition(this.v2.x);
        mesh.pushPosition(this.v2.y);
        mesh.pushPosition(this.zLevel);
        if (this.inverted) {
            mesh.pushTextureCoordinate(this.textureMax.x);
            mesh.pushTextureCoordinate(this.textureMin.y);
        } else {
            mesh.pushTextureCoordinate(this.textureMax.x);
            mesh.pushTextureCoordinate(this.textureMax.y);
        }

        mesh.pushPosition(this.v2.x);
        mesh.pushPosition(this.v1.y);
        mesh.pushPosition(this.zLevel);
        if (this.inverted) {
            mesh.pushTextureCoordinate(this.textureMax.x);
            mesh.pushTextureCoordinate(this.textureMax.y);
        } else {
            mesh.pushTextureCoordinate(this.textureMax.x);
            mesh.pushTextureCoordinate(this.textureMin.y);
        }

        mesh.pushIndex(0);
        mesh.pushIndex(1);
        mesh.pushIndex(2);
        mesh.pushIndex(3);
        mesh.pushIndex(0);
        mesh.pushIndex(2);

        mesh.bakeMesh();
        return mesh;
    }
}
