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

package javagems3d.system.resources.assets.models.mesh.udata;

import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import javagems3d.JGemsHelper;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.service.collections.Pair;
import vhacd.VHACD;
import vhacd.VHACDHull;
import vhacd.VHACDParameters;
import vhacd4.Vhacd4;
import vhacd4.Vhacd4Hull;
import vhacd4.Vhacd4Parameters;

import java.util.ArrayList;
import java.util.List;

public class MeshCollisionData implements IMeshUserData {
    private final MeshCollisionShape meshCollisionShape;
    private final MeshCollisionShape optimizedMeshCollisionShape;

    public MeshCollisionData(MeshStructure3D<?> meshStructure) {
        List<IndexedMesh> indexedMeshList = new ArrayList<>();
        Pair<float[], int[]> pair = this.pickData(meshStructure, indexedMeshList);

        //new MeshCollisionShape(true, indexedMeshList)
        // this.optimizedMeshCollisionShape = this.optimizedShape(pair.getFirst(), pair.getSecond());

        this.meshCollisionShape = new MeshCollisionShape(true, indexedMeshList);
        this.optimizedMeshCollisionShape = this.meshCollisionShape;
    }

    private CompoundCollisionShape optimizedShape(float[] meshPositions, int[] meshIndices) {
        VHACDParameters parms = new VHACDParameters();
        List<VHACDHull> vhacdHulls = VHACD.compute(meshPositions, meshIndices, parms);
        CompoundCollisionShape compound2 = new CompoundCollisionShape();
        for (VHACDHull vhacdHull : vhacdHulls) {
            HullCollisionShape hullShape = new HullCollisionShape(vhacdHull);
            compound2.addChildShape(hullShape);
        }
        return compound2;
    }

    private Pair<float[], int[]> pickData(MeshStructure3D<?> meshStructure, List<IndexedMesh> indexedMeshList) {
        int fC = 0;
        int iC = 0;
        for (MeshNode3D<?> meshNode3D : meshStructure.getAllNodes()) {
            fC += meshNode3D.getMeshData().getVertexPositions().size();
            iC += meshNode3D.getMeshData().getVertexIndexes().size();
        }
        float[] floats = new float[fC];
        int[] integers = new int[iC];
        int floatIndex = 0;
        int intIndex = 0;
        for (MeshNode3D<?> meshNode3D : meshStructure.getAllNodes()) {
            List<Float> vertexPositions = meshNode3D.getMeshData().getVertexPositions();
            List<Integer> vertexIndexes = meshNode3D.getMeshData().getVertexIndexes();
            for (Float vertexPosition : vertexPositions) {
                floats[floatIndex++] = vertexPosition;
            }
            for (Integer vertexIndex : vertexIndexes) {
                integers[intIndex++] = vertexIndex;
            }
            indexedMeshList.add(DynamicsUtils.getIndexMesh(JGemsHelper.UTILS.convertFloatsArray(meshNode3D.getMeshData().getVertexPositions()), JGemsHelper.UTILS.convertIntsArray(meshNode3D.getMeshData().getVertexIndexes())));
        }
        return new Pair<>(floats, integers);
    }

    public MeshCollisionShape getMeshCollisionShape() {
        return this.meshCollisionShape;
    }

    public MeshCollisionShape getOptimizedMeshCollisionShape() {
        return this.optimizedMeshCollisionShape;
    }
}
