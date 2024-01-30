package ru.BouH.engine.game.resources.assets.models.basic.forms.D3;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.math.MathHelper;

public class WireBoxModel3D implements BasicMesh<Format3D> {
    private final Vector3d min;
    private final Vector3d max;

    public WireBoxModel3D(btVector3 min, btVector3 max) {
        this(MathHelper.convert(min), MathHelper.convert(max));
    }

    public WireBoxModel3D(Vector3d min, Vector3d max) {
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
        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.max.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.max.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.max.z);

        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.max.z);

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