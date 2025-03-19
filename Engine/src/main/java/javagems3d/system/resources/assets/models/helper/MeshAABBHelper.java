package javagems3d.system.resources.assets.models.helper;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        int verticesPerThread = totalVertices / threads;
        int remainingVertices = totalVertices % threads;
        int startVertex = 0;

        for (int i = 0; i < threads; i++) {
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

    public static Map<Animation, CullingAABB> createAnimatedMultiThread(MeshStructure3D<? extends IMesh> meshStructure) {
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
                Vector3f localMin = new Vector3f(Float.POSITIVE_INFINITY);
                Vector3f localMax = new Vector3f(Float.NEGATIVE_INFINITY);
                for (MeshNode3D<?> meshNode3D : list) {
                    IMesh mesh = meshNode3D.getMeshData();
                    List<Float> positions = mesh.getVertexPositions();
                    for (AnimationFrame animationFrame : animation.getFrameList()) {
                        for (Matrix4f boneMatrix : animationFrame.getBoneMatrices()) {
                            for (int k = 0; k < positions.size(); k += 3) {
                                Vector3f vertex = new Vector3f(positions.get(k), positions.get(k + 1), positions.get(k + 2));
                                Vector3f transformedVertex = new Vector3f(vertex).mulPosition(boneMatrix);
                                localMin.min(transformedVertex);
                                localMax.max(transformedVertex);
                            }
                        }
                    }
                }
                return new CullingAABB(localMin, localMax);
            }));
        }

        Map<Animation, CullingAABB> result = new HashMap<>();
        try {
            for (Map.Entry<Animation, Future<CullingAABB>> entry : futureMap.entrySet()) {
                Animation animation = entry.getKey();
                CullingAABB localAABB = entry.getValue().get();
                if (localAABB != null) {
                    result.put(animation, localAABB);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new JGemsRuntimeException(e);
        }
        executorService.shutdown();
        return result;
    }
}
