package javagems3d.graphics.objects.rendering.pipeline.enums;

public enum Redirections {
    SCENE__IN__TRANSPARENCY(Pipeline.SCENE, Pipeline.TRANSPARENCY),
    TRANSPARENCY__IN__SCENE(Pipeline.TRANSPARENCY, Pipeline.SCENE);

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
