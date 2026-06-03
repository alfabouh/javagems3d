package api.scripting.coding.env.internal.util.world.physical.zones.instances;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSCollisionType;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.ITriggerZoneWithSetter;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSTriggerAction;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSZone;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.zones.base.AbstractTriggerZone;

@JSCodingClass(binding = "JSBasicTriggerZone", description = "Wrapper around trigger zone using composition (no inheritance).")
public class JSBasicTriggerZone implements JSWorldObjectI {
    @JSCodingField(description = "Underlying trigger zone (Java side)")
    private final AbstractTriggerZone zone;

    @JSCodingConstructor(description = "Wrap existing AbstractTriggerZone", paramNames = {"zone"})
    public JSBasicTriggerZone(AbstractTriggerZone zone) {
        this.zone = zone;
    }

    @JSCodingFunctionOrMethod(description = "Get zone")
    public JSZone getZone() {
        Zone z = this.zone.getZone();
        return new JSZone(new JSVector3f(z.location()), new JSVector3f(z.size()));
    }

    @JSCodingFunctionOrMethod(description = "Set zone position", paramNames = {"location"})
    public void setLocation(JSVector3f location) {
        this.zone.setLocation(location.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Set collision group", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        this.zone.setCollisionGroup(arr);
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        this.zone.setCollideWithGroups(arr);
    }

    @JSCodingFunctionOrMethod(description = "Get trigger action")
    public JSTriggerAction getAction() {
        ITriggerAction action = this.zone.onColliding();
        return action != null ? action::action : null;
    }

    @JSCodingFunctionOrMethod(description = "Set trigger callback", paramNames = {"action"})
    public void setAction(JSTriggerAction action) {
        if (this.zone instanceof ITriggerZoneWithSetter z) {
            z.setTriggerAction(action != null ? action.toJava() : null);
        }
    }

    @JSCodingFunctionOrMethod(description = "Check if trigger is valid")
    public boolean isValid() {
        return this.zone.isValid();
    }

    @JSCodingFunctionOrMethod(description = "Spawn trigger in world", paramNames = {"world"})
    public void spawn(JSPhysicsWorld world) {
        this.zone.onSpawn(world.getJavaPhysicsWorld());
    }

    @JSCodingFunctionOrMethod(description = "Destroy trigger", paramNames = {"world"})
    public void destroy(JSPhysicsWorld world) {
        this.zone.onDestroy(world.getJavaPhysicsWorld());
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public AbstractTriggerZone getJavaZone() {
        return this.zone;
    }

    @Override
    public IWorldObject getJavaWorldObject() {
        return this.zone;
    }
}