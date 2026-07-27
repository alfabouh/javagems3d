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

        int totalMeshes = list.size();
        int effectiveThreads = Math.min(threads, totalMeshes);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        List<Future<CullingAABB>> futures = new ArrayList<>();
        int meshesPerThread = totalMeshes / effectiveThreads;
        int restMeshes = totalMeshes % effectiveThreads;

        for (int i = 0; i < effectiveThreads; i++) {
            int start = i * (meshesPerThread);
            int end = i * (meshesPerThread) + meshesPerThread + ((i == effectiveThreads - 1) ? restMeshes : 0);

            futures.add(executorService.submit(() -> {
                Vector3f localMin = new Vector3f(Float.POSITIVE_INFINITY);
                Vector3f localMax = new Vector3f(Float.NEGATIVE_INFINITY);
                for (int k = start; k < end; k++) {
                    MeshNode3D<?> meshNode3D = list.get(k);
                    IMesh mesh = meshNode3D.getMeshData();
                    List<Float> positions = mesh.getVertexPositions();
                    for (int j = 0; j < positions.size(); j += 3) {
                        Vector3f vertex = new Vector3f(positions.get(j), positions.get(j + 1), positions.get(j + 2));
                        localMin.min(vertex);
                        localMax.max(vertex);
                    }
                }
                return new CullingAABB(localMin, localMax);
            }));
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
        } finally {
            executorService.shutdown();
        }

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
                List<Future<CullingAABB>> innerFutures = new ArrayList<>();
                int totalMeshes = list.size();
                int effectiveThreads = Math.min(innerThreads, totalMeshes);
                ForkJoinPool forkJoinPool = new ForkJoinPool(innerThreads);

                int totalInGroup = totalMeshes / effectiveThreads;
                int restInGroup = totalMeshes % effectiveThreads;

                try {
                    for (int part = 0; part < effectiveThreads; part++) {
                        int start = part * (totalInGroup);
                        int end = part * (totalInGroup) + totalInGroup + ((part == effectiveThreads - 1) ? restInGroup : 0);

                        innerFutures.add(forkJoinPool.submit(() -> {
                            Vector3f localMin = new Vector3f(Float.POSITIVE_INFINITY);
                            Vector3f localMax = new Vector3f(Float.NEGATIVE_INFINITY);

                            for (int meshIndex = start; meshIndex < end; meshIndex++) {
                                MeshNode3D<?> meshNode3D = list.get(meshIndex);
                                IMesh mesh = meshNode3D.getMeshData();
                                List<Float> positions = mesh.getVertexPositions();
                                for (int k = 0; k < positions.size(); k += 3) {
                                    Vector3f tempVertex = new Vector3f(positions.get(k), positions.get(k + 1), positions.get(k + 2));
                                    Vector3f positionStart = new Vector3f(0.0f);
                                    SkeletonData skeletonData = mesh.getSkeletonData();
                                    if (skeletonData == null) {
                                        localMin.min(tempVertex);
                                        localMax.max(tempVertex);
                                        continue;
                                    }
                                    for (AnimationFrame animationFrame : animation.frameList()) {
                                        positionStart.set(0.0f);
                                        for (int i = 0; i < JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS; i++) {
                                            int vertexIndex = k / 3;
                                            float weight = skeletonData.weights().get(vertexIndex * JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS + i);
                                            if (weight > 0.f) {
                                                int boneId = skeletonData.boneIds().get(vertexIndex * JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS + i);
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
                    return new CullingAABB(finalMin, finalMax);
                } finally {
                    forkJoinPool.shutdown();
                }
            }));
        }

        Map<Animation, CullingAABB> result = new HashMap<>();
        try {
            for (Map.Entry<Animation, Future<CullingAABB>> entry : futureMap.entrySet()) {
                result.put(entry.getKey(), entry.getValue().get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new JGemsRuntimeException(e);
        } finally {
            executorService.shutdown();
        }

        return result;
    }
}
