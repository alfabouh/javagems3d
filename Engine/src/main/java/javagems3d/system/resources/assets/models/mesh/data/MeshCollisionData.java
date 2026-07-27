/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.IntStream;

public class MeshCollisionData {
    //public static final Map<@NotNull MeshStructure3D<?>, MeshCollisionData> GLOBAL_CACHE = new HashMap<>();

    private final List<IndexedMesh> indexedMeshList;
    private final Pair<float[], int[]> dataPair;
    private final Fabric fabric;
    private final MeshStructure3D<?> meshStructure;

    public MeshCollisionData(@NotNull MeshStructure3D<?> meshStructure, @NotNull Fabric fabric) {
        this.indexedMeshList = new ArrayList<>();
        this.dataPair = this.pickData(meshStructure, indexedMeshList);
        this.meshStructure = meshStructure;
        this.fabric = fabric;
    }

    private MeshCollisionData(List<IndexedMesh> indexedMeshList, Pair<float[], int[]> dataPair, Fabric fabric, MeshStructure3D<?> meshStructure) {
        this.indexedMeshList = indexedMeshList;
        this.dataPair = dataPair;
        this.fabric = fabric;
        this.meshStructure = meshStructure;
    }

    public MeshCollisionData copyFor(MeshStructure3D<?> meshStructure, @Nullable Fabric fabric) {
        return new MeshCollisionData(this.indexedMeshList, this.dataPair, fabric == null ? this.fabric : fabric, meshStructure);
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
        return this.fabric.createShapedForAnimatedObject(this.meshStructure, this.dataPair.first(), this.dataPair.second(), this.indexedMeshList);
    }

    public CollisionShape getStaticCollision() {
        return this.fabric.createStaticShape(this.meshStructure, this.dataPair.first(), this.dataPair.second(), this.indexedMeshList);
    }

    public CollisionShape getDynamicCollision() {
        return this.fabric.createDynamicShape(this.meshStructure, this.dataPair.first(), this.dataPair.second(), this.indexedMeshList);
    }

    public static class DefaultFabric implements Fabric {
        private final StaticShape staticShape;
        private final DynamicShape dynamicShape;

        public DefaultFabric() {
            this(StaticShape.Mesh, DynamicShape.MeshHull);
        }

        public DefaultFabric(StaticShape staticShape, DynamicShape dynamicShape) {
            this.staticShape = staticShape;
            this.dynamicShape = dynamicShape;
        }

        @Override
        public CollisionShape createStaticShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList) {
            return switch (this.staticShape) {
                case Mesh -> new MeshCollisionShape(true, indexedMeshList);
            };
        }

        @Override
        public CollisionShape createDynamicShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList) {
            final CullingAABB cullingAABB = meshStructure.getMeshAABBData().getNormalizedAABB(new Pose3D(new Vector3f(0.0f)));
            return switch (this.dynamicShape) {
                case MeshHull -> new HullCollisionShape(positions);
                case Box -> this.createBoxShape(cullingAABB);
                case Capsule -> this.createCapsuleShape(cullingAABB);
                case Sphere -> this.createSphereShape(cullingAABB);
            };
        }

        @Override
        public List<CollisionShape> createShapedForAnimatedObject(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList) {
            List<CollisionShape> collisionShapes = new ArrayList<>();
            for (Animation animation : meshStructure.getAnimationsList()) {
                CullingAABB cullingAABB = meshStructure.getMeshAABBDataForAnimation(animation).getNormalizedAABB(new Pose3D(new Vector3f(0.0f)));
                collisionShapes.add(this.createDynamicShapeForAABB(cullingAABB, positions));
            }
            return collisionShapes;
        }

        protected CollisionShape createDynamicShapeForAABB(CullingAABB cullingAABB, float[] positions) {
            return switch (this.dynamicShape) {
                case MeshHull -> {
                    //if (positions.length / 3 <= 512) {
                        yield new HullCollisionShape(positions);
                    //}
                    //yield this.createBoxShape(cullingAABB);
                }
                case Box -> this.createBoxShape(cullingAABB);
                case Capsule -> this.createCapsuleShape(cullingAABB);
                case Sphere -> this.createSphereShape(cullingAABB);
            };
        }

        protected CollisionShape createBoxShape(CullingAABB cullingAABB) {
            Vector3f min = cullingAABB.getAabbMin();
            Vector3f max = cullingAABB.getAabbMax();
            float xExtent = (max.x - min.x) * 0.5f;
            float yExtent = (max.y - min.y) * 0.5f;
            float zExtent = (max.z - min.z) * 0.5f;
            return new BoxCollisionShape(xExtent, yExtent, zExtent);
        }

        protected CollisionShape createCapsuleShape(CullingAABB cullingAABB) {
            Vector3f min = cullingAABB.getAabbMin();
            Vector3f max = cullingAABB.getAabbMax();
            float width = max.x - min.x;
            float height = max.y - min.y;
            float depth = max.z - min.z;
            float radius = Math.max(width, depth) * 0.5f;
            float capsuleHeight = Math.max(0.0f, height - (radius * 2.0f));
            return new CapsuleCollisionShape(radius, capsuleHeight);
        }

        protected CollisionShape createSphereShape(CullingAABB cullingAABB) {
            Vector3f min = cullingAABB.getAabbMin();
            Vector3f max = cullingAABB.getAabbMax();
            float width = max.x - min.x;
            float height = max.y - min.y;
            float depth = max.z - min.z;
            float radius = Math.max(width, Math.max(height, depth)) * 0.5f;
            return new SphereCollisionShape(radius);
        }

        public enum StaticShape {
            Mesh
        }

        public enum DynamicShape {
            Box,
            MeshHull,
            Capsule,
            Sphere
        }
    }

    public interface Fabric {
        CollisionShape createStaticShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList);
        CollisionShape createDynamicShape(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList);
        List<CollisionShape> createShapedForAnimatedObject(MeshStructure3D<?> meshStructure, float[] positions, int[] indexes, List<IndexedMesh> indexedMeshList);
    }
}