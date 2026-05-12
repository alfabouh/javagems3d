package javagems3d.graphics.objects.rendering.pipeline.enums;

public enum Redirections {
    SOLID_SCENE__IN__TRANSPARENCY(Pipeline.SOLID_SCENE, Pipeline.TRANSPARENCY),
    TRANSPARENCY__IN__SOLID_SCENE(Pipeline.TRANSPARENCY, Pipeline.SOLID_SCENE);

    private final Pipeline from;
    private final Pipeline to;

    Redirections(Pipeline from, Pipeline to) {
        this.from = from;
        this.to = to;
    }

    public Pipeline getFrom() {
        return this.from;
    }

    public Pipeline getTo() {
        return this.to;
    }
}
