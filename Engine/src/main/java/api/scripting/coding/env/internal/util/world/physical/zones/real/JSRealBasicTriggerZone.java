package api.scripting.coding.env.internal.util.world.physical.zones.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSCollisionType;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSTriggerAction;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSZone;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.zones.AbstractTriggerZone;

@JSCodingClass(binding = "JSRealBasicTriggerZone", description = "Concrete trigger zone with JS callback support.")
public abstract class JSRealBasicTriggerZone extends AbstractTriggerZone implements JSWorldObjectI {
    @JSCodingField(description = "Trigger callback") private ITriggerAction action;

    public JSRealBasicTriggerZone(JSZone zone) {
        super(zone.getJavaZone());
    }

    @Override
    public ITriggerAction collisionTriggerFunc() {
        return this.action;
    }

    @JSCodingFunctionOrMethod(description = "Set trigger callback", paramNames = {"action"})
    public void setAction(JSTriggerAction action) {
        this.action = action != null ? action.toJava() : null;
    }

    @JSCodingFunctionOrMethod(description = "Get zone")
    public JSZone getJSZone() {
        Zone z = this.getZone();
        return new JSZone(new JSVector3f(z.location()), new JSVector3f(z.size()));
    }

    @JSCodingFunctionOrMethod(description = "Set zone position", paramNames = {"location"})
    public void setLocation(JSVector3f location) {
        super.setLocation(location.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Set collision group", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        super.setCollisionGroup(arr);
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        super.setCollideWithGroups(arr);
    }

    @JSCodingFunctionOrMethod(description = "Check if trigger is active")
    public boolean isValid() {
        return this.collisionTriggerFunc() != null;
    }

    @JSCodingFunctionOrMethod(description = "Destroy trigger")
    public void destroy(JSPhysicsWorld world) {
        this.onDestroy(world.getJavaPhysicsWorld());
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public AbstractTriggerZone getJavaTriggerZone() {
        return this;
    }

    @JSHideFromDoc
    @Override
    public IWorldObject getJavaWorldObject() {
        return this;
    }
}