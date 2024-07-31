package ru.jgems3d.engine.system.resources.assets.models.basic.forms.D2;

import org.joml.Vector2f;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.forms.BasicMesh;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

public class VectorModel2D implements BasicMesh<Format2D> {
    private final Vector2f v1;
    private final Vector2f v2;

    public VectorModel2D(Vector2f v1, Vector2f v2) {
        this.v1 = v1;
        this.v2 = v2;
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

        mesh.pushPosition(this.v2.x);
        mesh.pushPosition(this.v2.y);

        mesh.pushIndex(0);
        mesh.pushIndex(1);

        mesh.bakeMesh();
        return mesh;
    }
}
