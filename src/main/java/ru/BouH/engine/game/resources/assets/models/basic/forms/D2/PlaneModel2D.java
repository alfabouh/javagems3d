package ru.BouH.engine.game.resources.assets.models.basic.forms.D2;

import org.joml.Vector2d;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;

public class PlaneModel2D implements BasicMesh<Format2D> {
    private final Vector2d v1;
    private final Vector2d v2;
    private final float zLevel;
    private final boolean inverted;

    public PlaneModel2D(Vector2d v1, Vector2d v2, float zLevel) {
        this(false, v1, v2, zLevel);
    }

    public PlaneModel2D(boolean inverted, Vector2d v1, Vector2d v2, float zLevel) {
        this.v1 = v1;
        this.v2 = v2;
        this.zLevel = zLevel;
        this.inverted = inverted;
    }


    @Override
    public Model<Format2D> generateModel() {
        return new Model<>(new Format2D(), this.generateMesh());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();

        mesh.putPositionValue((float) this.v1.x);
        mesh.putPositionValue((float) this.v1.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(0.0f);
            mesh.putTextureCoordinateValue(1.0f);
        } else {
            mesh.putTextureCoordinateValue(0.0f);
            mesh.putTextureCoordinateValue(0.0f);
        }

        mesh.putPositionValue((float) this.v1.x);
        mesh.putPositionValue((float) this.v2.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(0.0f);
            mesh.putTextureCoordinateValue(0.0f);
        } else {
            mesh.putTextureCoordinateValue(0.0f);
            mesh.putTextureCoordinateValue(1.0f);
        }

        mesh.putPositionValue((float) this.v2.x);
        mesh.putPositionValue((float) this.v2.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(1.0f);
            mesh.putTextureCoordinateValue(0.0f);
        } else {
            mesh.putTextureCoordinateValue(1.0f);
            mesh.putTextureCoordinateValue(1.0f);
        }

        mesh.putPositionValue((float) this.v2.x);
        mesh.putPositionValue((float) this.v1.y);
        mesh.putPositionValue(this.zLevel);
        if (this.inverted) {
            mesh.putTextureCoordinateValue(1.0f);
            mesh.putTextureCoordinateValue(1.0f);
        } else {
            mesh.putTextureCoordinateValue(1.0f);
            mesh.putTextureCoordinateValue(0.0f);
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
