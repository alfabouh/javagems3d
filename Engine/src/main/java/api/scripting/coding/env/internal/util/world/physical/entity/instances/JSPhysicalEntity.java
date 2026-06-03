package api.scripting.coding.env.internal.util.world.physical.entity.instances;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSCollisionType;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSEntityState;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSPhysMaterial;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSPhysicalEntity", description = "Advanced physics entity with full control over material, collision and state.")
public class JSPhysicalEntity extends JSWorldItem {
    @JSCodingField(description = "Underlying JGemsBody (Java side)")
    private final JGemsBody body;

    @JSCodingConstructor(description = "Create a physical entity from existing JGemsBody", paramNames = {"world", "body"})
    public JSPhysicalEntity(JSPhysicsWorld world, JGemsBody body) {
        super(world, new JSVector3f(body.getPosition()), new JSVector3f(body.getRotation()), new JSVector3f(body.getScaling()), body.getItemName());
        this.body = body;
    }

    @JSCodingFunctionOrMethod(description = "Get position")
    public JSVector3f getPosition() {
        Vector3f v = this.body.getPosition();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set position", paramNames = {"pos"})
    public void setPosition(JSVector3f pos) {
        this.body.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation")
    public JSVector3f getRotation() {
        Vector3f v = this.body.getRotation();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set rotation", paramNames = {"rot"})
    public void setRotation(JSVector3f rot) {
        this.body.setRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling")
    public JSVector3f getScaling() {
        Vector3f v = this.body.getScaling();
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set scaling", paramNames = {"scale"})
    public void setScaling(JSVector3f scale) {
        this.body.setScaling(scale.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Make body dynamic (default material)")
    public void makeDynamic() {
        this.body.makeDynamic();
    }

    @JSCodingFunctionOrMethod(description = "Make body dynamic with material", paramNames = {"material"})
    public void makeDynamic(JSPhysMaterial material) {
        this.body.makeDynamic(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Make body static")
    public void makeStatic() {
        this.body.makeStatic();
    }

    @JSCodingFunctionOrMethod(description = "Make body static with material", paramNames = {"material"})
    public void makeStatic(JSPhysMaterial material) {
        this.body.makeStatic(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Set physics material", paramNames = {"material"})
    public void setMaterial(JSPhysMaterial material) {
        this.body.setMaterial(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Set collision group", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) arr[i] = types[i].getJavaType();
        this.body.setCollisionGroup(arr);
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) arr[i] = types[i].getJavaType();
        this.body.setCollideWithGroups(arr);
    }

    @JSCodingFunctionOrMethod(description = "Exclude collision types", paramNames = {"types"})
    public void setCollisionFilterNegative(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) arr[i] = types[i].getJavaType();
        this.body.setCollisionFilterNegative(arr);
    }

    @JSCodingFunctionOrMethod(description = "Get entity state")
    public JSEntityState getState() {
        return new JSEntityState(this.body.getEntityState());
    }

    @JSCodingFunctionOrMethod(description = "Set entity state", paramNames = {"state"})
    public void setState(JSEntityState state) {
        this.body.setEntityState(state.getJavaState());
    }

    @JSCodingFunctionOrMethod(description = "Get linear velocity")
    public JSVector3f getVelocity() {
        com.jme3.math.Vector3f v = this.body.getPhysicsRigidBody().getLinearVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set linear velocity", paramNames = {"vel"})
    public void setVelocity(JSVector3f vel) {
        this.body.getPhysicsRigidBody().setLinearVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Add linear velocity", paramNames = {"vel"})
    public void addVelocity(JSVector3f vel) {
        this.body.getPhysicsRigidBody().addLinearVelocity(vel.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Destroy entity")
    public void destroy() {
        this.body.destroy();
    }

    @JSCodingFunctionOrMethod(description = "Check if can be destroyed")
    public boolean canBeDestroyed() {
        return this.body.canBeDestroyed();
    }

    @JSCodingFunctionOrMethod(description = "Set destroyable flag", paramNames = {"flag"})
    public void setCanBeDestroyed(boolean flag) {
        this.body.setCanBeDeleted(flag);
    }

    @JSCodingFunctionOrMethod(description = "Get object id")
    public int getItemId() {
        return this.body.getItemId();
    }

    @JSCodingFunctionOrMethod(description = "Get object name")
    public String getItemName() {
        return this.body.getItemName();
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public JGemsBody getJavaBody() {
        return this.body;
    }
}