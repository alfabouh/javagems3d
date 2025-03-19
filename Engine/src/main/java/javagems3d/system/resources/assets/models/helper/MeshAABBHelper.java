package javagems3d.system.resources.assets.models.helper;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public abstract class MeshAABBHelper {
    public static CullingAABB create(MeshStructure3D<? extends IMesh> meshStructure) {
        List<? extends MeshNode3D<?>> list = meshStructure.getAllNodes();
        if (list.isEmpty()) {
            return null;
        }

        Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
        Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);

        for (MeshNode3D<?> meshNode3D : list) {
            IMesh mesh = meshNode3D.getMeshData();
            List<Float> positions = mesh.getVertexPositions();

            for (int i = 0; i < positions.size(); i += 3) {
                Vector3f vertex = new Vector3f(positions.get(i), positions.get(i + 1), positions.get(i + 2));
                min.min(vertex);
                max.max(vertex);
            }
        }

        return new CullingAABB(min, max);
    }

    public static CullingAABB createMultiThread(MeshStructure3D<? extends IMesh> meshStructure, int threads) {
        List<? extends MeshNode3D<?>> list = meshStructure.getAllNodes();
        if (list.isEmpty()) {
            return null;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<CullingAABB>> futures = new ArrayList<>();

        int totalVertices = 0;
        for (MeshNode3D<?> meshNode3D : list) {
            IMesh mesh = meshNode3D.getMeshData();
            totalVertices += mesh.getVertexPositions().size() / 3;
        }

        int effectiveThreads = Math.min(threads, totalVertices);
        int verticesPerThread = totalVertices / effectiveThreads;
        int remainingVertices = totalVertices % effectiveThreads;
        int startVertex = 0;

        for (int i = 0; i < effectiveThreads; i++) {
            final int threadStart = startVertex;
            final int threadEnd = threadStart + verticesPerThread + (i < remainingVertices ? 1 : 0);
            futures.add(executorService.submit(() -> {
                Vector3f localMin = new Vector3f(Float.POSITIVE_INFINITY);
                Vector3f localMax = new Vector3f(Float.NEGATIVE_INFINITY);
                for (MeshNode3D<?> meshNode3D : list) {
                    IMesh mesh = meshNode3D.getMeshData();
                    List<Float> positions = mesh.getVertexPositions();
                    for (int k = threadStart * 3; k < threadEnd * 3 && k + 2 < positions.size(); k += 3) {
                        Vector3f vertex = new Vector3f(positions.get(k), positions.get(k + 1), positions.get(k + 2));
                        localMin.min(vertex);
                        localMax.max(vertex);
                    }
                }
                return new CullingAABB(localMin, localMax);
            }));

            startVertex = threadEnd;
        }

        Vector3f finalMin = new Vector3f(Float.POSITIVE_INFINITY);
        Vector3f finalMax = new Vector3f(Float.NEGATIVE_INFINITY);
        try {
            for (Future<CullingAABB> future : futures) {
                CullingAABB localAABB = future.get();
                if (localAABB != null) {
                    finalMin.min(localAABB.getAabbMin());
                    finalMax.max(localAABB.getAabbMax());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new JGemsRuntimeException(e);
        }
        executorService.shutdown();

        return new CullingAABB(finalMin, finalMax);
    }

    public static Map<Animation, CullingAABB> createAnimatedMultiThread(MeshStructure3D<? extends IMesh> meshStructure, int innerThreads) {
        List<? extends MeshNode3D<?>> list = meshStructure.getAllNodes();
        List<Animation> animationsList = meshStructure.getAnimationsList();
        if (list.isEmpty()) {
            return null;
        }
        if (animationsList == null || animationsList.isEmpty()) {
            return null;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(animationsList.size());
        Map<Animation, Future<CullingAABB>> futureMap = new HashMap<>();

        for (Animation animation : meshStructure.getAnimationsList()) {
            futureMap.put(animation, executorService.submit(() -> {
                ForkJoinPool forkJoinPool = new ForkJoinPool(innerThreads);
                List<Future<CullingAABB>> innerFutures = new ArrayList<>();

                int totalMeshes = list.size();
                int effectiveThreads = Math.min(innerThreads, totalMeshes);
                int totalInGroup = totalMeshes / effectiveThreads;
                int restInGroup = totalMeshes % effectiveThreads;

                int startPoint = 0;
                for (int part = 0; part < effectiveThreads; part++) {
                    final int startIndex = startPoint;
                    final int endIndex = startIndex + totalInGroup + (part < restInGroup ? 1 : 0);

                    innerFutures.add(forkJoinPool.submit(() -> {
                        Vector3f localMin = new Vector3f(Float.POSITIVE_INFINITY);
                        Vector3f localMax = new Vector3f(Float.NEGATIVE_INFINITY);

                        for (int meshIndex = startIndex; meshIndex < endIndex; meshIndex++) {
                            MeshNode3D<?> meshNode3D = list.get(meshIndex);
                            IMesh mesh = meshNode3D.getMeshData();
                            List<Float> positions = mesh.getVertexPositions();
                            for (int k = 0; k < positions.size(); k += 3) {
                                Vector3f tempVertex = new Vector3f(positions.get(k), positions.get(k + 1), positions.get(k + 2));
                                for (AnimationFrame animationFrame : animation.getFrameList()) {
                                    Vector3f positionStart = new Vector3f(0.0f);
                                    for (int i = 0; i < JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS; i++) {
                                        SkeletonData skeletonData = mesh.getSkeletonData();
                                        if (skeletonData == null) {
                                            throw new JGemsRuntimeException("Couldn't calculate animated model's AABB! It's skeleton is NULL!");
                                        }
                                        int vertexIndex = k / 3;
                                        float weight = skeletonData.getWeights().get(vertexIndex * JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS + i);
                                        if (weight > 0.f) {
                                            int boneId = skeletonData.getBoneIds().get(vertexIndex * JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS + i);
                                            Matrix4f boneMatrix = animationFrame.getBoneMatrices()[boneId];
                                            Vector3f newPosition = new Vector3f(tempVertex).mulPosition(boneMatrix);
                                            newPosition.mul(weight);
                                            positionStart.add(newPosition);
                                        }
                                    }
                                    localMin = localMin.min(positionStart);
                                    localMax = localMax.max(positionStart);
                                }
                            }
                        }
                        return new CullingAABB(localMin, localMax);
                    }));
                    startPoint = endIndex;
                }

                Vector3f finalMin = new Vector3f(Float.POSITIVE_INFINITY);
                Vector3f finalMax = new Vector3f(Float.NEGATIVE_INFINITY);
                for (Future<CullingAABB> innerFuture : innerFutures) {
                    CullingAABB localAABB = innerFuture.get();
                    if (localAABB != null) {
                        finalMin.min(localAABB.getAabbMin());
                        finalMax.max(localAABB.getAabbMax());
                    }
                }
                forkJoinPool.shutdown();
                return new CullingAABB(finalMin, finalMax);
            }));
        }

        Map<Animation, CullingAABB> result = new HashMap<>();
        for (Map.Entry<Animation, Future<CullingAABB>> entry : futureMap.entrySet()) {
            try {
                result.put(entry.getKey(), entry.getValue().get());
            } catch (InterruptedException | ExecutionException e) {
                throw new JGemsRuntimeException(e);
            }
        }

        executorService.shutdown();
        return result;
    }
}
