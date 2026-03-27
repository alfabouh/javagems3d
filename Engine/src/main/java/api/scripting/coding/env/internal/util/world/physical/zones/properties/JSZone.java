package api.scripting.coding.env.internal.util.world.physical.zones.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.physics.world.triggers.Zone;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSZone", description = "Axis-aligned trigger zone defined by position and size.")
public class JSZone {

    @JSCodingField(description = "Zone center position")
    private final Vector3f location;

    @JSCodingField(description = "Zone size (width, height, depth)")
    private final Vector3f size;

    public JSZone(JSVector3f location, JSVector3f size) {
        this.location = location.getJavaVector3f();
        this.size = size.getJavaVector3f();
    }

    @JSCodingFunctionOrMethod(description = "Get location")
    public JSVector3f getLocation() {
        return new JSVector3f(new Vector3f(this.location));
    }

    @JSCodingFunctionOrMethod(description = "Get size")
    public JSVector3f getSize() {
        return new JSVector3f(new Vector3f(this.size));
    }

    @JSHideFromDoc
    public Zone getJavaZone() {
        return new Zone(this.location, this.size);
    }
}