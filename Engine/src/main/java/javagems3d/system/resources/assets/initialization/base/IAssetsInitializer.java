package javagems3d.system.resources.assets.initialization.base;

import javagems3d.help.JGemsUtils;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.managing.resources.SystemResources;

public interface IAssetsInitializer {
    void load(SystemResources systemResources);
    LaunchMode loadMode();
    LoadPriority loadPriority();

    float[] CubeModelPos = {
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f
    };
    int[] CubeModelInd = new int[]{
            0, 1, 3, 3, 1, 2,
            4, 0, 3, 5, 4, 3,
            3, 2, 7, 5, 3, 7,
            6, 1, 0, 6, 0, 4,
            2, 1, 6, 2, 6, 7,
            7, 6, 4, 7, 4, 5
    };
    float[] CubeModelNorm = new float[]{
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f
    };

    static MeshBuffer createDefaultCube_MBuffer() {
        DataMesh dataMesh = new DataMesh();
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_NORMALS, JGemsUtils.convertFloatsList(IAssetsInitializer.CubeModelNorm));
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_POSITIONS, JGemsUtils.convertFloatsList(IAssetsInitializer.CubeModelPos));
        dataMesh.putVertexIndexes(JGemsUtils.convertIntsList(IAssetsInitializer.CubeModelInd));
        MeshNode3D<DataMesh> meshBufferMeshNode3D = new MeshNode3D<>(dataMesh, new Material(new Color4Texture(1.0f, 1.0f, 1.0f)));
        MeshBuffer meshBuffer = new MeshBuffer(meshBufferMeshNode3D);
        meshBuffer.setKeepNodesInMemory(true);
        return meshBuffer;
    }

    static MeshGroup createDefaultCube_MGroup() {
        try (RenderMesh renderMesh = new RenderMesh()) {
            renderMesh.putVertexAttribute(new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS).putArray(IAssetsInitializer.CubeModelNorm));
            renderMesh.putVertexAttribute(new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS).putArray(IAssetsInitializer.CubeModelPos));
            renderMesh.putVertexIndexes(JGemsUtils.convertIntsList(IAssetsInitializer.CubeModelInd));
            MeshNode3D<RenderMesh> meshBufferMeshNode3D = new MeshNode3D<>(renderMesh, new Material(new Color4Texture(1.0f, 1.0f, 1.0f)));
            return new MeshGroup(meshBufferMeshNode3D);
        }
    }

    enum LaunchMode {
        ASYNC,
        REGULAR
    }

    enum LoadPriority {
        LOW(2),
        NORMAL(1),
        HIGH(0);

        public final int priority;

        LoadPriority(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}