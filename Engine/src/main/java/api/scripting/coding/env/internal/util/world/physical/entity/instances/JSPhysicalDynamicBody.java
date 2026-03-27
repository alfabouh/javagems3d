package api.scripting.coding.env.internal.util.world.physical.entity.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSColliderConstructor;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSCollisionType;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSEntityState;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSPhysMaterial;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSPhysicalDynamicBody", description = "Dynamic physics body with full runtime control over movement and forces.")
public class JSPhysicalDynamicBody implements JSWorldItemI {
    @JSCodingField(description = "Underlying dynamic body (Java side)")
    private final JGemsDynamicBody dynamicBody;

    @JSHideFromDoc
    public JSPhysicalDynamicBody(JGemsDynamicBody dynamicBody) {
        this.dynamicBody = dynamicBody;
    }

    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        this.dynamicBody = new JGemsDynamicBody(colliderConstructor.getJavaConstructor(), world.getJavaPhysicsWorld(), pos, rot, scale, itemName);
    }

    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(colliderConstructor, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(colliderConstructor, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public JSPhysicalDynamicBody(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, String itemName) {
        this(colliderConstructor, world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector3f getPosition() {
        Vector3f v = this.dynamicBody.getPosition();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPosition(JSVector3f pos) {
        this.dynamicBody.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public JSVector3f getRotation() {
        Vector3f v = this.dynamicBody.getRotation();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rot"})
    public void setRotation(JSVector3f rot) {
        this.dynamicBody.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling")
    public JSVector3f getScaling() {
        Vector3f v = this.dynamicBody.getScaling();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling", paramNames = {"scale"})
    public void setScaling(JSVector3f scale) {
        this.dynamicBody.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get linear velocity")
    public JSVector3f getVelocity() {
        com.jme3.math.Vector3f v = this.dynamicBody.getPhysicsRigidBody().getLinearVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set linear velocity", paramNames = {"vel"})
    public void setVelocity(JSVector3f vel) {
        this.dynamicBody.getPhysicsRigidBody().setLinearVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Add velocity", paramNames = {"vel"})
    public void addVelocity(JSVector3f vel) {
        this.dynamicBody.getPhysicsRigidBody().addLinearVelocity(vel.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Apply impulse instantly", paramNames = {"impulse"})
    public void applyImpulse(JSVector3f impulse) {
        this.dynamicBody.getPhysicsRigidBody().applyCentralImpulse(DynamicsUtils.convertV3F_JME(impulse.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply continuous force", paramNames = {"force"})
    public void applyForce(JSVector3f force) {
        this.dynamicBody.getPhysicsRigidBody().applyCentralForce(DynamicsUtils.convertV3F_JME(force.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Set physics material", paramNames = {"material"})
    public void setMaterial(JSPhysMaterial material) {
        this.dynamicBody.setMaterial(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Set collision group", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        this.dynamicBody.setCollisionGroup(arr);
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        this.dynamicBody.setCollisionFilter(arr);
    }

    @JSCodingFunctionOrMethod(description = "Get entity state")
    public JSEntityState getState() {
        return new JSEntityState(this.dynamicBody.getEntityState());
    }

    @JSCodingFunctionOrMethod(description = "Destroy body")
    public void destroy() {
        this.dynamicBody.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Get name")
    public String getName() {
        return this.dynamicBody.getItemName();
    }

    @JSCodingFunctionOrMethod(description = "Get id")
    public int getId() {
        return this.dynamicBody.getItemId();
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public JGemsDynamicBody getJavaDynamicBody() {
        return this.dynamicBody;
    }

    @JSHideFromDoc
    @Override
    public WorldItem getJavaWorldItem() {
        return this.dynamicBody;
    }
}