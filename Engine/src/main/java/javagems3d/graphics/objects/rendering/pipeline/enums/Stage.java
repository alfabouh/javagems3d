package javagems3d.graphics.objects.rendering.pipeline.enums;

public enum Stage {
    FORWARD(Type.DIRECT),
    DEFERRED_DIRECT(Type.DIRECT),
    DEFERRED_INDIRECT(Type.INDIRECT),
    SHADOW_DIRECT(Type.DIRECT),
    SHADOW_INDIRECT(Type.INDIRECT),
    TRANSPARENCY_INDIRECT(Type.INDIRECT);

    private final Type type;

    Stage(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
}
