package ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.forms.D3;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.forms.BasicMesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

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
        mesh.putPositionValue(this.min.x);
        mesh.putPositionValue(this.min.y);
        mesh.putPositionValue(this.min.z);

        mesh.putPositionValue(this.max.x);
        mesh.putPositionValue(this.min.y);
        mesh.putPositionValue(this.min.z);

        mesh.putPositionValue(this.max.x);
        mesh.putPositionValue(this.max.y);
        mesh.putPositionValue(this.min.z);

        mesh.putPositionValue(this.min.x);
        mesh.putPositionValue(this.max.y);
        mesh.putPositionValue(this.min.z);

        mesh.putPositionValue(this.min.x);
        mesh.putPositionValue(this.min.y);
        mesh.putPositionValue(this.max.z);

        mesh.putPositionValue(this.max.x);
        mesh.putPositionValue(this.min.y);
        mesh.putPositionValue(this.max.z);

        mesh.putPositionValue(this.max.x);
        mesh.putPositionValue(this.max.y);
        mesh.putPositionValue(this.max.z);

        mesh.putPositionValue(this.min.x);
        mesh.putPositionValue(this.max.y);
        mesh.putPositionValue(this.max.z);

        mesh.putIndexValue(0);
        mesh.putIndexValue(1);

        mesh.putIndexValue(1);
        mesh.putIndexValue(2);

        mesh.putIndexValue(2);
        mesh.putIndexValue(3);

        mesh.putIndexValue(3);
        mesh.putIndexValue(0);

        mesh.putIndexValue(4);
        mesh.putIndexValue(5);

        mesh.putIndexValue(5);
        mesh.putIndexValue(6);

        mesh.putIndexValue(6);
        mesh.putIndexValue(7);

        mesh.putIndexValue(7);
        mesh.putIndexValue(4);

        mesh.putIndexValue(0);
        mesh.putIndexValue(4);

        mesh.putIndexValue(1);
        mesh.putIndexValue(5);

        mesh.putIndexValue(2);
        mesh.putIndexValue(6);

        mesh.putIndexValue(3);
        mesh.putIndexValue(7);

        mesh.bakeMesh();
        return mesh;
    }
}