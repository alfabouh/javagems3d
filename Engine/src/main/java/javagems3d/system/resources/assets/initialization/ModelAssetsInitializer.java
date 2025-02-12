/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelMeshLoader;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.service.path.JGemsPath;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public static final float[] CubeModelPos = {
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f
    };
    public static final int[] CubeModelInd = new int[]{
            0, 1, 3, 3, 1, 2,
            4, 0, 3, 5, 4, 3,
            3, 2, 7, 5, 3, 7,
            6, 1, 0, 6, 0, 4,
            2, 1, 6, 2, 6, 7,
            7, 6, 4, 7, 4, 5
    };

    public MeshGroup defaultCube_gr;
    public MeshBuffer defaultCube_bff;

    public MeshGroup grassCube;
    public MeshBuffer ground2;
    public MeshBuffer ground3;
    public MeshBuffer test_anim;

    @Override
    public void load(GameResources gameResources) {
        this.createDefaults(gameResources);

        this.grassCube =gameResources.createMeshGroup(new JGemsPath(JGems3D.PATHS.MODELS, "test_anim/boblampclean.md5mesh"), ModelMeshLoader.FLAGS.DEFAULT | ModelMeshLoader.FLAGS.LOAD_ANIMATIONS);// gameResources.createMeshBuffer(new JGemsPath(JGems3D.PATHS.MODELS, "cube/cube.obj"), ModelMeshLoader.FLAGS.LOAD_IN_INDIRECT_BUFFER);
        this.ground2 = gameResources.createMeshBuffer(new JGemsPath(JGems3D.PATHS.MODELS, "map04/map04.obj"), ModelMeshLoader.FLAGS.DEFAULT);
        this.ground3 = gameResources.createMeshBuffer(new JGemsPath(JGems3D.PATHS.MODELS, "map05/map05.obj"), ModelMeshLoader.FLAGS.DEFAULT);
        this.test_anim = gameResources.createMeshBuffer(new JGemsPath(JGems3D.PATHS.MODELS, "cube/cube.obj"), ModelMeshLoader.FLAGS.DEFAULT);//gameResources.createMeshBuffer(new JGemsPath(JGems3D.PATHS.MODELS, "test_anim/boblamp.md5mesh"), ModelMeshLoader.FLAGS.DEFAULT);
    }

    private void createDefaults(GameResources gameResources) {
        this.defaultCube_bff = this.createDefaultCubeBuffer();
        gameResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_BFF", this.defaultCube_bff);
        gameResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(this.defaultCube_bff);

        this.defaultCube_gr = this.createDefaultCubeGroup();
        gameResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_GR", this.defaultCube_gr);
    }

    private MeshBuffer createDefaultCubeBuffer() {
        DataMesh dataMesh = new DataMesh();
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_POSITIONS, JGemsHelper.UTILS.convertFloatsList(ModelAssetsInitializer.CubeModelPos));
        dataMesh.putVertexIndexes(JGemsHelper.UTILS.convertIntsList(ModelAssetsInitializer.CubeModelInd));
        MeshNode3D<DataMesh> meshBufferMeshNode3D = new MeshNode3D<>(dataMesh, null);
        return new MeshBuffer(meshBufferMeshNode3D);
    }

    private MeshGroup createDefaultCubeGroup() {
        try (RenderMesh renderMesh = new RenderMesh()) {
            renderMesh.putVertexAttribute(new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS).putArray(ModelAssetsInitializer.CubeModelPos));
            renderMesh.putVertexIndexes(JGemsHelper.UTILS.convertIntsList(ModelAssetsInitializer.CubeModelInd));
            MeshNode3D<RenderMesh> meshBufferMeshNode3D = new MeshNode3D<>(renderMesh, null);
            return new MeshGroup(meshBufferMeshNode3D);
        }
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.NORMAL;
    }
}
