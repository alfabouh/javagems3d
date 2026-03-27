package api.scripting.coding.env.internal.util.world.physical.zones.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSTriggerAction;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSZone;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.base.Liquid;

@JSCodingClass(binding = "JSRealLiquid", description = "Liquid with JS-defined behavior (inheritance-based).")
public abstract class JSRealLiquid extends Liquid implements JSWorldItemI {
    @JSCodingField(description = "Trigger callback")
    private ITriggerAction action;

    @JSCodingField(description = "Indicates whether liquid is dead")
    private boolean dead;

    public JSRealLiquid(JSZone zone) {
        super(zone.getJavaZone());
    }

    @Override
    protected void onEntityEnteredLiquid(Object e) {
        if (this.action != null) {
            this.action.action(e);
        }
    }

    @JSCodingFunctionOrMethod(description = "Set enter callback", paramNames = {"action"})
    public void setAction(JSTriggerAction action) {
        this.action = action != null ? action.toJava() : null;
    }

    @JSCodingFunctionOrMethod(description = "Get zone")
    public JSZone getZoneJS() {
        Zone z = this.getZone();
        return new JSZone(new JSVector3f(z.location()), new JSVector3f(z.size()));
    }

    @Override
    @JSCodingFunctionOrMethod(description = "Marks liquid as dead")
    public void setDead() {
        this.dead = true;
    }

    @Override
    @JSCodingFunctionOrMethod(description = "Returns whether liquid is dead")
    public boolean isDead() {
        return this.dead;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public Liquid getJavaLiquid() {
        return this;
    }

    @JSHideFromDoc
    @Override
    public IWorldObject getJavaWorldItem() {
        return this;
    }
}