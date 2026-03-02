package javagems3d.system.resources.assets.models.mesh.data;

import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.synchronizing.SyncManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.*;
import java.util.stream.IntStream;

public class MeshCollisionData {
    public static final Map<@NotNull MeshStructure3D<?>, MeshCollisionData> GLOBAL_CACHE = new HashMap<>();

    private final CollisionShape staticCollision;
    private final CollisionShape dynamicCollision;
    private final List<CollisionShape> animationAABBShapes;

    public MeshCollisionData(@NotNull MeshStructure3D<?> meshStructure, @NotNull Fabric fabric) {
        List<IndexedMesh> indexedMeshList = new ArrayList<>();
        Pair<float[], int[]> pair = this.pickData(meshStructure, indexedMeshList);

        this.animationAABBShapes = fabric.createShapedForAnimatedObject(meshStructure, pair.getFirst(), pair.getSecond(), indexedMeshList);
        this.staticCollision = fabric.createStaticShape(meshStructure, pair.getFirst(), pair.getSecond(), indexedMeshList);
        this.dynamicCollision = fabric.createDynamicShape(meshStructure, pair.getFirst(), pair.getSecond(), indexedMeshList);
    }

    /*
    private Pair<float[], int[]> pickData(MeshStructure3D<?> meshStructure, List<IndexedMesh> indexedMeshList) {
        int fC = 0;
        int iC = 0;
        for (MeshNode3D<?> meshNode3D : meshStructure.getAllNodes()) {
            fC += meshNode3D.getMeshData().getVertexPositions().size();
            iC += meshNode3D.getMeshData().getVertexIndexes().size();
        }
        float[] floats = new float[fC];
        int[] integers = new int[iC];
        int oldFloatIndex = 0;
        int oldIntIndex = 0;
        int floatIndex = 0;
        int intIndex = 0;

        for (MeshNode3D<?> meshNode3D : meshStructure.getAllNodes()) {
            final List<Float> vertexPositions = meshNode3D.getMeshData().getVertexPositions();
            final List<Integer> vertexIndexes = meshNode3D.getMeshData().getVertexIndexes();

            float[] floatsLocal = new float[vertexPositions.size()];
            int[] integersLocal = new int[vertexIndexes.size()];

            for (Float vertexPosition : vertexPositions) {
                floats[floatIndex++] = vertexPosition;
            }
            for (Integer vertexIndex : vertexIndexes) {
                integers[intIndex++] = vertexIndex;
            }
            System.arraycopy(floats, oldFloatIndex, floatsLocal, 0, floatsLocal.length);
            System.arraycopy(integers, oldIntIndex, integersLocal, 0, integersLocal.length);
            indexedMeshList.add(DynamicsUtils.getIndexMesh(floatsLocal, integersLocal));
            oldFloatIndex = floatIndex;
            oldIntIndex = intIndex;
        }
        return new Pair<>(floats, integers);
    }
*/

    private Pair<float[], int[]> pickData(MeshStructure3D<?> meshStructure, List<IndexedMesh> indexedMeshList) {
        List<? extends MeshNode3D<?>> nodes = meshStructure.getAllNodes();
        int nodeCount = nodes.size();

        int[] floatOffsets = new int[nodeCount];
        int[] intOffsets = new int[nodeCount];

        int totalFloats = 0;
        int totalInts = 0;

        for (int i = 0; i < nodeCount; i++) {
            floatOffsets[i] = totalFloats;
            intOffsets[i] = totalInts;

            totalFloats += nodes.get(i).getMeshData().getVertexPositions().size();
            totalInts += nodes.get(i).getMeshData().getVertexIndexes().size();
        }

        float[] floats = new float[totalFloats];
        int[] integers = new int[totalInts];

        IntStream.range(0, nodeCount).parallel().forEach(i -> {
            MeshNode3D<?> node = nodes.get(i);

            List<Float> vertexPositions = node.getMeshData().getVertexPositions();
            List<Integer> vertexIndexes = node.getMeshData().getVertexIndexes();

            int floatOffset = floatOffsets[i];
            int intOffset = intOffsets[i];

            for (int j = 0; j < vertexPositions.size(); j++) {
                floats[floatOffset + j] = vertexPositions.get(j);
            }

            for (int j = 0; j < vertexIndexes.size(); j++) {
                integers[intOffset + j] = vertexIndexes.get(j);
            }

            float[] floatsLocal = new float[vertexPositions.size()];
            int[] intsLocal = new int[vertexIndexes.size()];

            for (int j = 0; j < floatsLocal.length; j++) {
                floatsLocal[j] = vertexPositions.get(j);
            }

            for (int j = 0; j < intsLocal.length; j++) {
                intsLocal[j] = vertexIndexes.get(j);
            }

            IndexedMesh mesh = DynamicsUtils.getIndexMesh(floatsLocal, intsLocal);
            synchronized (indexedMeshList) {
                indexedMeshList.add(mesh);
            }
        });

        return new Pair<>(floats, integers);
    }

    //private CompoundCollisionShape optimizedShape(float[] meshPositions, int[] meshIndices) {
    //    Vhacd4Parameters parms = new Vhacd4Parameters();
    //    List<Vhacd4Hull> vhacdHulls = Vhacd4.compute(meshPositions, meshIndices, parms);
    //    CompoundCollisionShape compound2 = new CompoundCollisionShape();
    //    for (Vhacd4Hull vhacdHull : vhacdHulls) {
    //        HullCollisionShape hullShape = new HullCollisionShape(vhacdHull);
    //        compound2.addChildShape(hullShape);
    //    }
    //    return compound2;
    //}

    public List<CollisionShape> getAnimationAABBShapes() {
        return this.animationAABBShapes;
    }

    public CollisionShape getStaticCollision() {
        return this.staticCollision;
    }

    public CollisionShape getDynamicCollision() {
        return this.dynamicCollision;
    }

    public static class DefaultFabric implements Fabric {
        @Override
        public CollisionShape createStaticShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList) {
            return new MeshCollisionShape(true, indexedMeshList);
        }

        @Override
        public CollisionShape createDynamicShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList) {
            if (positions.length / 3 <= 128) {
                return new HullCollisionShape(positions);
            } else {
                final CullingAABB cullingAABB = meshStructure.getMeshAABBData().getNormalizedAABB(new Pose3D(new Vector3f(0.0f)));
                return this.createCompoundShape(cullingAABB);
            }
        }

        @Override
        public List<CollisionShape> createShapedForAnimatedObject(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList) {
            List<CollisionShape> collisionShapes = new ArrayList<>();
            for (Animation animation : meshStructure.getAnimationsList()) {
                CullingAABB cullingAABB = meshStructure.getMeshAABBDataForAnimation(animation).getNormalizedAABB(new Pose3D(new Vector3f(0.0f)));
                collisionShapes.add(this.createCompoundShape(cullingAABB));
            }
            return collisionShapes;
        }

        protected CompoundCollisionShape createCompoundShape(CullingAABB cullingAABB) {
            float xExtent = cullingAABB.getAabbMax().x - cullingAABB.getAabbMin().x;
            float yExtent = cullingAABB.getAabbMax().y - cullingAABB.getAabbMin().y;
            float zExtent = cullingAABB.getAabbMax().z - cullingAABB.getAabbMin().z;

            final CompoundCollisionShape compoundCollisionShape = new CompoundCollisionShape();
            CollisionShape collisionShape = new BoxCollisionShape(2f);
            compoundCollisionShape.addChildShape(collisionShape, new com.jme3.math.Vector3f(0.0f, yExtent / 2.0f, 0.0f));
            return compoundCollisionShape;
        }
    }

    public interface Fabric {
        CollisionShape createStaticShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList);
        CollisionShape createDynamicShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList);
        List<CollisionShape> createShapedForAnimatedObject(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList);
    }
}