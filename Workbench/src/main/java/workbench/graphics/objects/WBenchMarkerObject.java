package workbench.graphics.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchMarkerObject extends WBenchObject {
    private final Vector3f color;
    private final boolean transparent;

    public WBenchMarkerObject(@NotNull WBenchWorld wBenchWorld, @NotNull WBenchObjectTemplate objectTemplate, @NotNull Vector3f color, boolean transparent) {
        super(wBenchWorld, objectTemplate);
        this.color = color;
        this.transparent = transparent;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public Vector3f getColor() {
        return this.color;
    }
}
