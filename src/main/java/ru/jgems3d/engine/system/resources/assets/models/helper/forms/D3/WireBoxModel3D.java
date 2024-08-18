package ru.jgems3d.engine.system.resources.assets.models.helper.forms.D3;

import org.joml.Vector3f;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.helper.forms.BasicMesh;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

public class WireBoxModel3D implements BasicMesh<Format3D> {
    private final Vector3f min;
    private final Vector3f max;

    public WireBoxModel3D(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Model<Format3D> generateModel() {
        return new Model<>(new Format3D(), this.generateMesh(), Material.createDefault());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();
        mesh.pushPosition(this.min.x);
        mesh.pushPosition(this.min.y);
        mesh.pushPosition(this.min.z);

        mesh.pushPosition(this.max.x);
        mesh.pushPosition(this.min.y);
        mesh.pushPosition(this.min.z);

        mesh.pushPosition(this.max.x);
        mesh.pushPosition(this.max.y);
        mesh.pushPosition(this.min.z);

        mesh.pushPosition(this.min.x);
        mesh.pushPosition(this.max.y);
        mesh.pushPosition(this.min.z);

        mesh.pushPosition(this.min.x);
        mesh.pushPosition(this.min.y);
        mesh.pushPosition(this.max.z);

        mesh.pushPosition(this.max.x);
        mesh.pushPosition(this.min.y);
        mesh.pushPosition(this.max.z);

        mesh.pushPosition(this.max.x);
        mesh.pushPosition(this.max.y);
        mesh.pushPosition(this.max.z);

        mesh.pushPosition(this.min.x);
        mesh.pushPosition(this.max.y);
        mesh.pushPosition(this.max.z);

        mesh.pushIndex(0);
        mesh.pushIndex(1);

        mesh.pushIndex(1);
        mesh.pushIndex(2);

        mesh.pushIndex(2);
        mesh.pushIndex(3);

        mesh.pushIndex(3);
        mesh.pushIndex(0);

        mesh.pushIndex(4);
        mesh.pushIndex(5);

        mesh.pushIndex(5);
        mesh.pushIndex(6);

        mesh.pushIndex(6);
        mesh.pushIndex(7);

        mesh.pushIndex(7);
        mesh.pushIndex(4);

        mesh.pushIndex(0);
        mesh.pushIndex(4);

        mesh.pushIndex(1);
        mesh.pushIndex(5);

        mesh.pushIndex(2);
        mesh.pushIndex(6);

        mesh.pushIndex(3);
        mesh.pushIndex(7);

        mesh.bakeMesh();
        return mesh;
    }
}