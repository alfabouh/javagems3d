package api.scripting.coding.env.internal.util.world.physical.entity.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSColliderConstructor;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSRealJavaStaticPhysicsBodyWrapped", description = "Base class for creating custom static physics bodies in Java with JS exposure.")
public abstract class JSRealJavaStaticPhysicsBodyWrapped extends JGemsStaticBody implements JSWorldItemI {
    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(colliderConstructor.getJavaConstructor(), world.getJavaPhysicsWorld(), pos, rot, scale, itemName);
    }

    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(colliderConstructor, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(colliderConstructor, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    protected JSRealJavaStaticPhysicsBodyWrapped(JSColliderConstructor colliderConstructor, JSPhysicsWorld world, String itemName) {
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

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public JGemsStaticBody getJavaBody() {
        return this;
    }

    @JSHideFromDoc
    @Override
    public IWorldObject getJavaWorldItem() {
        return this;
    }
}