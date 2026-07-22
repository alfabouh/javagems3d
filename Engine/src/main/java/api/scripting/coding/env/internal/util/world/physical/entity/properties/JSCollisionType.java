package api.scripting.coding.env.internal.util.world.physical.entity.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.physics.entities.properties.collision.CollisionType;

@JSCodingClass(binding = "JSCollisionType", description = "Collision groups and filters")
public enum JSCollisionType {
    PLAYER(CollisionType.PLAYER),
    STATIC_BODY(CollisionType.ST_BODY),
    DYNAMIC_BODY(CollisionType.DN_BODY),
    GHOST(CollisionType.GHOST),
    LIQUID(CollisionType.LIQUID),
    NOTHING(CollisionType.NOTHING),
    UNIVERSAL(CollisionType.UNIVERSAL);

    private final CollisionType type;

    JSCollisionType(CollisionType type) {
        this.type = type;
    }

    @JSCodingFunctionOrMethod(description = "Get mask")
    public int getMask() {
        return this.type.mask();
    }

    @JSCodingFunctionOrMethod(description = "Check mask", paramNames = {"mask"})
    public boolean match(int mask) {
        return this.type.matchMask(mask);
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public CollisionType getJavaType() {
        return this.type;
    }
}