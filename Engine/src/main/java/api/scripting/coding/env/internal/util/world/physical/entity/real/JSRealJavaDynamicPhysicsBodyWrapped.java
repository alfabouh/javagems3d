package api.scripting.coding.env.internal.util.world.physical.entity.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSColliderConstructor;
import com.jme3.bullet.objects.PhysicsRigidBody;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.wrappers.BulletBody;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSRealJavaDynamicPhysicsBodyWrapped", description = "Base class for creating custom dynamic physics bodies in Java with JS exposure.")
public abstract class JSRealJavaDynamicPhysicsBodyWrapped extends JGemsDynamicBody implements JSWorldItemI {
    protected JSRealJavaDynamicPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(colliderConstructor.getJavaConstructor(), world.getJavaPhysicsWorld(), pos, rot, scale, itemName);
    }

    protected JSRealJavaDynamicPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(colliderConstructor, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    protected JSRealJavaDynamicPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(colliderConstructor, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    protected JSRealJavaDynamicPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, String itemName) {
        this(colliderConstructor, world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @JSCodingFunctionOrMethod(description = "Called every tick (override in JS)")
    protected void onTick(JSPhysicsWorld world) {
    }

    @Override
    protected void onTick(IWorld iWorld) {
        super.onTick(iWorld);

        if (iWorld instanceof PhysicsWorld physicsWorld) {
            this.onTick(new JSPhysicsWorld(physicsWorld));
        }
    }

    @JSCodingFunctionOrMethod(description = "Apply impulse instantly", paramNames = {"impulse"})
    public void applyImpulse(JSVector3f impulse) {
        this.getPhysicsRigidBody().applyCentralImpulse(DynamicsUtils.convertV3F_JME(impulse.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Apply continuous force", paramNames = {"force"})
    public void applyForce(JSVector3f force) {
        this.getPhysicsRigidBody().applyCentralForce(DynamicsUtils.convertV3F_JME(force.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Get velocity")
    public JSVector3f getVelocity() {
        com.jme3.math.Vector3f v = this.getPhysicsRigidBody().getLinearVelocity(new com.jme3.math.Vector3f());
        return new JSVector3f(v.x, v.y, v.z);
    }

    @JSCodingFunctionOrMethod(description = "Set velocity", paramNames = {"vel"})
    public void setVelocity(JSVector3f vel) {
        this.getPhysicsRigidBody().setLinearVelocity(DynamicsUtils.convertV3F_JME(vel.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public JGemsDynamicBody getJavaBody() {
        return this;
    }

    @JSHideFromDoc
    @Override
    public IWorldObject getJavaWorldItem() {
        return this;
    }
}