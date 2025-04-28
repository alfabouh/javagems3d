package javagems3d.system.resources.assets.models.helper.forms.D3;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.models.helper.forms.BasicModelCreator;

public class WireBoxModel3D implements BasicModelCreator<Model3D> {
    private final Vector3f min;
    private final Vector3f max;

    public WireBoxModel3D(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Model3D generateModel(@Nullable ArbitraryArguments arguments) {
        return new Model3D(new Pose3D(), new MeshGroup(new MeshNode3D<>(this.generateMesh(arguments))));
    }

    @Override
    public RenderMesh generateMesh(@Nullable ArbitraryArguments arguments) {
        RenderMesh renderMesh = new RenderMesh();

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);

        vaPositions.put(this.min.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.min.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.min.z);

        vaPositions.put(this.min.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.max.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.min.y);
        vaPositions.put(this.max.z);

        vaPositions.put(this.max.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.max.z);

        vaPositions.put(this.min.x);
        vaPositions.put(this.max.y);
        vaPositions.put(this.max.z);

        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(1);

        renderMesh.putVertexIndex(1);
        renderMesh.putVertexIndex(2);

        renderMesh.putVertexIndex(2);
        renderMesh.putVertexIndex(3);

        renderMesh.putVertexIndex(3);
        renderMesh.putVertexIndex(0);

        renderMesh.putVertexIndex(4);
        renderMesh.putVertexIndex(5);

        renderMesh.putVertexIndex(5);
        renderMesh.putVertexIndex(6);

        renderMesh.putVertexIndex(6);
        renderMesh.putVertexIndex(7);

        renderMesh.putVertexIndex(7);
        renderMesh.putVertexIndex(4);

        renderMesh.putVertexIndex(0);
        renderMesh.putVertexIndex(4);

        renderMesh.putVertexIndex(1);
        renderMesh.putVertexIndex(5);

        renderMesh.putVertexIndex(2);
        renderMesh.putVertexIndex(6);

        renderMesh.putVertexIndex(3);
        renderMesh.putVertexIndex(7);

        renderMesh.putVertexAttribute(vaPositions);

        renderMesh.bakeMesh();
        return renderMesh;
    }
}