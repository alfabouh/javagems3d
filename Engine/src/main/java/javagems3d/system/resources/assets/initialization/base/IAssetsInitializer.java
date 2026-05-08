package javagems3d.system.resources.assets.initialization.base;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.managing.resources.SystemResources;
import org.joml.Vector3f;

public interface IAssetsInitializer {
    void load(SystemResources systemResources);
    LaunchMode loadMode();
    LoadPriority loadPriority();

    float[] ParticleModelPos = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
            -0.5f,  0.5f, 0.0f
    };

    float[] ParticleUV = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    int[] ParticleIndices = {
            0, 1, 2,
            2, 3, 0
    };

    float[] CubeUV = {
            0f,0f, 1f,0f, 1f,1f, 0f,1f,
            0f,0f, 1f,0f, 1f,1f, 0f,1f,
            0f,0f, 1f,0f, 1f,1f, 0f,1f,
            0f,0f, 1f,0f, 1f,1f, 0f,1f,
            0f,0f, 1f,0f, 1f,1f, 0f,1f,
            0f,0f, 1f,0f, 1f,1f, 0f,1f
    };
    float[] CubeModelPos = {
            -0.3f,-0.3f, 0.3f,
            0.3f,-0.3f, 0.3f,
            0.3f, 0.3f, 0.3f,
            -0.3f, 0.3f, 0.3f,

            0.3f,-0.3f,-0.3f,
            -0.3f,-0.3f,-0.3f,
            -0.3f, 0.3f,-0.3f,
            0.3f, 0.3f,-0.3f,

            -0.3f,-0.3f,-0.3f,
            -0.3f,-0.3f, 0.3f,
            -0.3f, 0.3f, 0.3f,
            -0.3f, 0.3f,-0.3f,

            0.3f,-0.3f, 0.3f,
            0.3f,-0.3f,-0.3f,
            0.3f, 0.3f,-0.3f,
            0.3f, 0.3f, 0.3f,

            -0.3f, 0.3f, 0.3f,
            0.3f, 0.3f, 0.3f,
            0.3f, 0.3f,-0.3f,
            -0.3f, 0.3f,-0.3f,

            -0.3f,-0.3f,-0.3f,
            0.3f,-0.3f,-0.3f,
            0.3f,-0.3f, 0.3f,
            -0.3f,-0.3f, 0.3f
    };
    int[] CubeModelInd = {
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            8, 9,10, 10,11, 8,
            12,13,14, 14,15,12,
            16,17,18, 18,19,16,
            20,21,22, 22,23,20
    };
    float[] CubeModelNorm = {
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,

            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,

            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,

            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,

            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,

            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f
    };

    static MeshBuffer createGlobalParticle_MBuffer() {
        DataMesh dataMesh = new DataMesh();
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_POSITIONS, JGemsHelper.Math.convertFloatsList(IAssetsInitializer.ParticleModelPos));
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, JGemsHelper.Math.convertFloatsList(IAssetsInitializer.ParticleUV));
        dataMesh.putVertexIndexes(JGemsHelper.Math.convertIntsList(IAssetsInitializer.ParticleIndices));
        MeshNode3D<DataMesh> meshBufferMeshNode3D = new MeshNode3D<>(dataMesh, new Material());
        MeshBuffer meshBuffer = new MeshBuffer(meshBufferMeshNode3D);
        meshBuffer.setKeepTrianglesInMemory(true);
        meshBuffer.setMeshAABBData(new MeshBoundingBoxData(new CullingAABB(new Vector3f(-0.5f), new Vector3f(0.5f))));
        return meshBuffer;
    }

    static MeshBuffer createDefaultCube_MBuffer() {
        DataMesh dataMesh = new DataMesh();
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_NORMALS, JGemsHelper.Math.convertFloatsList(IAssetsInitializer.CubeModelNorm));
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_POSITIONS, JGemsHelper.Math.convertFloatsList(IAssetsInitializer.CubeModelPos));
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, JGemsHelper.Math.convertFloatsList(IAssetsInitializer.CubeUV));
        dataMesh.putVertexIndexes(JGemsHelper.Math.convertIntsList(IAssetsInitializer.CubeModelInd));
        MeshNode3D<DataMesh> meshBufferMeshNode3D = new MeshNode3D<>(dataMesh, new Material());
        MeshBuffer meshBuffer = new MeshBuffer(meshBufferMeshNode3D);
        meshBuffer.setKeepTrianglesInMemory(true);
        JGemsHelper.JGemsResources.createMeshAABBData(meshBuffer);
        return meshBuffer;
    }

    static MeshGroup createDefaultCube_MGroup() {
        try (RenderMesh renderMesh = new RenderMesh()) {
            renderMesh.putVertexAttribute(new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS).putArray(IAssetsInitializer.CubeModelNorm));
            renderMesh.putVertexAttribute(new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS).putArray(IAssetsInitializer.CubeModelPos));
            renderMesh.putVertexAttribute(new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES).putArray(IAssetsInitializer.CubeUV));
            renderMesh.putVertexIndexes(JGemsHelper.Math.convertIntsList(IAssetsInitializer.CubeModelInd));
            MeshNode3D<RenderMesh> meshBufferMeshNode3D = new MeshNode3D<>(renderMesh, new Material());
            MeshGroup meshGroup = new MeshGroup(meshBufferMeshNode3D);
            JGemsHelper.JGemsResources.createMeshAABBData(meshGroup);
            return meshGroup;
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