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

package javagems3d.physics.world.thread.dynamics;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.util.BufferUtils;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.DefaultPointers;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * It is a utilitarian class that has functions for managing the state of physical Bullet entities.
 */
public abstract class DynamicsUtils {
    public static Vector3f lerp(Vector3f a, Vector3f b, float t) {
        return new Vector3f(a).mul(1.0f - t).add(new Vector3f(b).mul(t));
    }

    public static com.jme3.math.Vector3f lerp(com.jme3.math.Vector3f a, com.jme3.math.Vector3f b, float t) {
        return new com.jme3.math.Vector3f(a).mult(1.0f - t).add(new com.jme3.math.Vector3f(b).mult(t));
    }

    public static com.jme3.math.Vector3f createV3F_JME(float x, float y, float z) {
        return new com.jme3.math.Vector3f(x, y, z);
    }

    public static com.jme3.math.Vector3f convertV3F_JME(Vector3f vector3f) {
        return new com.jme3.math.Vector3f(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f convertV3F_JOML(com.jme3.math.Vector3f vector3f) {
        return new Vector3f(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Transform createTransform(Vector3f pos, Vector3f rot, Vector3f scaling) {
        Transform transform = new Transform();
        transform.setScale(DynamicsUtils.convertV3F_JME(scaling));
        transform.setTranslation(DynamicsUtils.convertV3F_JME(pos));
        transform.setRotation(new Quaternion().fromAngles(rot.x, rot.y, rot.z));
        return transform;
    }

    public static Transform createTransform(Vector3f pos, Vector3f rot) {
        return DynamicsUtils.createTransform(pos, rot, new Vector3f(1.0f));
    }

    public static Transform createTransform(Vector3f pos) {
        return DynamicsUtils.createTransform(pos, new Vector3f(0.0f), new Vector3f(1.0f));
    }

    public static void transformRigidBody(PhysicsRigidBody physicsRigidBody, Vector3f pos, Vector3f rot, Vector3f scaling) {
        physicsRigidBody.setPhysicsTransform(DynamicsUtils.createTransform(pos, rot, scaling));
    }

    public static void translateRigidBody(PhysicsRigidBody physicsRigidBody, Vector3f pos) {
        physicsRigidBody.setPhysicsLocation(DynamicsUtils.convertV3F_JME(pos));
    }

    public static void rotateRigidBody(PhysicsRigidBody physicsRigidBody, Vector3f rot) {
        physicsRigidBody.setPhysicsRotation(new Quaternion().fromAngles(rot.x, rot.y, rot.z));
    }

    public static void scaleRigidBody(PhysicsRigidBody physicsRigidBody, Vector3f scale) {
        physicsRigidBody.setPhysicsScale(DynamicsUtils.convertV3F_JME(scale));
    }

    public static void translateGhost(PhysicsGhostObject physicsGhostObject, Vector3f pos) {
        physicsGhostObject.setPhysicsLocation(DynamicsUtils.convertV3F_JME(pos));
    }

    public static void rotateGhost(PhysicsGhostObject physicsGhostObject, Vector3f rot) {
        physicsGhostObject.setPhysicsRotation(new Quaternion().fromAngles(rot.x, rot.y, rot.z));
    }

    public static Vector3f getObjectBodyPos(PhysicsCollisionObject physicsRigidBody) {
        Transform transform = new Transform();
        physicsRigidBody.getTransform(transform);
        return new Vector3f(DynamicsUtils.convertV3F_JOML(transform.getTranslation()));
    }

    public static Vector3f getObjectBodyScaling(PhysicsCollisionObject physicsRigidBody) {
        Transform transform = new Transform();
        physicsRigidBody.getTransform(transform);
        return new Vector3f(DynamicsUtils.convertV3F_JOML(transform.getScale()));
    }

    public static Vector3f getObjectBodyRot(PhysicsCollisionObject physicsRigidBody) {
        Matrix3f matrix = new Matrix3f();
        physicsRigidBody.getPhysicsRotationMatrix(matrix);

        float[] rotation = new float[3];

        rotation[0] = (float) Math.atan2(matrix.get(1, 2), matrix.get(2, 2));
        rotation[1] = (float) Math.atan2(-matrix.get(0, 2), Math.sqrt(matrix.get(1, 2) * matrix.get(1, 2) + matrix.get(2, 2) * matrix.get(2, 2)));
        rotation[2] = (float) Math.atan2(matrix.get(0, 1), matrix.get(0, 0));

        return new Vector3f(rotation[0], rotation[1], rotation[2]);
    }

    public static IndexedMesh getIndexMesh(float[] pos, int[] ind) {
        return new IndexedMesh(BufferUtils.createFloatBuffer(pos), BufferUtils.createIntBuffer(ind));
    }

    public static CompoundMesh getCompoundMesh(MeshGroup meshGroup) {
        return DynamicsUtils.getCompoundMesh(meshGroup, DefaultPointers.POSITIONS.getIndex());
    }

    public static CompoundMesh getCompoundMesh(MeshGroup meshGroup, int positionsAttributeIndex) {
        CompoundMesh compoundMesh = new CompoundMesh();
        List<IndexedMesh> indexedMeshList = new ArrayList<>();
        for (MeshGroup.Node meshNode : meshGroup.getModelNodeList()) {
            float[] positions = JGemsHelper.UTILS.convertFloatsArray(meshNode.getMesh().tryGetValuesFromAttributeByIndex(positionsAttributeIndex));
            int[] indexes = JGemsHelper.UTILS.convertIntsArray(meshNode.getMesh().getVertexIndexes());
            indexedMeshList.add(DynamicsUtils.getIndexMesh(positions, indexes));
        }
        for (IndexedMesh indexedMesh : indexedMeshList) {
            compoundMesh.add(indexedMesh);
        }
        return compoundMesh;
    }
}
