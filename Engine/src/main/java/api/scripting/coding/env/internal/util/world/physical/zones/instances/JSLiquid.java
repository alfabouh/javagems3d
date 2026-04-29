package api.scripting.coding.env.internal.util.world.physical.zones.instances;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSTriggerAction;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSZone;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.base.Liquid;

@JSCodingClass(binding = "JSLiquid", description = "Wrapper around liquid object.")
public class JSLiquid {
    @JSCodingField(description = "Underlying liquid (Java side)")
    private final Liquid liquid;

    @JSCodingConstructor(description = "Create a liquid from JSZone", paramNames = {"zone"})
    public JSLiquid(JSZone zone) {
        this.liquid = new Liquid(zone.getJavaZone()) {
            @Override
            public void setDead() {
                // default empty
            }

            @Override
            public boolean isDead() {
                return false;
            }

            @Override
            protected void onEntityEnteredLiquid(Object e) {
                // default empty
            }
        };
    }

    @JSCodingConstructor(description = "Wrap existing Liquid instance", paramNames = {"liquid"})
    public JSLiquid(Liquid liquid) {
        this.liquid = liquid;
    }

    @JSCodingFunctionOrMethod(description = "Get zone")
    public JSZone getZone() {
        Zone z = this.liquid.getZone();
        return new JSZone(new JSVector3f(z.location()), new JSVector3f(z.size()));
    }

    @JSCodingFunctionOrMethod(description = "Set enter callback", paramNames = {"action"})
    public void setActionOnEnterLiquid(JSTriggerAction action) {
        this.liquid.getSimpleTriggerZone().setTriggerAction(action != null ? action.toJava() : null);
    }

    @JSCodingFunctionOrMethod(description = "Spawn liquid in world", paramNames = {"world"})
    public void spawn(JSPhysicsWorld world) {
        this.liquid.onSpawn(world.getJavaPhysicsWorld());
    }

    @JSCodingFunctionOrMethod(description = "Destroy liquid", paramNames = {"world"})
    public void destroy(JSPhysicsWorld world) {
        this.liquid.onDestroy(world.getJavaPhysicsWorld());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java Liquid object")
    public Liquid getJavaLiquid() {
        return this.liquid;
    }
}