package javagems3d.system.external.mapping.tags.base;

import org.jetbrains.annotations.NotNull;

public record TranslationConstraints(AxisConstraints positionConstraints, AxisConstraints rotationConstraints,
                                     AxisConstraints scalingConstraints) {
    public TranslationConstraints(@NotNull AxisConstraints positionConstraints, @NotNull AxisConstraints rotationConstraints, @NotNull AxisConstraints scalingConstraints) {
        this.positionConstraints = positionConstraints;
        this.rotationConstraints = rotationConstraints;
        this.scalingConstraints = scalingConstraints;
    }
}
