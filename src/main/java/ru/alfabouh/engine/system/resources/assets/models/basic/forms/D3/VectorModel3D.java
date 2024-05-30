package ru.alfabouh.engine.system.resources.assets.models.basic.forms.D3;

import org.joml.Vector3f;
import ru.alfabouh.engine.system.resources.assets.materials.Material;
import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.basic.forms.BasicMesh;
import ru.alfabouh.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.system.resources.assets.models.mesh.Mesh;

public class VectorModel3D implements BasicMesh<Format3D> {
    private final Vector3f v1;
    private final Vector3f v2;

    public VectorModel3D(Vector3f v1, Vector3f v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Model<Format3D> generateModel() {
        return new Model<>(new Format3D(), this.generateMesh(), Material.createDefault());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();
        mesh.putPositionValue(this.v1.x);
        mesh.putPositionValue(this.v1.y);
        mesh.putPositionValue(this.v1.z);

        mesh.putPositionValue(this.v2.x);
        mesh.putPositionValue(this.v2.y);
        mesh.putPositionValue(this.v2.z);

        mesh.putIndexValue(0);
        mesh.putIndexValue(1);

        mesh.bakeMesh();
        return mesh;
    }
}
