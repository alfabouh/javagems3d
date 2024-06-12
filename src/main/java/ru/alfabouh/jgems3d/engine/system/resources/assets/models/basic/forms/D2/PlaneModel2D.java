package ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.forms.D2;

import org.joml.Vector2f;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.forms.BasicMesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

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

        mesh.putPositionValue(this.v1.x);
        mesh.putPositionValue(this.v1.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(this.textureMin.x);
            mesh.putTextureCoordinateValue(this.textureMax.y);
        } else {
            mesh.putTextureCoordinateValue(this.textureMin.x);
            mesh.putTextureCoordinateValue(this.textureMin.y);
        }

        mesh.putPositionValue(this.v1.x);
        mesh.putPositionValue(this.v2.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(this.textureMin.x);
            mesh.putTextureCoordinateValue(this.textureMin.y);
        } else {
            mesh.putTextureCoordinateValue(this.textureMin.x);
            mesh.putTextureCoordinateValue(this.textureMax.y);
        }

        mesh.putPositionValue(this.v2.x);
        mesh.putPositionValue(this.v2.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(this.textureMax.x);
            mesh.putTextureCoordinateValue(this.textureMin.y);
        } else {
            mesh.putTextureCoordinateValue(this.textureMax.x);
            mesh.putTextureCoordinateValue(this.textureMax.y);
        }

        mesh.putPositionValue(this.v2.x);
        mesh.putPositionValue(this.v1.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(this.textureMax.x);
            mesh.putTextureCoordinateValue(this.textureMax.y);
        } else {
            mesh.putTextureCoordinateValue(this.textureMax.x);
            mesh.putTextureCoordinateValue(this.textureMin.y);
        }

        mesh.putIndexValue(0);
        mesh.putIndexValue(1);
        mesh.putIndexValue(2);
        mesh.putIndexValue(3);
        mesh.putIndexValue(0);
        mesh.putIndexValue(2);

        mesh.bakeMesh();
        return mesh;
    }
}
