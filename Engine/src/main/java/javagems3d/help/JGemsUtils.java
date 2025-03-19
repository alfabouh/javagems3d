package javagems3d.help;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.helper.MeshAABBHelper;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.udata.MeshAABBData;
import javagems3d.system.resources.assets.models.mesh.udata.MeshCollisionData;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class JGemsUtils {
    public static String getTextWithLines(String text) {
        String[] lines = text.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            stringBuilder.append("/* (").append(i + 1).append(") */ ").append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    public static List<Integer> convertIntsList(int[] arr) {
        List<Integer> list = new ArrayList<>(arr.length);
        for (int f : arr) {
            list.add(f);
        }
        return list;
    }

    public static List<Float> convertFloatsList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) {
            list.add(f);
        }
        return list;
    }

    public static int[] convertIntsArray(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().mapToInt( v -> (Integer) v).toArray();
    }

    public static double[] convertDoublesArray(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().mapToDouble( v -> (Double) v).toArray();
    }

    public static float[] convertFloatsArray(List<Float> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        float[] a = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            a[i] = list.get(i);
        }
        return a;
    }

    public static float[] convertFloats3Array(List<Vector3f> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        float[] a = new float[list.size() * 3];
        for (int i = 0; i < list.size(); i += 3) {
            a[i] = list.get(i).x;
            a[i + 1] = list.get(i).y;
            a[i + 2] = list.get(i).z;
        }
        return a;
    }

    public static Vector3f calcLookVector(Vector3f rotations) {
        float x = rotations.x;
        float y = rotations.y;
        float lX = Math.sin(y) * Math.cos(x);
        float lY = -Math.sin(x);
        float lZ = -Math.cos(y) * Math.cos(x);
        return new Vector3f(lX, lY, lZ);
    }

    @SuppressWarnings("all")
    public static boolean createMeshAABBData(MeshStructure3D<?> meshStructure) {
        if (meshStructure != null) {
            meshStructure.setMeshAABBData(new MeshAABBData(MeshAABBHelper.createMultiThread(meshStructure, 4)));
            if (meshStructure.isAnimatedStructure()) {
                for (Map.Entry<Animation, CullingAABB> aabbEntry : MeshAABBHelper.createAnimatedMultiThread(meshStructure).entrySet()) {
                    meshStructure.setMeshAABBDataForAnimationFrame(aabbEntry.getKey(), new MeshAABBData(aabbEntry.getValue()));
                }
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("all")
    public static boolean createMeshCollisionData(MeshStructure3D<?> meshStructure) {
        if (meshStructure != null && meshStructure.getMeshUserData(MeshStructure3D.MESH_COLLISION_UD) == null) {
            meshStructure.setMeshUserData(MeshStructure3D.MESH_COLLISION_UD, new MeshCollisionData(meshStructure));
            return true;
        }
        return false;
    }

    public static List<Vector3f> getVertexPositionsFromMesh(IMesh mesh) {
        List<Integer> integers = mesh.getVertexIndexes();
        List<Float> floats = mesh.getVertexPositions();
        List<Vector3f> vertexes = new ArrayList<>();

        for (int i = 0; i < integers.size(); i++) {
            int i1 = mesh.getVertexIndexes().get(i) * 3;
            Vector4f v4 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f);
            vertexes.add(new Vector3f(v4.x, v4.y, v4.z));
        }

        return vertexes;
    }

    public static List<Vector3f> getVertexPositionsFromMesh(IMesh mesh, Pose3D pose) {
        List<Integer> integers = mesh.getVertexIndexes();
        List<Float> floats = mesh.getVertexPositions();
        List<Vector3f> vertexes = new ArrayList<>();
        Matrix4f modelMat = TransformUtils.getModelMatrix(pose);

        for (int i = 0; i < integers.size(); i++) {
            int i1 = mesh.getVertexIndexes().get(i) * 3;
            Vector4f v4 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f).mul(modelMat);
            vertexes.add(new Vector3f(v4.x, v4.y, v4.z));
        }

        return vertexes;
    }

    public static <K, V, U> void putObjectInMapOrUpdate(Map<K, V> map, K key, V defaultValue, BiFunction<V, U, V> updateFunction, U updateValue) {
        map.merge(key, defaultValue, (existingValue, newValue) -> updateFunction.apply(existingValue, updateValue));
    }
}
