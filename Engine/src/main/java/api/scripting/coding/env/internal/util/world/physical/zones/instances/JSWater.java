package api.scripting.coding.env.internal.util.world.physical.zones.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Water;

@JSCodingClass(binding = "JSWater", description = "Wrapper for Water liquid object")
public class JSWater implements JSWorldObjectI {
    @JSCodingField(description = "Real Water object")
    private final Water water;

    @JSHideFromDoc
    public JSWater(Water water) {
        this.water = water;
    }

    @JSHideFromDoc
    public JSWater(Zone zone) {
        this.water = new Water(zone);
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public Water getJavaWater() {
        return this.water;
    }


    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public IWorldObject getJavaWorldObject() {
        return this.water;
    }
}