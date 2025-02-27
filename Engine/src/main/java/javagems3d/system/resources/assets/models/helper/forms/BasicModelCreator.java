package javagems3d.system.resources.assets.models.helper.forms;

import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.pose.IPose;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;

public interface BasicModelCreator<T extends Model<?, ?>> {
    T generateModel();
    RenderMesh generateMesh();
}
